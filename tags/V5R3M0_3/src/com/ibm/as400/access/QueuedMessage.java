///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: QueuedMessage.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2003 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;



/**
The QueuedMessage class represents a message on an OS/400 message queue
or job log.
@see com.ibm.as400.access.MessageQueue
@see com.ibm.as400.access.JobLog
**/
//
// Implementation notes:
//
// * The constructor and set methods are not public, since it
//   never makes sense for anyone other than MessageQueue or
//   JobLog to call these.
//
// * Message keys are 4 bytes.
//
public class QueuedMessage extends AS400Message implements Serializable
{
  private static final String copyright = "Copyright (C) 1997-2003 International Business Machines Corporation and others.";


  static final long serialVersionUID = 5L;

  private MessageQueue messageQueue_;

  private String sendingUser_ = "";
  private String sendingProgram_ = "";
  private String sendingJobName_ = "";
  private String sendingJobNumber_ = "";
  private String currentUser_ = "";
  private byte[] key_;

  private String replyStatus_ = "";

  private JobHashtable values_;

  private String alertOption_ = "";

/**
Constructs a QueuedMessage object.
**/
  QueuedMessage()
  {
  }



/**
Constructs a QueuedMessage object.

@param messageQueue The message queue.
**/
  QueuedMessage(MessageQueue messageQueue)
  {
    if (messageQueue == null) throw new NullPointerException("messageQueue");
    messageQueue_   = messageQueue;
    setSystem(messageQueue_.getSystem()); //@G1A
  }

/**
 * Constructs a QueuedMessage object. Used by MessageQueue.receive().
 * Called from MessageQueue.getMessages().
**/
  QueuedMessage(MessageQueue messageQueue, int messageSeverity, String messageIdentifier,
                int messageType, byte[] messageKey, String messageFileName, String messageLibraryName, 
                String dateSent, String timeSent)
  {
    super(messageIdentifier, null, messageFileName, messageLibraryName, messageSeverity, messageType, null, null, dateSent, timeSent, null);
    messageQueue_ = messageQueue;
    setSystem(messageQueue_.getSystem()); //@G1A
    key_ = messageKey;
  }

//@G1A
/**
 * Constructs a QueuedMessage object. Called from JobLog.getMessages().
**/
  QueuedMessage(AS400 system, int messageSeverity, String messageIdentifier,
                int messageType, byte[] messageKey, String messageFileName, String messageLibraryName, 
                String dateSent, String timeSent)
  {
    super(messageIdentifier, null, messageFileName, messageLibraryName, messageSeverity, messageType, null, null, dateSent, timeSent, null);
    setSystem(system);
    key_ = messageKey;
  }

/**
 * Constructs a QueuedMessage object. Used by MessageQueue.receive().
**/
  QueuedMessage(MessageQueue messageQueue, int messageSeverity, String messageIdentifier,
                int messageType, byte[] messageKey, String messageFileName, String messageLibraryName,
                String sendingJob, String sendingUserProfile, String sendingJobNumber,
                String sendingProgramName, String dateSent, String timeSent,
                byte[] replacementData, String messageData, String messageHelp, String alertOption)
  {
    super(messageIdentifier, messageData, messageFileName, messageLibraryName, messageSeverity, messageType, replacementData, messageHelp, dateSent, timeSent, null);
    messageQueue_ = messageQueue;
    setSystem(messageQueue_.getSystem()); //@G1A
    key_ = messageKey;
    sendingUser_ = sendingUserProfile;
    sendingJobName_ = sendingJob;
    sendingJobNumber_ = sendingJobNumber;
    sendingProgram_ = sendingProgramName;
    alertOption_ = alertOption;
  }

  /**
   * Returns the alert option.
   * Possible values are:
   * <UL>
   * <LI>*DEFER - An alert is sent after local problem analysis.
   * <LI>*IMMED - An alert is sent immediately when the message is sent to a 
   * message queue that has the allow alerts attribute set to *YES.
   * <LI>*NO - No alert is sent.
   * <LI>*UNATTEND - An alert is sent immediately when the system is running
   * in unattended mode. See the ALRSTS network attribute.
   * <LI>"" - The alert option was not specified when the message was sent.
   * </UL>
   * @return One of the above values.
  **/
  public String getAlertOption()
  {
    return alertOption_.trim();
  }

/**
Returns the sending program name.

@return The sending program name, or "" if it is not set.
**/
  public String getFromProgram()
  {
    if (sendingProgram_.length() == 0 && values_ != null)
    {
      String s = (String)values_.get(603);
      if (s != null)
      {
        sendingProgram_ = s.trim();
      }
    }
    return sendingProgram_;
  }



/**
Returns the sender job name.

@return The sender job name, or "" if it is not set.
@see #getFromJobNumber
@see #getUser
**/
  public String getFromJobName()
  {
    if (sendingJobName_.length() == 0 && values_ != null)
    {
      String s = (String)values_.get(601);
      if (s != null)
      {
        sendingJobName_ = s.substring(0,10).trim();
      }
    }
    return sendingJobName_;
  }



/**
Returns the sender job number.

@return The sender job number, or "" if it is not set.
@see #getFromJobName
@see #getUser
**/
  public String getFromJobNumber()
  {
    if (sendingJobNumber_.length() == 0 && values_ != null)
    {
      String s = (String)values_.get(601);
      if (s != null)
      {
        sendingJobNumber_ = s.substring(20,26);
      }
    }
    return sendingJobNumber_;
  }


  /**
   * Returns the sender job's user. To get the
   * current user of the message, call {@link #getCurrentUser getCurrentUser()} when accessing a system
   * running OS/400 V5R3 or higher.
   * @return The sender job's user, or "" if it is not set.
   * @see #getFromJobName
   * @see #getFromJobNumber
  **/
  public String getUser()
  {
    if (sendingUser_.length() == 0 && values_ != null)
    {
      String s = (String)values_.get(601);
      if (s != null)
      {
        sendingUser_ = s.substring(10,20).trim();
      }
    }
    return sendingUser_;
  }


/**
Returns the 4-byte message key.

@return The message key, or null if it is not set.
**/
  public byte[] getKey()
  {
    return key_;
  }



/**
Returns the message queue.

@return The message queue, or null if it is not set.
**/
  public MessageQueue getQueue()
  {
    return messageQueue_;
  }



/**
Returns the reply status.

@return The reply status, "" if it is not set, or null
        if it is not applicable.
**/
  public String getReplyStatus()
  {
    if (replyStatus_ == "" && values_ != null)
    {
      String s = (String)values_.get(1001);
      if (s != null)
      {
        replyStatus_ = s.trim();
      }
    }
    return replyStatus_;
  }



  /**
   * Returns the current user name. If the system being accessed is running OS/400 V5R2 or earlier,
   * then "" is returned.
   * @return The current user name, or "" if it is not set.
  **/
  public String getCurrentUser()
  {
    if (currentUser_.length() == 0 && values_ != null)                                                  //@K1A
    {
      String s = (String)values_.get(607);                                                    //@K1A
      if (s != null)                                                                          //@K1A
      {
        //@K1A
        currentUser_ = s.trim();                                                            //@K1A
      }                                                                                       //@K1A
    }                                                                                           //@K1A
    return currentUser_;
  }                                                                                               //@K1A


  // Helper method called by MessageQueue.
  void setAsInt(int fieldID, int value)
  {
    setValueInternal(fieldID, new Integer(value));
  }


  // Helper method called by MessageQueue.
  void setAsLong(int fieldID, long value)
  {
    setValueInternal(fieldID, new Long(value));
  }


  // Helper method called by MessageQueue.
  void setValueInternal(int fieldID, Object value)
  {
    if (values_ == null) values_ = new JobHashtable();
    values_.put(fieldID, value);
    switch (fieldID)
    {
      case 501:
        setDefaultReply((String)value);
        break;
      case 404:
        setHelp((String)value);
        break;
      case 302:
        setText((String)value);
        break;
      case 101:
        alertOption_ = (String)value;
        break;
      default:
        break;
    }
  }


  /**
   * Returns the String representation of this QueuedMessage object.
   * @return The string.
   **/
  public String toString()
  {
    return super.toStringM2();
  }

}
