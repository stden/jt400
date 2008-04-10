///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                              
//                                                                             
// Filename: CommandCall.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import com.ibm.as400.resource.RJob;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Vector;

/**
 The CommandCall class represents an iSeries server command object.  This class allows the user to call an iSeries server CL command.  Results of the command are returned in a message list.
 <P>The following example demonstrates the use of CommandCall:
 <br>
 <pre>
 *    // Work with commands on server named "Hal."
 *    AS400 system = new AS400("Hal");
 *    CommandCall command = new CommandCall(system);
 *    try
 *    {
 *        // Run the command "CRTLIB FRED."
 *        if (command.run("CRTLIB FRED") != true)
 *        {
 *            // Note that there was an error.
 *            System.out.println("Command failed!");
 *        }
 *        // Show the messages (returned whether or not there was an error.)
 *        AS400Message[] messagelist = command.getMessageList();
 *        for (int i = 0; i < messagelist.length; ++i)
 *        {
 *            // Show each message.
 *            System.out.println(messagelist[i].getText());
 *        }
 *    }
 *    catch (Exception e)
 *    {
 *        System.out.println("Command " + command.getCommand() + " issued an exception!");
 *        e.printStackTrace();
 *    }
 *    // Done with the server.
 *    system.disconnectService(AS400.COMMAND);
 </pre>
 <p>NOTE:  When getting the message list from commands, users no longer have to create a <a href="MessageFile.html">MessageFile</a> to obtain the message help text.  The load() method can be used to retrieve additional message information. Then the getHelp() method can be called directly on the <a href="AS400Message.html">AS400Message</a> object returned from getMessageList().  Here is an example:
 <PRE>
 *    if (command.run("myCmd") != true)
 *    {
 *        // Show messages.
 *        AS400Message[] messageList = command.getMessageList();
 *        for (int i = 0; i < messageList.length; ++i)
 *        {
 *            // Show each message.
 *            System.out.println(messageList[i].getText());
 *            // Load additional message information.
 *            messageList[i].load();
 *            //Show help text.
 *            System.out.println(messageList[i].getHelp());
 *        }
 *    }
 </PRE>
 **/
public class CommandCall implements Serializable
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

    static final long serialVersionUID = 4L;

    // Constants that indicate how thread safety was determined.
    private static final int BY_DEFAULT = 0;
    private static final int BY_PROPERTY = 1;
    private static final int BY_SET_METHOD = 2;
    private static final int BY_LOOK_UP = 3;

    //The server the command is run on.
    private AS400 system_ = null;
    // The command to run.
    private String command_ = "";
    // The messages returned by the command.
    private AS400Message[] messageList_ = new AS400Message[0];

    // Implemenation object shared with program call, interacts with server or native methods.
    private transient RemoteCommandImpl impl_;

    // List of action completed event bean listeners.
    private transient Vector actionCompletedListeners_ = new Vector();
    // List of property change event bean listeners.
    private transient PropertyChangeSupport propertyChangeListeners_ = new PropertyChangeSupport(this);
    // List of vetoable change event bean listeners.
    private transient VetoableChangeSupport vetoableChangeListeners_ = new VetoableChangeSupport(this);

    // Thread safety of command.
    private boolean threadSafety_ = false;
    // How thread safety was determined.
    private int threadSafetyDetermined_ = BY_DEFAULT;

    /**
     Constructs a CommandCall object.  The server and the command must be set later.
     **/
    public CommandCall()
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Constructing CommandCall object.");
        checkThreadSafetyProperty();
    }

    /**
     Constructs a CommandCall object.  It uses the specified server.  The command must be set later.
     @param  system  The server on which to run the command.
     **/
    public CommandCall(AS400 system)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Constructing CommandCall object, system: " + system);
        if (system == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'system' is null.");
            throw new NullPointerException("system");
        }
        system_ = system;
        checkThreadSafetyProperty();
    }

    /**
     Constructs a CommandCall object.  It uses the specified server and command.
     @param  system  The server on which to run the command.
     @param  command  The command to run on the server.  If the command is not library qualified, the library list will be used to find the command.
     **/
    public CommandCall(AS400 system, String command)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Constructing CommandCall object, system: " + system + " command: " + command);
        if (system == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'system' is null.");
            throw new NullPointerException("system");
        }
        if (command == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'command' is null.");
            throw new NullPointerException("command");
        }

        system_  = system;
        command_ = command;

        checkThreadSafetyProperty();
    }

    /**
     Adds an ActionCompletedListener.  The specified ActionCompletedListener's <b>actionCompleted</b> method will be called each time a command has run.  The ActionCompletedListener object is added to a list of ActionCompletedListeners managed by this CommandCall.  It can be removed with removeActionCompletedListener.
     @param  listener  The ActionCompletedListener.
     **/
    public void addActionCompletedListener(ActionCompletedListener listener)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Adding action completed listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        actionCompletedListeners_.addElement(listener);
    }

    /**
     Adds a PropertyChangeListener.  The specified PropertyChangeListener's <b>propertyChange</b> method will be called each time the value of any bound property is changed.  The PropertyChangeListener object is added to a list of PropertyChangeListeners managed by this CommandCall.  It can be removed with removePropertyChangeListener.
     @param  listener  The PropertyChangeListener.
     **/
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Adding property change listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        propertyChangeListeners_.addPropertyChangeListener(listener);
    }

    /**
     Adds a VetoableChangeListener.  The specified VetoableChangeListener's <b>vetoableChange</b> method will be called each time the value of any constrained property is changed.
     @param  listener  The VetoableChangeListener.
     **/
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Adding vetoable change listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        vetoableChangeListeners_.addVetoableChangeListener(listener);
    }

    // Chooses the appropriate implementation, synchronize to protect impl_ object.
    private synchronized void chooseImpl() throws AS400SecurityException, IOException
    {
        if (impl_ == null)
        {
            if (system_ == null)
            {
/*                if (AS400.onAS400)
                {
                    impl_ = (RemoteCommandImpl)AS400.loadImpl("com.ibm.as400.access.RemoteCommandImplNative");
                    if (impl_ != null) return;
                }*/
                Trace.log( Trace.ERROR, "Attempt to connect to command server before setting system." );
                throw new ExtendedIllegalStateException("system", ExtendedIllegalStateException.PROPERTY_NOT_SET);
            }

            impl_ = (RemoteCommandImpl)system_.loadImpl3("com.ibm.as400.access.RemoteCommandImplNative", "com.ibm.as400.access.RemoteCommandImplRemote", "com.ibm.as400.access.RemoteCommandImplProxy");
            impl_.setSystem(system_.getImpl());
        }
        if (system_ != null) system_.signon(false);
    }

    // Fires the action completed event.
    private void fireActionCompleted()
    {
        Vector targets = (Vector)actionCompletedListeners_.clone();
        ActionCompletedEvent event = new ActionCompletedEvent(this);
        for (int i = 0; i < targets.size(); ++i)
        {
            ActionCompletedListener target = (ActionCompletedListener)targets.elementAt(i);
            target.actionCompleted(event);
        }
    }

    /**
     Returns the command to run.  It will return an empty string ("") if the command has not been previously set.
     @return  The command to run.
     **/
    public String getCommand()
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Getting command: " + command_);
        return command_;
    }

    /**
     Returns an RJob object which represents the server job in which the command will be run.  The information contained in the RJob object is invalidated by <code>AS400.disconnectService()</code> or <code>AS400.disconnectAllServices()</code>.
     <br>Typical uses include:
     <br>(1) before run() to identify the job before calling the command;
     <br>(2) after run() to see what job the command ran under (to identify the job log, for example).
     <p><b>Note:</b> This method is not supported in the Toolbox proxy environment.
     @return  The job in which the command will be run.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the server.
     @exception  InterruptedException  If this thread is interrupted.
     @see #getServerJob
     **/
    public RJob getJob() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Getting job.");
        chooseImpl();
        String jobInfo = impl_.getJobInfo(threadSafety_);
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Constructing RJob for job: " + jobInfo);
        // Contents of the "job information" string:  The name of the user job that the thread is associated with.  The format of the job name is a 10-character simple job name, a 10-character user name, and a 6-character job number.
        // Changed this return so the class loader would not complain when using the Proxy - wiedrich.
        return new RJob(system_, jobInfo.substring(0, 10).trim(), jobInfo.substring(10, 20).trim(), jobInfo.substring(20, 26).trim());
    }

    /**
     Returns the list of messages returned from running the command.  It will return an empty list if the command has not been run yet or if there are no messages.
     @return  The array of messages returned by the command.
     **/
    public AS400Message[] getMessageList()
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Getting message list.");
        return messageList_;
    }

    /**
     Returns a message returned from running the command.
     @param  index  The index into the list of messages returned by the command.  It must be greater than or equal to zero and less than the number of messages in the list.
     @return  The message at the requested index returned by the command.
     **/
    public AS400Message getMessageList(int index)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Getting message from message list:", index);
        return messageList_[index];
    }

    //@E0A
    /**
     Returns a Job object which represents the server job in which the command will be run.  
     The information contained in the Job object is invalidated by <code>AS400.disconnectService()</code>
     or <code>AS400.disconnectAllServices()</code>.
     <br>Typical uses include:
     <br>(1) before run() to identify the job before calling the command;
     <br>(2) after run() to see what job the command ran under (to identify the job log, for example).
     <p><b>Note:</b> This method is not supported in the Toolbox proxy environment.
     @return  The job in which the command will be run.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the server.
     @exception  InterruptedException  If this thread is interrupted.
     @see #getJob
     **/
    public Job getServerJob() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException
    {
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Getting job.");
        chooseImpl();
        String jobInfo = impl_.getJobInfo(threadSafety_);
        if (Trace.traceOn_) Trace.log(Trace.DIAGNOSTIC, "Constructing Job for job: " + jobInfo);
        // Contents of the "job information" string:  The name of the user job that the thread is associated with.
        // The format of the job name is a 10-character simple job name, a 10-character user name, and a 6-character job number.
        return new Job(system_, jobInfo.substring(0, 10).trim(), jobInfo.substring(10, 20).trim(), jobInfo.substring(20, 26).trim());
    }

    /**
     Returns the server on which the command is to be run.
     @return  The server on which the command is to be run.  If the server has not been set, null is returned.
     **/
    public AS400 getSystem()
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Getting system: " + system_);
        return system_;
    }

    /**
     Returns the thread on which the command would be run, if it were to be called on-thread.  Returns null if either:
     <ul compact>
     <li> The client is communicating with the server through sockets.
     <li> The command has not been marked as thread safe.
     </ul>
     @return  The thread on which the command would be run.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  IOException  If an error occurs while communicating with the server.
     **/
    public Thread getSystemThread() throws AS400SecurityException, IOException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Getting system thread.");
        chooseImpl();
        Thread currentThread = impl_.getClass().getName().endsWith("ImplNative") ? Thread.currentThread() : null;
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "System thread: " + currentThread);
        return currentThread;
    }

    // Check thread safety system property.
    private void checkThreadSafetyProperty()
    {
        String property = SystemProperties.getProperty(SystemProperties.COMMANDCALL_THREADSAFE);
        if (property == null)  // Property not set.
        {
            if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Thread safe system property not set, thread safety property remains unspecified.");
        }
        else
        {
            threadSafety_ = property.equalsIgnoreCase("true");
            threadSafetyDetermined_ = BY_PROPERTY;
            if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Thread safe system property: " +  property);
        }
    }

    /**
     Indicates whether or not the command will actually get run on the current thread.
     @return  true if the command will be run on the current thread; false otherwise.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the server.
     @exception  InterruptedException  If this thread is interrupted.
     **/
    public boolean isStayOnThread() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Checking if command will actually get run on the current thread.");
        if (command_.length() == 0)
        {
            Trace.log(Trace.ERROR, "Attempt to check thread safety before setting command.");
            throw new ExtendedIllegalStateException ("command", ExtendedIllegalStateException.PROPERTY_NOT_SET);
        }
        chooseImpl();
        if (threadSafetyDetermined_ == BY_DEFAULT)
        {
            threadSafety_ = impl_.isCommandThreadSafe(command_);
            threadSafetyDetermined_ = BY_LOOK_UP;
        }
        boolean isStayOnThread = (threadSafety_ && impl_.getClass().getName().endsWith("ImplNative"));
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Command will actually get run on the current thread: ", isStayOnThread);
        return isStayOnThread;
    }

    /**
     Indicates whether or not the command will be assumed thread-safe, according to the settings specified by <code>setThreadSafe()</code> or the <code>com.ibm.as400.access.CommandCall.threadSafe</code> property.
     <br>Note: If the CL command on the server is not actually threadsafe (as indicated by its "threadsafe indicator" attribute), then the results of attempting to run the command on-thread will depend on the command's "multithreaded job action" attribute, in combination with the setting of system value QMLTTHDACN ("Multithreaded job action").  Possible results are:
     <ul>
     <li> Run the command. Do not send a message.
     <li> Send an informational message and run the command.
     <li> Send an escape message, and do not run the command.
     </ul>
     @return  true if the command will be assumed thread-safe; false otherwise.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the server.
     @exception  InterruptedException  If this thread is interrupted.
     **/
    public boolean isThreadSafe() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Checking if command will be assumed thread-safe: " + threadSafety_);
        return threadSafety_;
    }

    // Deserializes and initializes transient data.
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "De-serializing CommandCall object.");
        in.defaultReadObject();

        // impl_ remains null.
        actionCompletedListeners_ = new Vector();
        propertyChangeListeners_ = new PropertyChangeSupport(this);
        vetoableChangeListeners_ = new VetoableChangeSupport(this);

        // (Re)initialize the thread-safe attribute.  Note:  The threadSafety attribute is persistent, not transient.  First see if object was previously serialized when its thread-safe behavior was determined by a system property (and not specified via setThreadSafe()).  This property may have since changed.
        if (threadSafetyDetermined_ != BY_SET_METHOD)
        {
            String property = SystemProperties.getProperty(SystemProperties.COMMANDCALL_THREADSAFE);
            if (property == null)  // Property not set.
            {
                threadSafety_ = false;
                threadSafetyDetermined_ = BY_DEFAULT;
                if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Thread safe system property not set, thread safety property changed to unspecified.");
            }
            else
            {
                threadSafety_ = property.equalsIgnoreCase("true");
                threadSafetyDetermined_ = BY_PROPERTY;
                if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Thread safe system property: " + property);
            }
        }
    }

    /**
     Removes the ActionCompletedListener.  If the ActionCompletedListener is not on the list, nothing is done.
     @param  listener  The ActionCompletedListener.
     **/
    public void removeActionCompletedListener(ActionCompletedListener listener)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Removing action completed listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        actionCompletedListeners_.removeElement(listener);
    }

    /**
     Removes the PropertyChangeListener.  If the PropertyChangeListener is not on the list, nothing is done.
     @param  listener  The PropertyChangeListener.
     **/
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Removing property change listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        propertyChangeListeners_.removePropertyChangeListener(listener);
    }

    /**
     Removes the VetoableChangeListener.  If the VetoableChangeListener is not on the list, nothing is done.
     @param  listener  The VetoableChangeListener.
     **/
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Removing vetoable change listener.");
        if (listener == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'listener' is null.");
            throw new NullPointerException("listener");
        }
        vetoableChangeListeners_.removeVetoableChangeListener(listener);
    }

    /**
     Runs the command on the server.  The command must be set prior to this call.
     @return  true if command is successful; false otherwise.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the server.
     @exception  InterruptedException  If this thread is interrupted.
     **/
    public boolean run() throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.INFORMATION, "Running command: " + command_);
        if (command_.length() == 0)
        {
            Trace.log(Trace.ERROR, "Attempt to run before setting command.");
            throw new ExtendedIllegalStateException ("command", ExtendedIllegalStateException.PROPERTY_NOT_SET);
        }

        chooseImpl();
        if (threadSafetyDetermined_ == BY_DEFAULT)
        {
            threadSafety_ = impl_.isCommandThreadSafe(command_);
            threadSafetyDetermined_ = BY_LOOK_UP;
            if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Command thread safety: ", threadSafety_);
        }
        // Run the command.
        boolean result = impl_.runCommand(command_, threadSafety_);
        // Retrieve the messages.
        messageList_ = impl_.getMessageList();
        // Set our system into each of the messages.
        if (system_ != null)
        {
        for (int i = 0; i < messageList_.length; ++i)
        {
            messageList_[i].setSystem(system_);
        }
        }

        // Fire action completed event.
        fireActionCompleted();
        return result;
    }

    /**
     Sets the command and runs it on the server.
     @param  command  The command to run.  If the command is not library qualified, the library list will be used to find the command.
     @return  true if command is successful; false otherwise.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the server.
     @exception  InterruptedException  If this thread is interrupted.
     @exception  PropertyVetoException  If the change is vetoed.
     **/
    public boolean run(String command) throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, PropertyVetoException
    {
        setCommand(command);
        return run();
    }

    /**
     Runs the command on the server.  This method takes the command to run as a byte array instead of a String.  The most common use of CommandCall is to supply the command to run as a String and let the Toolbox convert the string to server format (EBCDIC) before sending it to the server for processing.  Use this method if the default conversion of the command to EBCDIC is not correct.  In certain cases, especially bi-directional languages, the Toolbox conversion is not be correct.  In this case the application can construct their own command and supply it to CommandCall as a byte array.
     <p>Unlike the run method that takes a string, this method will not look up the thread safety of the command.  If this command is to be run on-thread when running on the server's JVM, setThreadSafe(true) must be called by the application.
     @param  command  The command to run.
     @return  true if command is successful; false otherwise.
     @exception  AS400SecurityException  If a security or authority error occurs.
     @exception  ErrorCompletingRequestException  If an error occurs before the request is completed.
     @exception  IOException  If an error occurs while communicating with the server.
     @exception  InterruptedException  If this thread is interrupted.
     @exception  PropertyVetoException  If the change is vetoed.
     **/
    public boolean run(byte[] command) throws AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, PropertyVetoException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.INFORMATION, "Running command:", command);
        if (command == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'command' is null.");
            throw new NullPointerException("command");
        }
        if (command.length == 0)
        {
            Trace.log(Trace.ERROR, "Length of 'command' parameter is not valid:", command);
            throw new ExtendedIllegalArgumentException("command (" + command.length + ")", ExtendedIllegalArgumentException.LENGTH_NOT_VALID);
        }

        chooseImpl();

        // Run the command.
        boolean success = impl_.runCommand(command, threadSafety_);

        // Retrieve the messages.
        messageList_ = impl_.getMessageList();

        // Set our system into each of the messages.
        if (system_ != null)
        {
        for (int i = 0; i < messageList_.length; ++i)
        {
            messageList_[i].setSystem(system_);
        }
        }

        // Fire action completed event.
        fireActionCompleted();
        return success;
    }

    /**
     Sets the command to run.
     @param  command  The command to run on the server.  If the command is not library qualified, the library list will be used to find the command.
     @exception  PropertyVetoException  If the change is vetoed.
     **/
    public void setCommand(String command) throws PropertyVetoException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Setting command: " + command);
        if (command == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'command' is null.");
            throw new NullPointerException("command");
        }

        String old = command_;
        vetoableChangeListeners_.fireVetoableChange("command", old, command);
        if (threadSafetyDetermined_ == BY_LOOK_UP && !command_.equals(command))
        {
            threadSafety_ = false;
            threadSafetyDetermined_ = BY_DEFAULT;
        }
        command_ = command;
        propertyChangeListeners_.firePropertyChange("command", old, command);
    }

    /**
     Sets the server to run the command.  The server cannot be changed once a connection is made to the server.
     @param  system  The server on which to run the command.
     @exception  PropertyVetoException  If the change is vetoed.
     **/
    public void setSystem(AS400 system) throws PropertyVetoException
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Setting system: " + system);
        if (system == null)
        {
            Trace.log(Trace.ERROR, "Parameter 'system' is null.");
            throw new NullPointerException("system");
        }

        if (impl_ != null)
        {
            Trace.log(Trace.ERROR, "Cannot set property 'system' after connect.");
            throw new ExtendedIllegalStateException("system", ExtendedIllegalStateException.PROPERTY_NOT_CHANGED);
        }

        AS400 old = system_;
        vetoableChangeListeners_.fireVetoableChange("system", old, system);
        system_ = system;
        propertyChangeListeners_.firePropertyChange("system", old, system);
    }

    /**
     Specifies whether or not the command should be assumed thread-safe.  If not specified, the default is the command's actual "threadsafe" attribute on the server.  The thread-safety lookup is a run-time check, so it will affect performance.  To be as fast as possible, we recommend setting this attribute, to avoid the run-time lookup.
     <br>Note: This method does not modify the actual command object on the server.
     @param  threadSafe  true if the command should be assumed to be thread-safe; false otherwise.
     **/
    public void setThreadSafe(boolean threadSafe)
    {
        if (Trace.isTraceOn()) Trace.log(Trace.DIAGNOSTIC, "Setting thread safe: " + threadSafe);
        Boolean oldValue = new Boolean(threadSafety_);
        Boolean newValue = new Boolean(threadSafe);

        threadSafety_ = threadSafe;
        threadSafetyDetermined_ = BY_SET_METHOD;

        propertyChangeListeners_.firePropertyChange ("threadSafe", oldValue, newValue);
    }

    /**
     Returns the string representation of this command call object.
     @return  The string representing this command call object.
     **/
    public String toString()
    {
        return "CommandCall (system: " + system_ + " command: " + command_ + "):" + super.toString();
    }
}