///////////////////////////////////////////////////////////////////////////////
//                                                                             
// AS/400 Toolbox for Java - OSS version                                       
//                                                                             
// Filename: ConvTable851.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable851 extends ConvTableAsciiMap
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

  private static final String toUnicode_ = 
    "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000B\f\r\u000E\u000F" +
    "\u0010\u0011\u0012\u0013\u00B6\u0015\u0016\u0017\u0018\u0019\u001C\u001B\u007F\u001D\u001E\u001F" +
    "\u0020\u0021\"\u0023\u0024\u0025\u0026\'\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" +
    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" +
    "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" +
    "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" +
    "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" +
    "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u001A" +
    "\u00C7\u00FC\u00E9\u00E2\u00E4\u00E0\u0386\u00E7\u00EA\u00EB\u00E8\u00EF\u00EE\u0388\u00C4\u0389" +
    "\u038A\u001A\u038C\u00F4\u00F6\u038E\u00FB\u00F9\u038F\u00D6\u00DC\u03AC\u00A3\u03AD\u03AE\u03AF" +
    "\u03CA\u0390\u03CC\u03CD\u0391\u0392\u0393\u0394\u0395\u0396\u0397\u00BD\u0398\u0399\u00AB\u00BB" +
    "\u2591\u2592\u2593\u2502\u2524\u039A\u039B\u039C\u039D\u2563\u2551\u2557\u255D\u039E\u039F\u2510" +
    "\u2514\u2534\u252C\u251C\u2500\u253C\u03A0\u03A1\u255A\u2554\u2569\u2566\u2560\u2550\u256C\u03A3" +
    "\u03A4\u03A5\u03A6\u03A7\u03A8\u03A9\u03B1\u03B2\u03B3\u2518\u250C\u2588\u2584\u03B4\u03B5\u2580" +
    "\u03B6\u03B7\u03B8\u03B9\u03BA\u03BB\u03BC\u03BD\u03BE\u03BF\u03C0\u03C1\u03C3\u03C2\u03C4\u00B4" +
    "\u00AD\u00B1\u03C5\u03C6\u03C7\u00A7\u03C8\u00B8\u00B0\u00A8\u03C9\u03CB\u03B0\u03CE\u25A0\u00A0";


  private static final String fromUnicode_ = 
    "\u0001\u0203\u0405\u0607\u0809\u0A0B\u0C0D\u0E0F\u1011\u1213\u7F15\u1617\u1819\u7F1B\u1A1D\u1E1F" +
    "\u2021\u2223\u2425\u2627\u2829\u2A2B\u2C2D\u2E2F\u3031\u3233\u3435\u3637\u3839\u3A3B\u3C3D\u3E3F" +
    "\u4041\u4243\u4445\u4647\u4849\u4A4B\u4C4D\u4E4F\u5051\u5253\u5455\u5657\u5859\u5A5B\u5C5D\u5E5F" +
    "\u6061\u6263\u6465\u6667\u6869\u6A6B\u6C6D\u6E6F\u7071\u7273\u7475\u7677\u7879\u7A7B\u7C7D\u7E1C" +
    "\uFFFF\u0010\u7F7F\uFF7F\u7F9C\u7F7F\u7FF5\uF97F\u7FAE\u7FF0\u7F7F\uF8F1\u7F7F\uEF7F\u147F\uF77F" +
    "\u7FAF\u7FAB\u7F7F\u7F7F\u7F7F\u8E7F\u7F80\uFFFF\u0007\u7F7F\u997F\u7F7F\u7F7F\u9A7F\u7F7F\u857F" +
    "\u837F\u847F\u7F87\u8A82\u8889\u7F7F\u8C8B\u7F7F\u7F7F\u937F\u947F\u7F97\u7F96\u817F\uFFFF\u0144" +
    "\u7F7F\u867F\u8D8F\u907F\u927F\u9598\uA1A4\uA5A6\uA7A8\uA9AA\uACAD\uB5B6\uB7B8\uBDBE\uC6C7\u7FCF" +
    "\uD0D1\uD2D3\uD4D5\u7F7F\u9B9D\u9E9F\uFCD6\uD7D8\uDDDE\uE0E1\uE2E3\uE4E5\uE6E7\uE8E9\uEAEB\uEDEC" +
    "\uEEF2\uF3F4\uF6FA\uA0FB\uA2A3\uFD7F\uFFFF\u1098\u7F7F\uC47F\uB37F\uFFFF\u0004\u7F7F\uDA7F\u7F7F" +
    "\uBF7F\u7F7F\uC07F\u7F7F\uD97F\u7F7F\uC37F\u7F7F\u7F7F\u7F7F\uB47F\u7F7F\u7F7F\u7F7F\uC27F\u7F7F" +
    "\u7F7F\u7F7F\uC17F\u7F7F\u7F7F\u7F7F\uC57F\uFFFF\t\u7F7F\uCDBA\u7F7F\uC97F\u7FBB\u7F7F\uC87F" +
    "\u7FBC\u7F7F\uCC7F\u7FB9\u7F7F\uCB7F\u7FCA\u7F7F\uCE7F\uFFFF\t\u7F7F\uDF7F\u7F7F\uDC7F\u7F7F" +
    "\uDB7F\u7F7F\u7F7F\u7F7F\u7FB0\uB1B2\uFFFF\u0006\u7F7F\uFE7F\uFFFF\u6D2F\u7F7F";


  ConvTable851()
  {
    super(851, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
  }
}
