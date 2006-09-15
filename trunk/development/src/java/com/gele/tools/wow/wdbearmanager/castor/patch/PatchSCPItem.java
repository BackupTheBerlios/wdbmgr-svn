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

import java.io.Serializable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class PatchSCPItem.
 * 
 * @version $Revision$ $Date$
 */
public class PatchSCPItem implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _patchConfig
     */
    private com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig _patchConfig;


      //----------------/
     //- Constructors -/
    //----------------/

    public PatchSCPItem() 
     {
        super();
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'patchConfig'.
     * 
     * @return PatchConfig
     * @return the value of field 'patchConfig'.
     */
    public com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig getPatchConfig()
    {
        return this._patchConfig;
    } //-- com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig getPatchConfig() 

    /**
     * Sets the value of field 'patchConfig'.
     * 
     * @param patchConfig the value of field 'patchConfig'.
     */
    public void setPatchConfig(com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig patchConfig)
    {
        this._patchConfig = patchConfig;
    } //-- void setPatchConfig(com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig) 

}
