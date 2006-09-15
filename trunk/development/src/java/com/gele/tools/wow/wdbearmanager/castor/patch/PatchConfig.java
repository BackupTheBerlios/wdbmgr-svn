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
 * Class PatchConfig.
 * 
 * @version $Revision$ $Date$
 */
public class PatchConfig implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * something like [quest, eg
     */
    private com.gele.tools.wow.wdbearmanager.castor.patch.Marker _marker;

    /**
     * config file containing the XML description of the database
     */
    private com.gele.tools.wow.wdbearmanager.castor.patch.XmlConfig _xmlConfig;

    /**
     * Field _replaceList
     */
    private java.util.Vector _replaceList;


      //----------------/
     //- Constructors -/
    //----------------/

    public PatchConfig() 
     {
        super();
        _replaceList = new Vector();
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addReplace
     * 
     * 
     * 
     * @param vReplace
     */
    public void addReplace(com.gele.tools.wow.wdbearmanager.castor.patch.Replace vReplace)
        throws java.lang.IndexOutOfBoundsException
    {
        _replaceList.addElement(vReplace);
    } //-- void addReplace(com.gele.tools.wow.wdbearmanager.castor.patch.Replace) 

    /**
     * Method addReplace
     * 
     * 
     * 
     * @param index
     * @param vReplace
     */
    public void addReplace(int index, com.gele.tools.wow.wdbearmanager.castor.patch.Replace vReplace)
        throws java.lang.IndexOutOfBoundsException
    {
        _replaceList.insertElementAt(vReplace, index);
    } //-- void addReplace(int, com.gele.tools.wow.wdbearmanager.castor.patch.Replace) 

    /**
     * Method enumerateReplace
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateReplace()
    {
        return _replaceList.elements();
    } //-- java.util.Enumeration enumerateReplace() 

    /**
     * Returns the value of field 'marker'. The field 'marker' has
     * the following description: something like [quest, eg
     * 
     * @return Marker
     * @return the value of field 'marker'.
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Marker getMarker()
    {
        return this._marker;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Marker getMarker() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

    /**
     * Method getReplace
     * 
     * 
     * 
     * @param index
     * @return Replace
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Replace getReplace(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _replaceList.size())) {
            throw new IndexOutOfBoundsException("getReplace: Index value '"+index+"' not in range [0.."+_replaceList.size()+ "]");
        }
        
        return (com.gele.tools.wow.wdbearmanager.castor.patch.Replace) _replaceList.elementAt(index);
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Replace getReplace(int) 

    /**
     * Method getReplace
     * 
     * 
     * 
     * @return Replace
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Replace[] getReplace()
    {
        int size = _replaceList.size();
        com.gele.tools.wow.wdbearmanager.castor.patch.Replace[] mArray = new com.gele.tools.wow.wdbearmanager.castor.patch.Replace[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.gele.tools.wow.wdbearmanager.castor.patch.Replace) _replaceList.elementAt(index);
        }
        return mArray;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Replace[] getReplace() 

    /**
     * Method getReplaceCount
     * 
     * 
     * 
     * @return int
     */
    public int getReplaceCount()
    {
        return _replaceList.size();
    } //-- int getReplaceCount() 

    /**
     * Returns the value of field 'xmlConfig'. The field
     * 'xmlConfig' has the following description: config file
     * containing the XML description of the database
     * 
     * @return XmlConfig
     * @return the value of field 'xmlConfig'.
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.XmlConfig getXmlConfig()
    {
        return this._xmlConfig;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.XmlConfig getXmlConfig() 

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
     * Method removeAllReplace
     * 
     */
    public void removeAllReplace()
    {
        _replaceList.removeAllElements();
    } //-- void removeAllReplace() 

    /**
     * Method removeReplace
     * 
     * 
     * 
     * @param index
     * @return Replace
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Replace removeReplace(int index)
    {
        java.lang.Object obj = _replaceList.elementAt(index);
        _replaceList.removeElementAt(index);
        return (com.gele.tools.wow.wdbearmanager.castor.patch.Replace) obj;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Replace removeReplace(int) 

    /**
     * Sets the value of field 'marker'. The field 'marker' has the
     * following description: something like [quest, eg
     * 
     * @param marker the value of field 'marker'.
     */
    public void setMarker(com.gele.tools.wow.wdbearmanager.castor.patch.Marker marker)
    {
        this._marker = marker;
    } //-- void setMarker(com.gele.tools.wow.wdbearmanager.castor.patch.Marker) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

    /**
     * Method setReplace
     * 
     * 
     * 
     * @param index
     * @param vReplace
     */
    public void setReplace(int index, com.gele.tools.wow.wdbearmanager.castor.patch.Replace vReplace)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _replaceList.size())) {
            throw new IndexOutOfBoundsException("setReplace: Index value '"+index+"' not in range [0.."+_replaceList.size()+ "]");
        }
        _replaceList.setElementAt(vReplace, index);
    } //-- void setReplace(int, com.gele.tools.wow.wdbearmanager.castor.patch.Replace) 

    /**
     * Method setReplace
     * 
     * 
     * 
     * @param replaceArray
     */
    public void setReplace(com.gele.tools.wow.wdbearmanager.castor.patch.Replace[] replaceArray)
    {
        //-- copy array
        _replaceList.removeAllElements();
        for (int i = 0; i < replaceArray.length; i++) {
            _replaceList.addElement(replaceArray[i]);
        }
    } //-- void setReplace(com.gele.tools.wow.wdbearmanager.castor.patch.Replace) 

    /**
     * Sets the value of field 'xmlConfig'. The field 'xmlConfig'
     * has the following description: config file containing the
     * XML description of the database
     * 
     * @param xmlConfig the value of field 'xmlConfig'.
     */
    public void setXmlConfig(com.gele.tools.wow.wdbearmanager.castor.patch.XmlConfig xmlConfig)
    {
        this._xmlConfig = xmlConfig;
    } //-- void setXmlConfig(com.gele.tools.wow.wdbearmanager.castor.patch.XmlConfig) 

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
        return (com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig) Unmarshaller.unmarshal(com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig.class, reader);
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
