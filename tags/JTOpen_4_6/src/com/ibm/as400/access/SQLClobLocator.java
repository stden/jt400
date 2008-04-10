///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: SQLClobLocator.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2003 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;


final class SQLClobLocator implements SQLLocator
{
    private static final String copyright = "Copyright (C) 1997-2003 International Business Machines Corporation and others.";

    private AS400JDBCConnection     connection_;
    private SQLConversionSettings   settings_;
    private ConvTable               converter_;
    private int                     id_;
    private JDLobLocator            locator_;
    private int                     maxLength_;
    private int                     truncated_;
    private int                     columnIndex_;

    private Object savedObject_; // This is the AS400JDBCBlobLocator or InputStream or whatever got set into us.
    private int scale_; // This is actually the length that got set into us.

    SQLClobLocator(AS400JDBCConnection connection,
                   int id,
                   int maxLength, 
                   SQLConversionSettings settings,
                   ConvTable converter,
                   int columnIndex)
    {
        connection_     = connection;
        id_             = id;
        locator_        = new JDLobLocator(connection, id, maxLength, false);
        maxLength_      = maxLength;
        truncated_      = 0;
        settings_       = settings;
        converter_      = converter;
        columnIndex_    = columnIndex;
    }

    public Object clone()
    {
        return new SQLClobLocator(connection_, id_, maxLength_, settings_, converter_, columnIndex_);
    }

    public void setHandle(int handle)
    {
        locator_.setHandle(handle);
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSION TO AND FROM RAW BYTES                        //
    //                                                         //
    //---------------------------------------------------------//

    public void convertFromRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter)
    throws SQLException
    {
        int locatorHandle = BinaryConverter.byteArrayToInt(rawBytes, offset);
        locator_.setHandle(locatorHandle);
        locator_.setColumnIndex(columnIndex_);
    }

    //@CRS - This is only called from AS400JDBCPreparedStatement in one place.
    public void convertToRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter)
    throws SQLException
    {
        BinaryConverter.intToByteArray(locator_.getHandle(), rawBytes, offset);

        // Now we write our saved data to the server, because the prepared statement is being executed.
        // We used to write the data to the server on the call to set(), but this messed up
        // batch executes, since the host server only reserves temporary space for locator handles one row at a time.
        // See the toObject() method in this class for more details.
        if(savedObject_ != null) writeToServer();
    }

    //---------------------------------------------------------//
    //                                                         //
    // SET METHODS                                             //
    //                                                         //
    //---------------------------------------------------------//

    public void set(Object object, Calendar calendar, int scale)
    throws SQLException
    {
        if(!(object instanceof String) &&
           !(object instanceof Reader) &&
           !(object instanceof InputStream) &&
           (JDUtilities.JDBCLevel_ >= 20 && !(object instanceof Clob)))
        {
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        }

        savedObject_ = object;
        if(scale != -1) scale_ = scale; // Skip resetting it if we don't know the real length
    }

    private void writeToServer()
    throws SQLException
    {
        try
        {
            Object object = savedObject_;
            if(object instanceof String)
            {
                String string = (String)object;
                int bidiStringType = settings_.getBidiStringType();
                if(bidiStringType == -1) bidiStringType = converter_.bidiStringType_;

                BidiConversionProperties bidiConversionProperties = new BidiConversionProperties(bidiStringType);  //@KBA
                bidiConversionProperties.setBidiImplicitReordering(settings_.getBidiImplicitReordering());         //@KBA
                bidiConversionProperties.setBidiNumericOrderingRoundTrip(settings_.getBidiNumericOrdering());      //@KBA

                byte[] bytes = converter_.stringToByteArray(string, bidiConversionProperties);  //@KBC changed to use bidiConversionProperties instead of bidiStringType
                locator_.writeData(0L, bytes, true);                //@K1C
            }
            else if(object instanceof Reader)
            {
                int length = scale_; // hack to get the length into the set method
                // Need to write even if there are 0 bytes in case we are batching and
                // the host server reuses the same handle for the previous locator; otherwise,
                // we'll have data in the current row from the previous row.
                if (length == 0) 
                {
                  locator_.writeData(0, new byte[0], 0, 0, true);               //@K1C
                }
                else if(length > 0)
                {
                    try
                    {
                        int blockSize = length < AS400JDBCPreparedStatement.LOB_BLOCK_SIZE ? length : AS400JDBCPreparedStatement.LOB_BLOCK_SIZE;
                        int bidiStringType = settings_.getBidiStringType();
                        if(bidiStringType == -1) bidiStringType = converter_.bidiStringType_;

                        BidiConversionProperties bidiConversionProperties = new BidiConversionProperties(bidiStringType);  //@KBA
                        bidiConversionProperties.setBidiImplicitReordering(settings_.getBidiImplicitReordering());         //@KBA
                        bidiConversionProperties.setBidiNumericOrderingRoundTrip(settings_.getBidiNumericOrdering());      //@KBA

                        ReaderInputStream stream = new ReaderInputStream((Reader)savedObject_, converter_.getCcsid(), bidiConversionProperties, blockSize); //@KBC changed to use bidiConversionProperties instead of bidiStringType
                        byte[] byteBuffer = new byte[blockSize];
                        int totalBytesRead = 0;
                        int bytesRead = stream.read(byteBuffer, 0, blockSize);
                        while(bytesRead > -1 && totalBytesRead < length)
                        {
                            locator_.writeData((long)totalBytesRead, byteBuffer, 0, bytesRead, true); // totalBytesRead is our offset.  @K1C
                            totalBytesRead += bytesRead;
                            int bytesRemaining = length - totalBytesRead;
                            if(bytesRemaining < blockSize)
                            {
                                blockSize = bytesRemaining;
                                if(stream.available() == 0 && blockSize != 0)
                                {
                                    stream = new ReaderInputStream((Reader)savedObject_, converter_.getCcsid(), bidiConversionProperties, blockSize); // do this so we don't read more chars out of the Reader than we have to. //@KBC changed to use bidiConversionProperties instead of bidiStringType
                                }
                            }
                            bytesRead = stream.read(byteBuffer, 0, blockSize);
                        }

                        if(totalBytesRead < length)
                        {
                            // a length longer than the stream was specified
                            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
                        }

                    }
                    catch(IOException ie)
                    {
                        JDError.throwSQLException(this, JDError.EXC_INTERNAL, ie);
                    }
                }
                else
                {
                    JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
                }
            }
            else if(object instanceof InputStream)
            {
                int length = scale_; // hack to get the length into the set method
                // Need to write even if there are 0 bytes in case we are batching and
                // the host server reuses the same handle for the previous locator; otherwise,
                // we'll have data in the current row from the previous row.
                if (length == 0) 
                {
                  locator_.writeData(0, new byte[0], 0, 0, true);           //@K1C
                }
                else if(length > 0)
                {
                    InputStream stream = (InputStream)savedObject_;
                    int blockSize = length < AS400JDBCPreparedStatement.LOB_BLOCK_SIZE ? length : AS400JDBCPreparedStatement.LOB_BLOCK_SIZE;
                    byte[] byteBuffer = new byte[blockSize];
                    try
                    {
                        int totalBytesRead = 0;
                        int bytesRead = stream.read(byteBuffer, 0, blockSize);
                        while(bytesRead > -1 && totalBytesRead < length)
                        {
                            locator_.writeData((long)totalBytesRead, byteBuffer, 0, bytesRead, true); // totalBytesRead is our offset.  @K1C
                            totalBytesRead += bytesRead;
                            int bytesRemaining = length - totalBytesRead;
                            if(bytesRemaining < blockSize)
                            {
                                blockSize = bytesRemaining;
                            }
                            bytesRead = stream.read(byteBuffer, 0, blockSize);
                        }

                        if(totalBytesRead < length)
                        {
                            // a length longer than the stream was specified
                            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
                        }

                    }
                    catch(IOException ie)
                    {
                        JDError.throwSQLException(this, JDError.EXC_INTERNAL, ie);
                    }
                }
                else
                {
                    JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
                }
            }
            else if(JDUtilities.JDBCLevel_ >= 20 && object instanceof Clob) //@H0A check for jdbc level to know if lobs exist
            {
                //@G5A Start new code for updateable locator case
                boolean set = false;
                if(object instanceof AS400JDBCClobLocator)
                {
                    AS400JDBCClobLocator clob = (AS400JDBCClobLocator)object;

                    //Synchronize on a lock so that the user can't keep making updates
                    //to the clob while we are taking updates off the vectors.
                    synchronized(clob)
                    {
                        // See if we saved off our real object from earlier.
                        if(clob.savedObject_ != null)
                        {
                            savedObject_ = clob.savedObject_;
                            scale_ = clob.savedScale_;
                            clob.savedObject_ = null;
                            writeToServer();
                            return;
                        }
                    }
                }

                //@G5A If the code for updateable lob locators did not run, then run old code.
                if(!set)
                {
                    Clob clob = (Clob)object;
                    int length = (int)clob.length();
                    String substring = clob.getSubString(1, length);
                    locator_.writeData(0L, converter_.stringToByteArray(substring), 0, length, true);           //@K1C
                    set = true;
                }
                else
                {
                    JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
                }
            }
            else
            {
                JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
            }
        }
        finally
        {
            savedObject_ = null;
        }
        scale_ = (int)locator_.getLength();
    }

    //---------------------------------------------------------//
    //                                                         //
    // DESCRIPTION OF SQL TYPE                                 //
    //                                                         //
    //---------------------------------------------------------//

    public int getSQLType()
    {
        return SQLData.CLOB_LOCATOR;
    }

    public String getCreateParameters()
    {
        return AS400JDBCDriver.getResource("MAXLENGTH"); 
    }

    public int getDisplaySize()
    {
        return maxLength_;
    }

    public String getJavaClassName()
    {
        return "com.ibm.as400.access.AS400JDBCClobLocator";   
    }

    public String getLiteralPrefix()
    {
        return null;
    }

    public String getLiteralSuffix()
    {
        return null;
    }

    public String getLocalName()
    {
        return "CLOB"; 
    }

    public int getMaximumPrecision()
    {
        return 2147483646; // the DB2 SQL reference says this should be 2147483647 but we return 1 less to allow for NOT NULL columns
    }

    public int getMaximumScale()
    {
        return 0;
    }

    public int getMinimumScale()
    {
        return 0;
    }

    public int getNativeType()
    {
        return 964;        
    }

    public int getPrecision()
    {
        return maxLength_;
    }

    public int getRadix()
    {
        return 0;
    }

    public int getScale()
    {
        return 0;
    }

    public int getType()
    {
        return java.sql.Types.CLOB;
    }

    public String getTypeName()
    {
        return "CLOB";
    }

    public boolean isSigned()
    {
        return false;
    }

    public boolean isText()
    {
        return true;
    }

    public int getActualSize()
    {
        return maxLength_;
    }

    public int getTruncated()
    {
        return truncated_;
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSIONS TO JAVA TYPES                               //
    //                                                         //
    //---------------------------------------------------------//

    public InputStream getAsciiStream()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return new ReaderInputStream(new ConvTableReader(new AS400JDBCInputStream(new JDLobLocator(locator_)), converter_.getCcsid()), 819); // ISO-8859-1.
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
            return null;
        }
    }

    public BigDecimal getBigDecimal(int scale)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public InputStream getBinaryStream()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return new HexReaderInputStream(new ConvTableReader(new AS400JDBCInputStream(new JDLobLocator(locator_)), converter_.getCcsid()));
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
            return null;
        }
    }

    public Blob getBlob()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            byte[] bytes = BinaryConverter.stringToBytes(getString());
            return new AS400JDBCBlob(bytes, bytes.length);
        }
        catch(NumberFormatException nfe)
        {
            // this Clob contains non-hex characters
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            return null;
        }
    }

    public boolean getBoolean()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return false;
    }

    public byte getByte()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public byte[] getBytes()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return BinaryConverter.stringToBytes(getString());
        }
        catch(NumberFormatException nfe)
        {
            // this Clob contains non-hex characters
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, nfe);
            return null;
        }
    }

    public Reader getCharacterStream()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return new ConvTableReader(new AS400JDBCInputStream(new JDLobLocator(locator_)), converter_.getCcsid());
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
            return null;
        }
    }

    public Clob getClob()
    throws SQLException
    {
        truncated_ = 0;
        return new AS400JDBCClobLocator(new JDLobLocator(locator_), converter_, savedObject_, scale_);        
    }

    public Date getDate(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public double getDouble()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public float getFloat()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public int getInt()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public long getLong()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public Object getObject()
    throws SQLException
    {
        truncated_ = 0;
        // getObject is used by AS400JDBCPreparedStatement for batching, so we save off our InputStream
        // inside the AS400JDBCClobLocator. Then, when convertToRawBytes() is called, the writeToServer()
        // code checks the AS400JDBCClobLocator's saved InputStream... if it exists, then it writes the
        // data out of the InputStream to the server by calling writeToServer() again.
        return new AS400JDBCClobLocator(new JDLobLocator(locator_), converter_, savedObject_, scale_);
    }

    public short getShort()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return 0;
    }

    public String getString()
    throws SQLException
    {
        DBLobData data = locator_.retrieveData(0, locator_.getMaxLength());
        String value = converter_.byteArrayToString(data.getRawBytes(),
                                                    data.getOffset(),
                                                    data.getLength());
        return value;
    }

    public Time getTime(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public Timestamp getTimestamp(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public InputStream getUnicodeStream()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return new ReaderInputStream(new ConvTableReader(new AS400JDBCInputStream(new JDLobLocator(locator_)), converter_.getCcsid()), 13488);
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(JDError.EXC_INTERNAL, e);
            return null;
        }
    }
}
