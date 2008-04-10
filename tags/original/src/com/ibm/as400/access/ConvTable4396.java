///////////////////////////////////////////////////////////////////////////////
//                                                                             
// AS/400 Toolbox for Java - OSS version                                       
//                                                                             
// Filename: ConvTable4396.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.UnsupportedEncodingException;

// Table for CCSID 4396
class ConvTable4396 extends ConvTableRedundant
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

    static private String Copyright()
    {
	return Copyright.copyright;
    }

    private final static int ccsid = 4396;
    private ConvTable table;

    ConvTable4396()
    {
	// CCSID 4396 is the DBCS portion of the mixed byte CCSID's 5026 & 5035 and a subset of CCSID 300 which is the DBCS portion of the mixed byte CCSID's 930 &939
	super();
	if (ConvTable.convDebug) Trace.log(Trace.CONVERSION, "Constructing Conversion Table for CCSID: 4396 (using CCSID 930)");
    }

    void setSystem(AS400ImplRemote system) throws UnsupportedEncodingException
    {
	this.table = ConvTable.getTable(930, system);
    }

    // Returns the ccsid of this conversion object.
    // return  the ccsid.
    int getCcsid()
    {
	return ccsid;
    }

    // Returns the encoding of this conversion object.
    // return  the encoding.
    String getEncoding()
    {
	return String.valueOf(ccsid);
    }

    // use the 930 table ofter adding shift-out/shift-in
    String byteArrayToString(byte[] source, int offset, int length)
    {
	if (ConvTable.convDebug)
	{
	    Trace.log(Trace.CONVERSION, "Converting byte to char for CCSID: 4396 (will use CCSID 930)", source, offset, length);
	}
	byte[] newSource = new byte[length+2];
	newSource[0] = 0x0E;
	newSource[length+1] = 0x0F;
	System.arraycopy(source, offset, newSource, 1, length);
	return table.byteArrayToString(newSource, 0, length+2);
    }

    // use 930 table, then remove shift-out/shift-in
    byte[] stringToByteArray(String source)
    {
	byte[] iDest = table.stringToByteArray(source);
	if (iDest.length < 2) return new byte[0];
	byte[] dest = new byte[iDest.length-2];
	System.arraycopy(iDest, 1, dest, 0, dest.length);
	if (ConvTable.convDebug)
	{
	    Trace.log(Trace.CONVERSION, "Char to byte output (for CCSID 4396): ", dest);
	}
	return dest;
    }
}