///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: SQLInteger.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2001 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;



class SQLInteger
implements SQLData
{
  private static final String copyright = "Copyright (C) 1997-2001 International Business Machines Corporation and others.";




    // Private data.
    private int                 truncated_;
    // @D0D private static AS400Bin4    typeConverter_;
    private int                 value_;
    private int                 scale_;                              // @A0A
    private BigDecimal          bigDecimalValue_ = null;             // @A0A



    // @D0D static
    // @D0D {
    // @D0D     typeConverter_ = new AS400Bin4 ();
    // @D0D }



    SQLInteger ()
    {
        this (0);
    }



    SQLInteger (int scale)                     // @A0A
    {
        truncated_          = 0;
        value_              = 0;
        scale_              = scale;                                      // @A0A
        if (scale_ > 0)                                                   // @C0A
            bigDecimalValue_    = new BigDecimal (Integer.toString (value_)); // @A0A
    }



    public Object clone ()
    {
        return new SQLInteger (scale_);
    }



//---------------------------------------------------------//
//                                                         //
// CONVERSION TO AND FROM RAW BYTES                        //
//                                                         //
//---------------------------------------------------------//



    public void convertFromRawBytes (byte[] rawBytes, int offset, ConvTable ccsidConverter) //@P0C
        throws SQLException
    {
        value_ = BinaryConverter.byteArrayToInt(rawBytes, offset);                               // @D0C

        if (scale_ > 0) {                                                                        // @C0A
            bigDecimalValue_ = (new BigDecimal(Integer.toString(value_))).movePointLeft(scale_); // @A0A
            value_ = bigDecimalValue_.intValue();                                                // @A0A
        }                                                                                        // @C0A
    }



    public void convertToRawBytes (byte[] rawBytes, int offset, ConvTable ccsidConverter) //@P0C
        throws SQLException
    {
        BinaryConverter.intToByteArray(value_, rawBytes, offset);                                // @D0C
    }



//---------------------------------------------------------//
//                                                         //
// SET METHODS                                             //
//                                                         //
//---------------------------------------------------------//



    public void set (Object object, Calendar calendar, int scale)
        throws SQLException
    {
        truncated_ = 0;                                                     // @D9c

        if (object instanceof String)
        {
            // @D10c new implementation
            // old ...
            //
            // try
            // {
            //     value_ = Integer.parseInt ((String) object);
            // }
            // catch (NumberFormatException e)
            // {
            //     JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
            // }
            //
            // new ...

            // @P1 First try to convert the string to an int (no extra object creation).  If
            //     that fails try turning it into a Double, which will involve an extra object
            //     create but Double will accept bigger numbers and floating point numbers so it 
            //     will catch more truncation cases.  The bottom line is don't create an extra
            //     object in the normal case.  If the user does ps.setString(1, "111222333.444.555")
            //     on an integer field, they can't expect the best performance. 
            boolean tryAgain = false;                                                    // @P1a

            try
            {
               // @P1d long longValue = (long) Double.parseDouble ((String) object); 
               long longValue = (long) Long.parseLong ((String) object);              // @P1a

               if (( longValue > Integer.MAX_VALUE ) || ( longValue < Integer.MIN_VALUE ))
               {
                   truncated_ = 4;                                                           // @D9c
               }
               value_ = (int) longValue;
            }
            catch (NumberFormatException e)
            {
               tryAgain = true;                                                          // @P1a
               // @P1d JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
            }

            if (tryAgain)                                                                // @P1a
            {                                                                            // @P1a
               try                                                                       // @P1a
               {                                                                         // @P1a
                  double doubleValue = Double.valueOf ((String) object).doubleValue ();  // @P1a
                                                                                         // @P1a
                  if (( doubleValue > Short.MAX_VALUE ) || ( doubleValue < Short.MIN_VALUE )) // @P1a
                  {                                                                      // @P1a
                      truncated_ = 6;                                                    // @P1a
                  }                                                                      // @P1a
                  value_ = (short) doubleValue;                                          // @P1a  
               }                                                                         // @P1a
               catch (NumberFormatException e)                                           // @P1a
               {                                                                         // @P1a
                  JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);            // @P1a
               }                                                                         // @P1a
            }                                                                            // @P1a
        }

        else if (object instanceof Number)
        {
            // Compute truncation by getting the value as a long
            // and comparing it against MAX_VALUE/MIN_VALUE.  You
            // do this because truncation of the decimal portion of
            // the value is insignificant.  We only care if the
            // whole number portion of the value is too large/small
            // for the column.
            long longValue = ((Number) object).longValue ();                              // @D9c
            if (( longValue > Integer.MAX_VALUE ) || ( longValue < Integer.MIN_VALUE ))   // @D9c
            {
                // Note:  Truncated here is set to 4 bytes.  This is based on
                //        the idea that a long was used and an int was the
                //        column type.  We could check for different types
                //        and provide a more accurate number, but I don't
                //        really know that this field is of any use to people
                //        in this case anyway (for example, you could have a
                //        float (4 bytes) that didn't fit into a bigint (8
                //        bytes) without some data truncation.
                truncated_ = 4;                                                           // @D9c
            }

            // Store the value.
            value_ = (int) longValue;                                                     // @D9c
        }

        else if (object instanceof Boolean)
            value_ = (((Boolean) object).booleanValue() == true) ? 1 : 0;

        else
            JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);

        if (scale_ > 0) {                                                                        // @C0A
            bigDecimalValue_ = (new BigDecimal(Integer.toString(value_))).movePointLeft(scale_); // @A0A
            value_ = bigDecimalValue_.intValue();                                                // @A0A
        }                                                                                        // @C0A
    }


    public void set(int value)                                                          // @E2A
    {                                                                                   // @E2A
        value_ = value;                                                                 // @E2A
    }                                                                                   // @E2A


//---------------------------------------------------------//
//                                                         //
// DESCRIPTION OF SQL TYPE                                 //
//                                                         //
//---------------------------------------------------------//



    public String getCreateParameters ()
    {
        return null;
    }


    public int getDisplaySize ()
    {
        return 11;
    }


    //@F1A JDBC 3.0
    public String getJavaClassName()
    {
        return "java.lang.Integer";
    }


    public String getLiteralPrefix ()
    {
        return null;
    }


    public String getLiteralSuffix ()
    {
        return null;
    }



    public String getLocalName ()
    {
        return "INTEGER";
    }


    public int getMaximumPrecision ()
    {
        return 10;
    }


    public int getMaximumScale ()
    {
        return 0;
    }


    public int getMinimumScale ()
    {
        return 0;
    }


    public int getNativeType ()
    {
        return 496;
    }


    public int getPrecision ()
    {
        return 10;
    }


    public int getRadix ()
    {
        return 10;
    }



    public int getScale ()
    {
        return scale_;
    }


     public int getType ()
     {
          return java.sql.Types.INTEGER;
     }


     public String getTypeName ()
     {
          return "INTEGER";
     }


// @E1D    public boolean isGraphic ()
// @E1D    {
// @E1D        return false;
// @E1D    }



    public boolean isSigned ()
    {
        return true;
    }



    public boolean isText ()
    {
        return false;
    }



//---------------------------------------------------------//
//                                                         //
// CONVERSIONS TO JAVA TYPES                               //
//                                                         //
//---------------------------------------------------------//



    public int getActualSize ()
    {
        return 4; // @D0C
    }



    public int getTruncated ()
    {
        return truncated_;
    }



     public InputStream toAsciiStream ()
         throws SQLException
     {
          JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public BigDecimal toBigDecimal (int scale)
         throws SQLException
     {
        if (scale_ > 0) {                                                   // @C0A
             if (scale >= 0)
                return bigDecimalValue_.setScale(scale);                    // @A0A
            else
                return bigDecimalValue_;
        }                                                                   // @C0A
        else {                                                              // @C0A
            if (scale <= 0)                                                 // @C0A
                return BigDecimal.valueOf ((long) value_);                  // @C0A
            else                                                            // @C0A
                return BigDecimal.valueOf ((long) value_).setScale (scale); // @C0A
        }                                                                   // @C0A
     }



     public InputStream toBinaryStream ()
         throws SQLException
     {
          JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public Blob toBlob ()
         throws SQLException
     {
          JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public boolean toBoolean ()
         throws SQLException
     {
         return (value_ != 0);
     }



     public byte toByte ()
         throws SQLException
     {
         return (byte) value_;
     }



     public byte[] toBytes ()
         throws SQLException
     {
         JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public Reader toCharacterStream ()
         throws SQLException
     {
          JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public Clob toClob ()
         throws SQLException
     {
          JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public Date toDate (Calendar calendar)
         throws SQLException
     {
         JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public double toDouble ()
         throws SQLException
     {
        if (scale_ > 0)                                 // @C0A
            return bigDecimalValue_.doubleValue();      // @A0A
        else                                            // @C0A
            return (double) value_;                     // @A0D @C0A
     }



     public float toFloat ()
         throws SQLException
     {
        if (scale_ > 0)                                 // @C0A
            return bigDecimalValue_.floatValue();       // @A0A
        else                                            // @C0A
            return (float) value_;                      // @A0D @C0A
     }



     public int toInt ()
         throws SQLException
     {
         return (int) value_;
     }



     public long toLong ()
         throws SQLException
     {
         return value_;
     }



     public Object toObject ()
     {
         return new Integer ((int) value_);
     }



     public short toShort ()
         throws SQLException
     {
         return (short) value_;
     }



     public String toString ()
     {
        if (scale_ > 0)                                 // @C0A
            return bigDecimalValue_.toString();         // @A0A
        else                                            // @C0A
            return Integer.toString (value_);           // @A0D @C0A
     }



     public Time toTime (Calendar calendar)
         throws SQLException
     {
         JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public Timestamp toTimestamp (Calendar calendar)
         throws SQLException
     {
         JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



     public InputStream  toUnicodeStream ()
         throws SQLException
     {
         JDError.throwSQLException (JDError.EXC_DATA_TYPE_MISMATCH);
          return null;
     }



}
