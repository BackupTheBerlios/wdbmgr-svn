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
 * Class Replace.
 * 
 * @version $Revision$ $Date$
 */
public class Replace implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _source
     */
    private java.lang.String _source;

    /**
     * Field _multiline
     */
    private java.lang.String _multiline = "0";

    /**
     * Field _lineList
     */
    private java.util.Vector _lineList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Replace() 
     {
        super();
        setMultiline("0");
        _lineList = new Vector();
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Replace()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addLine
     * 
     * 
     * 
     * @param vLine
     */
    public void addLine(com.gele.tools.wow.wdbearmanager.castor.patch.Line vLine)
        throws java.lang.IndexOutOfBoundsException
    {
        _lineList.addElement(vLine);
    } //-- void addLine(com.gele.tools.wow.wdbearmanager.castor.patch.Line) 

    /**
     * Method addLine
     * 
     * 
     * 
     * @param index
     * @param vLine
     */
    public void addLine(int index, com.gele.tools.wow.wdbearmanager.castor.patch.Line vLine)
        throws java.lang.IndexOutOfBoundsException
    {
        _lineList.insertElementAt(vLine, index);
    } //-- void addLine(int, com.gele.tools.wow.wdbearmanager.castor.patch.Line) 

    /**
     * Method enumerateLine
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateLine()
    {
        return _lineList.elements();
    } //-- java.util.Enumeration enumerateLine() 

    /**
     * Method getLine
     * 
     * 
     * 
     * @param index
     * @return Line
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Line getLine(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _lineList.size())) {
            throw new IndexOutOfBoundsException("getLine: Index value '"+index+"' not in range [0.."+_lineList.size()+ "]");
        }
        
        return (com.gele.tools.wow.wdbearmanager.castor.patch.Line) _lineList.elementAt(index);
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Line getLine(int) 

    /**
     * Method getLine
     * 
     * 
     * 
     * @return Line
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Line[] getLine()
    {
        int size = _lineList.size();
        com.gele.tools.wow.wdbearmanager.castor.patch.Line[] mArray = new com.gele.tools.wow.wdbearmanager.castor.patch.Line[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.gele.tools.wow.wdbearmanager.castor.patch.Line) _lineList.elementAt(index);
        }
        return mArray;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Line[] getLine() 

    /**
     * Method getLineCount
     * 
     * 
     * 
     * @return int
     */
    public int getLineCount()
    {
        return _lineList.size();
    } //-- int getLineCount() 

    /**
     * Returns the value of field 'multiline'.
     * 
     * @return String
     * @return the value of field 'multiline'.
     */
    public java.lang.String getMultiline()
    {
        return this._multiline;
    } //-- java.lang.String getMultiline() 

    /**
     * Returns the value of field 'source'.
     * 
     * @return String
     * @return the value of field 'source'.
     */
    public java.lang.String getSource()
    {
        return this._source;
    } //-- java.lang.String getSource() 

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
     * Method removeAllLine
     * 
     */
    public void removeAllLine()
    {
        _lineList.removeAllElements();
    } //-- void removeAllLine() 

    /**
     * Method removeLine
     * 
     * 
     * 
     * @param index
     * @return Line
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.Line removeLine(int index)
    {
        java.lang.Object obj = _lineList.elementAt(index);
        _lineList.removeElementAt(index);
        return (com.gele.tools.wow.wdbearmanager.castor.patch.Line) obj;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.Line removeLine(int) 

    /**
     * Method setLine
     * 
     * 
     * 
     * @param index
     * @param vLine
     */
    public void setLine(int index, com.gele.tools.wow.wdbearmanager.castor.patch.Line vLine)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _lineList.size())) {
            throw new IndexOutOfBoundsException("setLine: Index value '"+index+"' not in range [0.."+_lineList.size()+ "]");
        }
        _lineList.setElementAt(vLine, index);
    } //-- void setLine(int, com.gele.tools.wow.wdbearmanager.castor.patch.Line) 

    /**
     * Method setLine
     * 
     * 
     * 
     * @param lineArray
     */
    public void setLine(com.gele.tools.wow.wdbearmanager.castor.patch.Line[] lineArray)
    {
        //-- copy array
        _lineList.removeAllElements();
        for (int i = 0; i < lineArray.length; i++) {
            _lineList.addElement(lineArray[i]);
        }
    } //-- void setLine(com.gele.tools.wow.wdbearmanager.castor.patch.Line) 

    /**
     * Sets the value of field 'multiline'.
     * 
     * @param multiline the value of field 'multiline'.
     */
    public void setMultiline(java.lang.String multiline)
    {
        this._multiline = multiline;
    } //-- void setMultiline(java.lang.String) 

    /**
     * Sets the value of field 'source'.
     * 
     * @param source the value of field 'source'.
     */
    public void setSource(java.lang.String source)
    {
        this._source = source;
    } //-- void setSource(java.lang.String) 

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
        return (com.gele.tools.wow.wdbearmanager.castor.patch.Replace) Unmarshaller.unmarshal(com.gele.tools.wow.wdbearmanager.castor.patch.Replace.class, reader);
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
