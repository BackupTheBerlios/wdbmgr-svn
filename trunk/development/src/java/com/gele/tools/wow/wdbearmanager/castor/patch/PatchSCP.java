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
 * Root for the definition of the patch
 * 
 * @version $Revision$ $Date$
 */
public class PatchSCP implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _items
     */
    private java.util.Vector _items;


      //----------------/
     //- Constructors -/
    //----------------/

    public PatchSCP() 
     {
        super();
        _items = new Vector();
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCP()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPatchSCPItem
     * 
     * 
     * 
     * @param vPatchSCPItem
     */
    public void addPatchSCPItem(com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem vPatchSCPItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.addElement(vPatchSCPItem);
    } //-- void addPatchSCPItem(com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem) 

    /**
     * Method addPatchSCPItem
     * 
     * 
     * 
     * @param index
     * @param vPatchSCPItem
     */
    public void addPatchSCPItem(int index, com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem vPatchSCPItem)
        throws java.lang.IndexOutOfBoundsException
    {
        _items.insertElementAt(vPatchSCPItem, index);
    } //-- void addPatchSCPItem(int, com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem) 

    /**
     * Method enumeratePatchSCPItem
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumeratePatchSCPItem()
    {
        return _items.elements();
    } //-- java.util.Enumeration enumeratePatchSCPItem() 

    /**
     * Method getPatchSCPItem
     * 
     * 
     * 
     * @param index
     * @return PatchSCPItem
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem getPatchSCPItem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException("getPatchSCPItem: Index value '"+index+"' not in range [0.."+_items.size()+ "]");
        }
        
        return (com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem) _items.elementAt(index);
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem getPatchSCPItem(int) 

    /**
     * Method getPatchSCPItem
     * 
     * 
     * 
     * @return PatchSCPItem
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem[] getPatchSCPItem()
    {
        int size = _items.size();
        com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem[] mArray = new com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem) _items.elementAt(index);
        }
        return mArray;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem[] getPatchSCPItem() 

    /**
     * Method getPatchSCPItemCount
     * 
     * 
     * 
     * @return int
     */
    public int getPatchSCPItemCount()
    {
        return _items.size();
    } //-- int getPatchSCPItemCount() 

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
     * Method removeAllPatchSCPItem
     * 
     */
    public void removeAllPatchSCPItem()
    {
        _items.removeAllElements();
    } //-- void removeAllPatchSCPItem() 

    /**
     * Method removePatchSCPItem
     * 
     * 
     * 
     * @param index
     * @return PatchSCPItem
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem removePatchSCPItem(int index)
    {
        java.lang.Object obj = _items.elementAt(index);
        _items.removeElementAt(index);
        return (com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem) obj;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem removePatchSCPItem(int) 

    /**
     * Method setPatchSCPItem
     * 
     * 
     * 
     * @param index
     * @param vPatchSCPItem
     */
    public void setPatchSCPItem(int index, com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem vPatchSCPItem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _items.size())) {
            throw new IndexOutOfBoundsException("setPatchSCPItem: Index value '"+index+"' not in range [0.."+_items.size()+ "]");
        }
        _items.setElementAt(vPatchSCPItem, index);
    } //-- void setPatchSCPItem(int, com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem) 

    /**
     * Method setPatchSCPItem
     * 
     * 
     * 
     * @param patchSCPItemArray
     */
    public void setPatchSCPItem(com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem[] patchSCPItemArray)
    {
        //-- copy array
        _items.removeAllElements();
        for (int i = 0; i < patchSCPItemArray.length; i++) {
            _items.addElement(patchSCPItemArray[i]);
        }
    } //-- void setPatchSCPItem(com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem) 

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
        return (com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCP) Unmarshaller.unmarshal(com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCP.class, reader);
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
