///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: ConvTable916.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable916 extends ConvTableBidiMap
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

  private static final String toUnicode_ = 
    "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000B\f\r\u000E\u000F" +
    "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" +
    "\u0020\u0021\"\u0023\u0024\u0025\u0026\'\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" +
    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" +
    "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" +
    "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" +
    "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" +
    "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u007F" +
    "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F" +
    "\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F" +
    "\u00A0\u001A\u00A2\u00A3\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00D7\u00AB\u00AC\u00AD\u00AE\u203E" +
    "\u00B0\u00B1\u00B2\u00B3\u00B4\u00B5\u00B6\u2022\u00B8\u00B9\u00F7\u00BB\u00BC\u00BD\u00BE\u001A" +
    "\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A" +
    "\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u001A\u2017" +
    "\u05D0\u05D1\u05D2\u05D3\u05D4\u05D5\u05D6\u05D7\u05D8\u05D9\u05DA\u05DB\u05DC\u05DD\u05DE\u05DF" +
    "\u05E0\u05E1\u05E2\u05E3\u05E4\u05E5\u05E6\u05E7\u05E8\u05E9\u05EA\u001A\u001A\u001A\u001A\u001A";


  private static final String fromUnicode_ = 
    "\u0001\u0203\u0405\u0607\u0809\u0A0B\u0C0D\u0E0F\u1011\u1213\u1415\u1617\u1819\u1A1B\u1C1D\u1E1F" +
    "\u2021\u2223\u2425\u2627\u2829\u2A2B\u2C2D\u2E2F\u3031\u3233\u3435\u3637\u3839\u3A3B\u3C3D\u3E3F" +
    "\u4041\u4243\u4445\u4647\u4849\u4A4B\u4C4D\u4E4F\u5051\u5253\u5455\u5657\u5859\u5A5B\u5C5D\u5E5F" +
    "\u6061\u6263\u6465\u6667\u6869\u6A6B\u6C6D\u6E6F\u7071\u7273\u7475\u7677\u7879\u7A7B\u7C7D\u7E7F" +
    "\u8081\u8283\u8485\u8687\u8889\u8A8B\u8C8D\u8E8F\u9091\u9293\u9495\u9697\u9899\u9A9B\u9C9D\u9E9F" +
    "\uA01A\uA2A3\uA4A5\uA6A7\uA8A9\u1AAB\uACAD\uAE1A\uB0B1\uB2B3\uB4B5\uB61A\uB8B9\u1ABB\uBCBD\uBE1A" +
    "\uFFFF\u000B\u1A1A\u1AAA\uFFFF\u000F\u1A1A\u1ABA\uFFFF\u026C\u1A1A\uE0E1\uE2E3\uE4E5\uE6E7\uE8E9" +
    "\uEAEB\uECED\uEEEF\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\uFA1A\uFFFF\u0D15\u1A1A\u1ADF\uFFFF\u0005\u1A1A" +
    "\uB71A\uFFFF\r\u1A1A\uAF1A\uFFFF\u6FE0\u1A1A";


  ConvTable916()
  {
    super(916, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
  }
}
