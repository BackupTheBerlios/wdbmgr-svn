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
import java.util.Enumeration;
import java.util.Vector;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Root for the definition of the WDB file
 * 
 * @version $Revision$ $Date$
 */
public class WdbDef implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * unique id for the file
     */
    private com.gele.tools.wow.wdbearmanager.castor.WdbId _wdbId;

    /**
     * Skip this number of bytes from the beginning of the WDB
     * file. For PRE 1.6.0 this is 16, 1.6.0 is 20
     */
    private com.gele.tools.wow.wdbearmanager.castor.SkipBytes _skipBytes;

    /**
     * This is the EOF marker, for a WDB this is 8 times 00 (hex
     * value)
     */
    private com.gele.tools.wow.wdbearmanager.castor.EofMarker _eofMarker;

    /**
     * Field _wdbElementList
     */
    private java.util.Vector _wdbElementList;


      //----------------/
     //- Constructors -/
    //----------------/

    public WdbDef() 
     {
        super();
        _wdbElementList = new Vector();
    } //-- com.gele.tools.wow.wdbearmanager.castor.WdbDef()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addWdbElement
     * 
     * 
     * 
     * @param vWdbElement
     */
    public void addWdbElement(com.gele.tools.wow.wdbearmanager.castor.WdbElement vWdbElement)
        throws java.lang.IndexOutOfBoundsException
    {
        _wdbElementList.addElement(vWdbElement);
    } //-- void addWdbElement(com.gele.tools.wow.wdbearmanager.castor.WdbElement) 

    /**
     * Method addWdbElement
     * 
     * 
     * 
     * @param index
     * @param vWdbElement
     */
    public void addWdbElement(int index, com.gele.tools.wow.wdbearmanager.castor.WdbElement vWdbElement)
        throws java.lang.IndexOutOfBoundsException
    {
        _wdbElementList.insertElementAt(vWdbElement, index);
    } //-- void addWdbElement(int, com.gele.tools.wow.wdbearmanager.castor.WdbElement) 

    /**
     * Method enumerateWdbElement
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateWdbElement()
    {
        return _wdbElementList.elements();
    } //-- java.util.Enumeration enumerateWdbElement() 

    /**
     * Returns the value of field 'eofMarker'. The field
     * 'eofMarker' has the following description: This is the EOF
     * marker, for a WDB this is 8 times 00 (hex value)
     * 
     * @return EofMarker
     * @return the value of field 'eofMarker'.
     */
    public com.gele.tools.wow.wdbearmanager.castor.EofMarker getEofMarker()
    {
        return this._eofMarker;
    } //-- com.gele.tools.wow.wdbearmanager.castor.EofMarker getEofMarker() 

    /**
     * Returns the value of field 'skipBytes'. The field
     * 'skipBytes' has the following description: Skip this number
     * of bytes from the beginning of the WDB file. For PRE 1.6.0
     * this is 16, 1.6.0 is 20
     * 
     * @return SkipBytes
     * @return the value of field 'skipBytes'.
     */
    public com.gele.tools.wow.wdbearmanager.castor.SkipBytes getSkipBytes()
    {
        return this._skipBytes;
    } //-- com.gele.tools.wow.wdbearmanager.castor.SkipBytes getSkipBytes() 

    /**
     * Method getWdbElement
     * 
     * 
     * 
     * @param index
     * @return WdbElement
     */
    public com.gele.tools.wow.wdbearmanager.castor.WdbElement getWdbElement(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _wdbElementList.size())) {
            throw new IndexOutOfBoundsException("getWdbElement: Index value '"+index+"' not in range [0.."+_wdbElementList.size()+ "]");
        }
        
        return (com.gele.tools.wow.wdbearmanager.castor.WdbElement) _wdbElementList.elementAt(index);
    } //-- com.gele.tools.wow.wdbearmanager.castor.WdbElement getWdbElement(int) 

    /**
     * Method getWdbElement
     * 
     * 
     * 
     * @return WdbElement
     */
    public com.gele.tools.wow.wdbearmanager.castor.WdbElement[] getWdbElement()
    {
        int size = _wdbElementList.size();
        com.gele.tools.wow.wdbearmanager.castor.WdbElement[] mArray = new com.gele.tools.wow.wdbearmanager.castor.WdbElement[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.gele.tools.wow.wdbearmanager.castor.WdbElement) _wdbElementList.elementAt(index);
        }
        return mArray;
    } //-- com.gele.tools.wow.wdbearmanager.castor.WdbElement[] getWdbElement() 

    /**
     * Method getWdbElementCount
     * 
     * 
     * 
     * @return int
     */
    public int getWdbElementCount()
    {
        return _wdbElementList.size();
    } //-- int getWdbElementCount() 

    /**
     * Returns the value of field 'wdbId'. The field 'wdbId' has
     * the following description: unique id for the file
     * 
     * @return WdbId
     * @return the value of field 'wdbId'.
     */
    public com.gele.tools.wow.wdbearmanager.castor.WdbId getWdbId()
    {
        return this._wdbId;
    } //-- com.gele.tools.wow.wdbearmanager.castor.WdbId getWdbId() 

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
     * Method removeAllWdbElement
     * 
     */
    public void removeAllWdbElement()
    {
        _wdbElementList.removeAllElements();
    } //-- void removeAllWdbElement() 

    /**
     * Method removeWdbElement
     * 
     * 
     * 
     * @param index
     * @return WdbElement
     */
    public com.gele.tools.wow.wdbearmanager.castor.WdbElement removeWdbElement(int index)
    {
        java.lang.Object obj = _wdbElementList.elementAt(index);
        _wdbElementList.removeElementAt(index);
        return (com.gele.tools.wow.wdbearmanager.castor.WdbElement) obj;
    } //-- com.gele.tools.wow.wdbearmanager.castor.WdbElement removeWdbElement(int) 

    /**
     * Sets the value of field 'eofMarker'. The field 'eofMarker'
     * has the following description: This is the EOF marker, for a
     * WDB this is 8 times 00 (hex value)
     * 
     * @param eofMarker the value of field 'eofMarker'.
     */
    public void setEofMarker(com.gele.tools.wow.wdbearmanager.castor.EofMarker eofMarker)
    {
        this._eofMarker = eofMarker;
    } //-- void setEofMarker(com.gele.tools.wow.wdbearmanager.castor.EofMarker) 

    /**
     * Sets the value of field 'skipBytes'. The field 'skipBytes'
     * has the following description: Skip this number of bytes
     * from the beginning of the WDB file. For PRE 1.6.0 this is
     * 16, 1.6.0 is 20
     * 
     * @param skipBytes the value of field 'skipBytes'.
     */
    public void setSkipBytes(com.gele.tools.wow.wdbearmanager.castor.SkipBytes skipBytes)
    {
        this._skipBytes = skipBytes;
    } //-- void setSkipBytes(com.gele.tools.wow.wdbearmanager.castor.SkipBytes) 

    /**
     * Method setWdbElement
     * 
     * 
     * 
     * @param index
     * @param vWdbElement
     */
    public void setWdbElement(int index, com.gele.tools.wow.wdbearmanager.castor.WdbElement vWdbElement)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _wdbElementList.size())) {
            throw new IndexOutOfBoundsException("setWdbElement: Index value '"+index+"' not in range [0.."+_wdbElementList.size()+ "]");
        }
        _wdbElementList.setElementAt(vWdbElement, index);
    } //-- void setWdbElement(int, com.gele.tools.wow.wdbearmanager.castor.WdbElement) 

    /**
     * Method setWdbElement
     * 
     * 
     * 
     * @param wdbElementArray
     */
    public void setWdbElement(com.gele.tools.wow.wdbearmanager.castor.WdbElement[] wdbElementArray)
    {
        //-- copy array
        _wdbElementList.removeAllElements();
        for (int i = 0; i < wdbElementArray.length; i++) {
            _wdbElementList.addElement(wdbElementArray[i]);
        }
    } //-- void setWdbElement(com.gele.tools.wow.wdbearmanager.castor.WdbElement) 

    /**
     * Sets the value of field 'wdbId'. The field 'wdbId' has the
     * following description: unique id for the file
     * 
     * @param wdbId the value of field 'wdbId'.
     */
    public void setWdbId(com.gele.tools.wow.wdbearmanager.castor.WdbId wdbId)
    {
        this._wdbId = wdbId;
    } //-- void setWdbId(com.gele.tools.wow.wdbearmanager.castor.WdbId) 

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
        return (com.gele.tools.wow.wdbearmanager.castor.WdbDef) Unmarshaller.unmarshal(com.gele.tools.wow.wdbearmanager.castor.WdbDef.class, reader);
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
