/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package com.gele.tools.wow.wdbearmanager.castor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Skip this number of bytes from the beginning of the WDB file.
 * For PRE 1.6.0 this is 16, 1.6.0 is 20
 * 
 * @version $Revision$ $Date$
 */
public class SkipBytes implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _numBytes
     */
    private int _numBytes;

    /**
     * keeps track of state for field: _numBytes
     */
    private boolean _has_numBytes;


      //----------------/
     //- Constructors -/
    //----------------/

    public SkipBytes() 
     {
        super();
    } //-- com.gele.tools.wow.wdbearmanager.castor.SkipBytes()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteNumBytes
     * 
     */
    public void deleteNumBytes()
    {
        this._has_numBytes= false;
    } //-- void deleteNumBytes() 

    /**
     * Returns the value of field 'numBytes'.
     * 
     * @return int
     * @return the value of field 'numBytes'.
     */
    public int getNumBytes()
    {
        return this._numBytes;
    } //-- int getNumBytes() 

    /**
     * Method hasNumBytes
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasNumBytes()
    {
        return this._has_numBytes;
    } //-- boolean hasNumBytes() 

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
     * Sets the value of field 'numBytes'.
     * 
     * @param numBytes the value of field 'numBytes'.
     */
    public void setNumBytes(int numBytes)
    {
        this._numBytes = numBytes;
        this._has_numBytes = true;
    } //-- void setNumBytes(int) 

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
        return (com.gele.tools.wow.wdbearmanager.castor.SkipBytes) Unmarshaller.unmarshal(com.gele.tools.wow.wdbearmanager.castor.SkipBytes.class, reader);
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
