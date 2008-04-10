///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: JDLocalNameFieldMap.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2003 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.sql.SQLException;

class JDLocalNameFieldMap
extends JDTypeInfoFieldMap
implements JDFieldMap
{
    private static final String copyright = "Copyright (C) 1997-2003 International Business Machines Corporation and others.";

    JDLocalNameFieldMap(int typeIndex, int lengthIndex, int precisionIndex, 
                        int scaleIndex, int vrm, JDProperties properties)            // @MOC - added vrm and properties
    {
        super(typeIndex, lengthIndex, precisionIndex, scaleIndex, vrm, properties);  // @M0C
    }

    /**
        Returns the data type in JDBC format.
    **/
    public Object getValue(JDRow row)
    throws SQLException
    {
        return((SQLData)super.getValue(row)).getLocalName();
    }

    /**
        Indicates if the value is null.
    **/
    public boolean isNull(JDRow row)
    throws SQLException
    {
        return false;
    }
}