///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: PrinterListBeanInfo.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.beans.SimpleBeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.BeanInfo;
import java.awt.Image;

/**
BeanInfo for PrinterList class.
**/
public class PrinterListBeanInfo extends PrintObjectListBeanInfo
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

    // Additional properties defined for PrinterList
    private static PropertyDescriptor[] prtDListProperties_;

    // Class this bean info represents.
    private final static Class beanClass = PrinterList.class;

    // Handles loading the appropriate resource bundle
    private static ResourceBundleLoader rbl_;

    static
    {
        try
        {
            PropertyDescriptor prtDFilter =
              new PropertyDescriptor("printerFilter", beanClass);
            prtDFilter.setBound(true);
            prtDFilter.setConstrained(true);
            prtDFilter.setDisplayName(rbl_.getText("PROP_NAME_PRTD_NAME_FILTER"));
            prtDFilter.setShortDescription(rbl_.getText("PROP_DESC_PRTD_NAME_FILTER"));

            PropertyDescriptor[] properties = {prtDFilter};
            prtDListProperties_ = properties;
        }

        catch (IntrospectionException e)
        {
            throw new Error(e.toString());
        }
    }

    /**
    Returns the bean descriptor.
    @return The bean descriptor.
    **/
    public BeanDescriptor getBeanDescriptor()
    {
        return new BeanDescriptor(beanClass);
    }

    // Returns the copyright.
    private static String getCopyright()
    {
        return Copyright.copyright;
    }

    // PrinterList does not define any additional events so we
    // let the PrintObjectList provide:
    // public int getDefaultEventIndex()
    // public EventSetDescriptor[] getEventSetDescriptors()

    // We want "system" as the default property. PrintObjectList
    // sets this so we don't have to override:
    // public int getDefaultPropertyIndex()

    /**
    Returns the descriptors for all properties.
    @return The descriptors for all properties.
    **/
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        // Per the JavaBean spec, properties are discrete, named attributes
        // of a Java Bean that can affect its appearance or its behavior.

        PropertyDescriptor[] printObjectListProperties;
        PropertyDescriptor[] combinedProperties;
        int combinedSize;

        // Get the properties defined in PrintObjectListBeanInfo
        printObjectListProperties = super.getPropertyDescriptors();

        combinedSize = printObjectListProperties.length + prtDListProperties_.length;
        combinedProperties = new PropertyDescriptor[combinedSize];

        // copy PrintObjectList properties
        System.arraycopy( printObjectListProperties,          // source
                          0,                                  // --offset
                          combinedProperties,                 // destination
                          0,                                  // --offset
                          printObjectListProperties.length ); // length

        // copy PrinterList properties
        System.arraycopy( prtDListProperties_,                // source
                          0,                                  // --offset
                          combinedProperties,                 // destination
                          printObjectListProperties.length,   // --offset
                          prtDListProperties_.length );       // length

        return combinedProperties;
    }

    /**
      * Returns an Image for this bean's icon.
      * @param icon The desired icon size and color.
      * @return The Image for the icon.
      **/
    public Image getIcon(int icon)
    {
        Image image = null;

        switch(icon)
        {
            case BeanInfo.ICON_MONO_16x16:
            case BeanInfo.ICON_COLOR_16x16:
                image = loadImage("PrinterList16.gif");
                break;
            case BeanInfo.ICON_MONO_32x32:
            case BeanInfo.ICON_COLOR_32x32:
                image = loadImage("PrinterList32.gif");
                break;
        }

        return image;
    }

}

