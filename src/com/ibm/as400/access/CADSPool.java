///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: CADSPool.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2001 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

// This class is used to pool ClientAccessDataStream objects.
final class CADSPool
{
  private static final String copyright = "Copyright (C) 1997-2001 International Business Machines Corporation and others.";

  private ClientAccessDataStream[] streams_ = new ClientAccessDataStream[4];

  final ClientAccessDataStream getUnusedStream()
  {
    synchronized(streams_)
    {
      int max = streams_.length;
      for (int i=0; i<max; ++i)
      {
        if (streams_[i] == null)
        {
          streams_[i] = new ClientAccessDataStream();
          streams_[i].inUse_ = true;
          return streams_[i];
        }
        if (!streams_[i].inUse_)
        {
          streams_[i].inUse_ = true;
          return streams_[i];
        }
      }
      // Need more streams
      ClientAccessDataStream[] newStreams = new ClientAccessDataStream[max*2];
      newStreams[max] = new ClientAccessDataStream();
      newStreams[max].inUse_ = true;
      streams_ = newStreams;
      return newStreams[max];
    }
  }
}


