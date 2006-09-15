/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.gele.tools.wow.wdbearmanager.castor.patch;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Line.
 * 
 * @version $Revision$ $Date$
 */
public class Line implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Specify a value from the WDBear database to be used
     */
    private java.util.Vector _destinationList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Line() 
     {
        super();
        _destinationList = new Vector();
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Line()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addDestination
     * 
     * 
     * 
     * @param vDestination
     */
    public void addDestination(com.gele.tools.wow.wdbearmanager.castor.patch.Destination vDestination)
        throws java.lang.IndexOutOfBoundsException
    {
        _destinationList.addElement(vDestination);
    } //-- void addDestination(com.gele.tools.wow.wdbearmanager.castor.patch.Destination) 

    /**
     * Method addDestination
     * 
     * 
     * 
     * @param index
     * @param vDestination
     */
    public void addDestination(int index, com.gele.tools.wow.wdbearmanager.castor.patch.Destination vDestination)
        throws java.lang.IndexOutOfBoundsException
    {
        _destinationList.insertElementAt(vDestination, index);
    } //-- void addDestination(int, com.gele.tools.wow.wdbearmanager.castor.patch.Destination) 

    /**
     * Method enumerateDestination
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateDestination()
    {
        return _destinationList.elements();
    } //-- java.util.Enumeration enumerateDestination() 

    /**
     * Method getDestination
     * 
     * 
     * 
     * @param index
     * @return Destination
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Destination getDestination(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _destinationList.size())) {
            throw new IndexOutOfBoundsException("getDestination: Index value '"+index+"' not in range [0.."+_destinationList.size()+ "]");
        }
        
        return (com.gele.tools.wow.wdbearmanager.castor.patch.Destination) _destinationList.elementAt(index);
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Destination getDestination(int) 

    /**
     * Method getDestination
     * 
     * 
     * 
     * @return Destination
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Destination[] getDestination()
    {
        int size = _destinationList.size();
        com.gele.tools.wow.wdbearmanager.castor.patch.Destination[] mArray = new com.gele.tools.wow.wdbearmanager.castor.patch.Destination[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.gele.tools.wow.wdbearmanager.castor.patch.Destination) _destinationList.elementAt(index);
        }
        return mArray;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Destination[] getDestination() 

    /**
     * Method getDestinationCount
     * 
     * 
     * 
     * @return int
     */
    public int getDestinationCount()
    {
        return _destinationList.size();
    } //-- int getDestinationCount() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeAllDestination
     * 
     */
    public void removeAllDestination()
    {
        _destinationList.removeAllElements();
    } //-- void removeAllDestination() 

    /**
     * Method removeDestination
     * 
     * 
     * 
     * @param index
     * @return Destination
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Destination removeDestination(int index)
    {
        java.lang.Object obj = _destinationList.elementAt(index);
        _destinationList.removeElementAt(index);
        return (com.gele.tools.wow.wdbearmanager.castor.patch.Destination) obj;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Destination removeDestination(int) 

    /**
     * Method setDestination
     * 
     * 
     * 
     * @param index
     * @param vDestination
     */
    public void setDestination(int index, com.gele.tools.wow.wdbearmanager.castor.patch.Destination vDestination)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _destinationList.size())) {
            throw new IndexOutOfBoundsException("setDestination: Index value '"+index+"' not in range [0.."+_destinationList.size()+ "]");
        }
        _destinationList.setElementAt(vDestination, index);
    } //-- void setDestination(int, com.gele.tools.wow.wdbearmanager.castor.patch.Destination) 

    /**
     * Method setDestination
     * 
     * 
     * 
     * @param destinationArray
     */
    public void setDestination(com.gele.tools.wow.wdbearmanager.castor.patch.Destination[] destinationArray)
    {
        //-- copy array
        _destinationList.removeAllElements();
        for (int i = 0; i < destinationArray.length; i++) {
            _destinationList.addElement(destinationArray[i]);
        }
    } //-- void setDestination(com.gele.tools.wow.wdbearmanager.castor.patch.Destination) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.gele.tools.wow.wdbearmanager.castor.patch.Line) Unmarshaller.unmarshal(com.gele.tools.wow.wdbearmanager.castor.patch.Line.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
