///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: ConvTable905.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2002 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable905 extends ConvTableSingleMap
{
  private static final String copyright = "Copyright (C) 1997-2002 International Business Machines Corporation and others.";
  
  private static final String toUnicode_ = 
    "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
    "\u0020\u00A0\u00E2\u00E4\u00E0\u00E1\u001A\u010B\u007B\u00F1\u00C7\u002E\u003C\u0028\u002B\u0021" +
    "\u0026\u00E9\u00EA\u00EB\u00E8\u00ED\u00EE\u00EF\u00EC\u00DF\u011E\u0130\u002A\u0029\u003B\u005E" +
    "\u002D\u002F\u00C2\u00C4\u00C0\u00C1\u001A\u010A\u005B\u00D1\u015F\u002C\u0025\u005F\u003E\u003F" +
    "\u001A\u00C9\u00CA\u00CB\u00C8\u00CD\u00CE\u00CF\u00CC\u0131\u003A\u00D6\u015E\'\u003D\u00DC" +
    "\u02D8\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u0127\u0109\u015D\u016D\u001A\u007C" +
    "\u00B0\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\u0125\u011D\u0135\u00B8\u001A\u00A4" +
    "\u00B5\u00F6\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u0126\u0108\u015C\u016C\u001A\u0040" +
    "\u02D9\u00A3\u017C\u007D\u017B\u00A7\u005D\u00B7\u00BD\u0024\u0124\u011C\u0134\u00A8\u00B4\u00D7" +
    "\u00E7\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u00AD\u00F4\u007E\u00F2\u00F3\u0121" +
    "\u011F\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\u0060\u00FB\\\u00F9\u00FA\u001A" +
    "\u00FC\u00F7\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u00B2\u00D4\u0023\u00D2\u00D3\u0120" +
    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u00B3\u00DB\"\u00D9\u00DA\u009F";


  private static final String fromUnicode_ = 
    "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
    "\u404F\uFCEC\uB96C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
    "\uAFC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE968\uDCB6\u5F6D" +
    "\uDA81\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA948\u8FB3\uCC07" +
    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
    "\u413F\u3FB1\u9F3F\u3FB5\uBD3F\u3F3F\u3FCA\u3F3F\u903F\uEAFA\uBEA0\u3FB7\u9D3F\u3F3F\u3FB8\u3F3F" +
    "\u6465\u623F\u633F\u3F4A\u7471\u7273\u7875\u7677\u3F69\uEDEE\uEB3F\u7BBF\u3FFD\uFEFB\u7F3F\u3F59" +
    "\u4445\u423F\u433F\u3FC0\u5451\u5253\u5855\u5657\u3F49\uCDCE\uCB3F\uA1E1\u3FDD\uDEDB\uE03F\uFFFF" +
    "\u0005\u3F3F\uAB8B\u6747\uFFFF\b\u3F3F\uBB9B\u5AD0\uEFCF\u3F3F\uBA9A\uAA8A\uFFFF\u0004\u3F3F" +
    "\u5B79\u3F3F\uBC9C\uFFFF\u0013\u3F3F\uAC8C\u7C6A\uFFFF\u0006\u3F3F\uAD8D\uFFFF\u0006\u3F3F\u3FB4" +
    "\uB23F\uFFFF\u00AD\u3F3F\u80B0\uFFFF\u7E93\u3F3F";


  ConvTable905()
  {
    super(905, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
  }
}
