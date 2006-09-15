/*
 * Created on 15.03.2005
 *
 * Helper methods that are needed throughout the project<br/>
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDB_Helper.java 216 2006-09-10 16:55:52Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-10 18:55:52 +0200 (So, 10 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 216 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/helper/WDB_Helper.java $
 * $Rev: 216 $
 */
package com.gele.tools.wow.wdbearmanager.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Types;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.WDB_DTO;
import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.castor.EofMarker;
import com.gele.tools.wow.wdbearmanager.castor.SkipBytes;
import com.gele.tools.wow.wdbearmanager.castor.WdbDef;
import com.gele.tools.wow.wdbearmanager.castor.WdbElement;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCP;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorInsideXMLConfigFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorReadingXMLConfigFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_FileNotFound;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoXMLConfigFileForThisWDBException;

public class WDB_Helper {

  static Logger myLogger = Logger.getLogger(WDB_Helper.class);

  /** This file contains the patch scripts */
  public static String PATCH_XML = "patchSCP.xml";

  /** Client version, eg 4211 (Means "1.2.3")*/ 
  public static String INF_VERSION = "inf_version";
  /** Client locale, enGB, enUS, deDE etc */ 
  public static String INF_LOCALE = "inf_locale";
  /** ALL - if the user selects a locale, ALL means, that the selection
   * is for all locales, not for a specific one.
   */
  public static String INF_LOCALE_ALL = "ALL";

  /**
   * For every WDB file there exists a specific XML config file<br/>
   * Return the
   * name of the XML config file for a specific *.wdb file <br/> <br/>
   * 
   * 
   * If there is no config file for a given *.wdb filename, throw Exception<br/>
   * 
   * @param parFilename
   *          name of the wdb file, !with full path!
   * 
   * @return name of the config file
   * 
   * @throws WDBMgr_NoXMLConfigFileForThisWDBException
   */
  public static String determineXMLconfigForWDB(WDBearManager_I parAPI,
      String parFilename) throws WDBMgr_NoXMLConfigFileForThisWDBException {
    // String xmlConfig = "";
    File tmpFile = new File(parFilename);

    String wdbFileName = tmpFile.getName();
    // Check if we find the XML config file
    String wdbFileLOWER = wdbFileName.toLowerCase();
    if (wdbFileLOWER.endsWith(".wdb")) {
      wdbFileName = wdbFileName.substring(0, wdbFileName.length() - 4);
    } else {
      throw new WDBMgr_NoXMLConfigFileForThisWDBException(
          "Config file error: Please select a WDB file");
    }

    // Determine version of the WDB file
    // Identify the version of the WDB file
    WDBInfo myWDBInfo = null;
    try {
      myWDBInfo = parAPI.getWDBInfo(parFilename);
    } catch (Exception ex) {
      throw new WDBMgr_NoXMLConfigFileForThisWDBException(
          "Error retrieving information about the WDB file...\n"
              + "Please check the WDB file");
    }

    try {
      WDB_Helper.readXML4WDB(WDBearManager.WDBEAR_CONFIG + "/" + wdbFileName
          + ".xml");
    } catch (WDBMgr_ErrorReadingXMLConfigFile ex) {
      throw new WDBMgr_NoXMLConfigFileForThisWDBException(
          "No config file for '" + parFilename + "'");
    }

    /*
     * if (wdbFile.equalsIgnoreCase("creaturecache.wdb")) { xmlConfig =
     * "creaturecache.xml"; } else if
     * (wdbFile.equalsIgnoreCase("gameobjectcache.wdb")) { xmlConfig =
     * "gameobjectcache.xml"; } else if
     * (wdbFile.equalsIgnoreCase("itemcache.wdb")) { xmlConfig =
     * "itemcache.xml"; } else if
     * (wdbFile.equalsIgnoreCase("itemtextcaxhe.wdb")) { xmlConfig =
     * "itemtextcaxhe.xml"; } else if (wdbFile.equalsIgnoreCase("npccache.wdb")) {
     * xmlConfig = "npccache.xml"; } else if
     * (wdbFile.equalsIgnoreCase("pagetextcache.wdb")) { xmlConfig =
     * "pagetextcache.xml"; } else if
     * (wdbFile.equalsIgnoreCase("questcache.wdb")) { xmlConfig =
     * "questcache.xml"; } else if
     * (wdbFile.equalsIgnoreCase("itemnamecache.wdb")) { xmlConfig =
     * "itemnamecache.xml"; } else { throw new
     * WDBMgr_NoXMLConfigFileForThisWDBException("No config file for '" +
     * parFilename + "'"); }
     */

    return myWDBInfo.getCompatibilityVersion()+"/"+wdbFileName + ".xml";
  }// determineXMLconfig

  /**
   * Create a DTO (columns, table name) from a given XML config file<br/>
   * 
   * @param parClass
   *          create Read XML config file relatively to this class if it is
   *          NULL, read XML config file "absolutely" ("/")
   * 
   * @param parXMLConfig
   *          name of the config file
   * 
   * @return instance of the DTO
   * 
   * @throws WDBMgr_ErrorReadingXMLConfigFile
   *           error creating the XML config file
   * 
   * @throws WDBMgr_ErrorInsideXMLConfigFile
   *           the XML config file contained wrong types
   */
  public static WDB_DTO createDTOfromXML(Class parClass, String parXMLConfig)
      throws WDBMgr_ErrorReadingXMLConfigFile, WDBMgr_ErrorInsideXMLConfigFile {

    WdbDef myWdbDef = null;
    try {
      if (parClass != null) {
        myLogger
            .debug("createDTOfromXML: Try to read it using provided class: "
                + parClass.getName());
        myWdbDef = WDB_Helper.readXML4WDB(parClass, parXMLConfig);
      } else {
        myLogger.debug("createDTOfromXML: Try to read it using file system");
        myWdbDef = WDB_Helper.readXML4WDB(parXMLConfig);
      }
    } catch (WDBMgr_ErrorReadingXMLConfigFile ex) {
      // K, try to read them inside "wdb-config/"
      myLogger.debug("Try to read it using " + WDBearManager.WDBEAR_CONFIG
          + "/");

      if (parClass != null) {
        myWdbDef = WDB_Helper.readXML4WDB(parClass, WDBearManager.WDBEAR_CONFIG
            + "/" + parXMLConfig);
      } else {
        myWdbDef = WDB_Helper.readXML4WDB(WDBearManager.WDBEAR_CONFIG + "/"
            + parXMLConfig);
      }
    }
    myLogger.debug("createDTOfromXML: got it myWdbDef : " + myWdbDef);

    // Create the DTO that will keep all information
    WDB_DTO myWDB_DTO = new WDB_DTO();
    myWDB_DTO.setTableName(myWdbDef.getWdbId().getName());
    // Number of bytes to be skipped in the beginning of the WDB file
    SkipBytes mySkipBytes = myWdbDef.getSkipBytes();
    if (mySkipBytes != null) {
      myWDB_DTO.setSkipBytes(mySkipBytes.getNumBytes());
    } else {
      // do nothing
      // default value is 16
    }
    EofMarker myEofMarker = myWdbDef.getEofMarker();
    if (myEofMarker != null) {
      String hexValue = myEofMarker.getValue();
      // convert String to byte
      if ((hexValue.length() % 2) != 0) {
        throw new WDBMgr_ErrorInsideXMLConfigFile("File: '"
            + WDBearManager.WDBEAR_CONFIG + "/" + parXMLConfig + "'\n"
            + "Number of values must be an even number "
            + "Please check your XML specs, EofMarker, value: '" + hexValue
            + "'");
      }
      int numBytes = hexValue.length() / 2;
      byte[] endMarker = new byte[numBytes];
      for (int i = 0; i < numBytes; i++) {
        endMarker[i] = (byte) Integer.parseInt(hexValue.substring(i * 2,
            (i * 2) + 2), 16);
      }
      myWDB_DTO.setEofMarker(endMarker);
    } else {
      // do nothing
      // default value is 16
    }

    WdbElement[] wdbEls = myWdbDef.getWdbElement();
    for (int i = 0; i < wdbEls.length; i++) {
      if (wdbEls[i].getType().equalsIgnoreCase("varchar")) {
        myWDB_DTO.addColumn(wdbEls[i].getName(), Types.VARCHAR);
        if (wdbEls[i].getSize() != 0) {
          myWDB_DTO.setPrecision(wdbEls[i].getSize());
        }
      } else if (wdbEls[i].getType().equalsIgnoreCase("tinyInt")) {
        // 1 byte integer / signed
        myWDB_DTO.addColumn(wdbEls[i].getName(), Types.TINYINT);
      } else if (wdbEls[i].getType().equalsIgnoreCase("smallInt")) {
        // 2 byte integer / signed
        myWDB_DTO.addColumn(wdbEls[i].getName(), Types.SMALLINT);
      } else if (wdbEls[i].getType().equalsIgnoreCase("integer")) {
        // 4 byte integer / signed
        myWDB_DTO.addColumn(wdbEls[i].getName(), Types.INTEGER);
      } else if (wdbEls[i].getType().equalsIgnoreCase("single")) {
        // single - single prec floating point
        myWDB_DTO.addColumn(wdbEls[i].getName(), Types.FLOAT);
      } else if (wdbEls[i].getType().equalsIgnoreCase("bigIntHex")) {
        // bigInt, conv to HEX
        myWDB_DTO.addColumn(wdbEls[i].getName(), Types.NUMERIC);
      } else if (wdbEls[i].getType().equalsIgnoreCase("char")) {
        myWDB_DTO.addColumn(wdbEls[i].getName(), Types.CHAR);
        if (wdbEls[i].getSize() != 0) {
          myWDB_DTO.setPrecision(wdbEls[i].getSize());
        } else {
          throw new WDBMgr_ErrorInsideXMLConfigFile("File: '"
              + WDBearManager.WDBEAR_CONFIG + "/" + parXMLConfig + "'\n"
              + "Missing SIZE for type char: "
              + "Please check your XML specs, column: '" + wdbEls[i].getName()
              + "'");
        }
      } else {
        throw new WDBMgr_ErrorInsideXMLConfigFile("File: '" + parXMLConfig
            + "'; Unknown type: '" + wdbEls[i].getType()
            + "', please check your XML specs");
      }
      if (i == 0) {
        myWDB_DTO.addPrimaryKeyColumn(wdbEls[i].getName());
      }
    }// loop... all defined entries
    
    // Add additional information:
    // - version of the WDB file
    // - client language (deDE, enUS, enGB, etc)
    myWDB_DTO.addColumn(INF_VERSION, Types.VARCHAR);
    myWDB_DTO.addColumn(INF_LOCALE, Types.VARCHAR);
    myWDB_DTO.addPrimaryKeyColumn(INF_LOCALE);
    
//    for( int i = 0; i<myWDB_DTO.getPrimaryKeyColumns().length; i++ ) {
//      .out.println("O UT: PK: "+ myWDB_DTO.getPrimaryKeyColumns()[i]);
//      .out.println("O UT: VAL: "+ myWDB_DTO.getColumnValue( myWDB_DTO.getPrimaryKeyColumns()[i] ));
//    }


    return myWDB_DTO;

  }// createDTOfromXML


  /**
   * Read the config file that is stored inside the same package/dir as the
   * class, that is provided<br/>
   * 
   * @param parClass
   *          read XML file relative to class, quite handy if you don't want to
   *          place all your stuff in the root dir ("/")
   * 
   * @param parXMLfile
   *          name of the XML file that should be parsed
   * 
   * @return Instance of the (Castor) class to access the XML config
   * 
   * @throws WDBMgr_ErrorReadingXMLConfigFile
   *           Config file could not be read, maybe not found, malformed, etc.
   */

  public static WdbDef readXML4WDB(Class parClass, String parXMLfile)
      throws WDBMgr_ErrorReadingXMLConfigFile {
    WdbDef myWdbDef = null;
    InputStream ct_insInput = null;
    try {
      String pkg = parClass.getPackage().getName();
      pkg = pkg.replace('.', File.separatorChar);
      File myFile = new File(pkg, parXMLfile);
      ct_insInput = new FileInputStream(myFile);
      myWdbDef = (WdbDef) WdbDef.unmarshal(new BufferedReader(
          new InputStreamReader(ct_insInput)));
      ct_insInput.close();
    } catch (Exception ex) {
      // does not work as a filename, maybe inside a JAR -> try it as a resource
      try {
        ct_insInput = parClass.getResourceAsStream(parXMLfile);
        myWdbDef = (WdbDef) WdbDef.unmarshal(new BufferedReader(
            new InputStreamReader(ct_insInput)));
        ct_insInput.close();
      } catch (NullPointerException exx) {
        // exx.printStackTrace();
        throw new WDBMgr_ErrorReadingXMLConfigFile(parXMLfile
            + ": file not found." + exx.getLocalizedMessage(), exx);
      } catch (Exception exx) {
        // exx.printStackTrace();
        throw new WDBMgr_ErrorReadingXMLConfigFile(
            "Could not locate file: " + parXMLfile + "\n"
                + " Tried to read it as a file '"
                + new File(parXMLfile).getAbsolutePath()
                + "' and as a resource.\n", exx);
      }
    }// could not read as file
    if (ct_insInput == null) {
      throw new WDBMgr_ErrorReadingXMLConfigFile(
          "Error reading XML config file: '" + parXMLfile + "'");
    } else {
      return myWdbDef;
    }
  }// readXML4WDB -> relative to class

  /**
   * Read the config file using Castor generated class Ipal_Imp_Stv_Konfig<br/>
   * 
   * @return instance of WdbDef
   * 
   * @throws STVI_ErrorReadingConfigFile
   *           if the system could not locate/read the XML config file
   */
  public static WdbDef readXML4WDB(String parXMLfile)
      throws WDBMgr_ErrorReadingXMLConfigFile {
    WdbDef myWdbDef = null;
    // XML config file for STV import
    InputStream ct_insInput = null;
    try {
      // .err.println(new File(parXMLfile).getAbsolutePath());
      ct_insInput = new FileInputStream(new File(parXMLfile));
      myWdbDef = (WdbDef) WdbDef.unmarshal(new BufferedReader(
          new InputStreamReader(ct_insInput)));
      ct_insInput.close();
    } catch (Exception ex) {
      // .err.println("geht nicht: "+ parXMLfile);
      // ex.printStackTrace();
      // System.exit(0);

      // does not work as a filename, maybe inside a JAR -> try it as a resource
      try {
        ct_insInput = WDB_Helper.class.getResourceAsStream("/" + parXMLfile);
        // .err.println("ct_insInput: "+ct_insInput);
        myWdbDef = (WdbDef) WdbDef.unmarshal(new BufferedReader(
            new InputStreamReader(ct_insInput)));
        ct_insInput.close();
      } catch (Throwable exx) {
        // .err.println("Nicht mal als ress: "+ parXMLfile);
        // exx.printStackTrace();
        // System.exit(0);
        throw new WDBMgr_ErrorReadingXMLConfigFile(
            "Could not locate file: " + parXMLfile + "\n"
                + " Tried to read it as a file '"
                + new File(parXMLfile).getAbsolutePath()
                + "' and as a resource.\n", exx);
      }// Exception... -> File not found inside JAR
    }// Exception... -> File not found

    if (ct_insInput == null) {
      throw new WDBMgr_ErrorReadingXMLConfigFile(
          "Error reading XML config file: '" + parXMLfile + "'");
    } else {
      return myWdbDef;
    }
  } // readXML4WDB

  /**
   * Read the config file that is stored inside the same package/dir as the
   * class, that is provided<br/>
   * 
   * @param parClass
   * @param parXMLfile
   * @return instance of the (Castor) class to access the SCP patch settings
   * @throws STVI_ErrorReadingConfigFile
   */
  public static PatchSCP readXML4SCP(Class parClass, String parXMLfile)
      throws WDBMgr_ErrorReadingXMLConfigFile {
    PatchSCP myPatchSCP = null;
    InputStream ct_insInput = null;
    try {
      String pkg = parClass.getPackage().getName();
      pkg = pkg.replace('.', File.separatorChar);
      File myFile = new File(pkg, parXMLfile);
      ct_insInput = new FileInputStream(myFile);
      myPatchSCP = (PatchSCP) PatchSCP.unmarshal(new BufferedReader(
          new InputStreamReader(ct_insInput)));
      ct_insInput.close();
    } catch (Exception ex) {
      // does not work as a filename, maybe inside a JAR -> try it as a resource
      try {
        ct_insInput = parClass.getResourceAsStream(parXMLfile);
        myPatchSCP = (PatchSCP) PatchSCP.unmarshal(new BufferedReader(
            new InputStreamReader(ct_insInput)));
        ct_insInput.close();
      } catch (NullPointerException exx) {
        // exx.printStackTrace();
        throw new WDBMgr_ErrorReadingXMLConfigFile(parXMLfile
            + ": file not found." + exx.getLocalizedMessage(), exx);
      } catch (Exception exx) {
        // exx.printStackTrace();
        throw new WDBMgr_ErrorReadingXMLConfigFile(
            "Could not locate file: " + parXMLfile + "\n"
                + " Tried to read it as a file '"
                + new File(parXMLfile).getAbsolutePath()
                + "' and as a resource.\n", exx);
      }
    }// could not read as file
    if (ct_insInput == null) {
      throw new WDBMgr_ErrorReadingXMLConfigFile(
          "Error reading XML config file: '" + parXMLfile + "'");
    } else {
      return myPatchSCP;
    }
  }// readXML4SCP -> relative to class

  /**
   * Read the config file using Castor generated class <br/>
   * 
   * @return instance of PatchSCP
   * 
   * @throws STVI_ErrorReadingConfigFile
   *           if the system could not locate/read the XML config file
   */
  public static PatchSCP readXML4SCP(String parXMLfile)
      throws WDBMgr_ErrorReadingXMLConfigFile {
    PatchSCP myPatchSCP = null;
    // XML config file for STV import
    InputStream ct_insInput = null;
    try {
      ct_insInput = new FileInputStream(new File(parXMLfile));
      myPatchSCP = (PatchSCP) PatchSCP.unmarshal(new BufferedReader(
          new InputStreamReader(ct_insInput)));
      ct_insInput.close();
    }catch( org.exolab.castor.xml.MarshalException ex2 ) {
      throw new WDBMgr_ErrorReadingXMLConfigFile(
          "Error parsing: " + parXMLfile + "\n", ex2);
      
    } catch (Exception ex) {
      // does not work as a filename, maybe inside a JAR -> try it as a resource
      try {
        ct_insInput = WDB_Helper.class.getResourceAsStream("/" + parXMLfile);
        myPatchSCP = (PatchSCP) PatchSCP.unmarshal(new BufferedReader(
            new InputStreamReader(ct_insInput)));
        ct_insInput.close();
      } catch (Exception exx) {
        throw new WDBMgr_ErrorReadingXMLConfigFile(
            "Could not locate file: " + parXMLfile + "\n"
                + " Tried to read it as a file '"
                + new File(parXMLfile).getAbsolutePath()
                + "' and as a resource.\n", exx);
      }// Exception... -> File not found inside JAR
    }// Exception... -> File not found

    if (ct_insInput == null) {
      throw new WDBMgr_ErrorReadingXMLConfigFile(
          "Error reading XML config file: '" + parXMLfile + "'");
    } else {
      return myPatchSCP;
    }
  } // readXML4WDB
  
  /**
   * Return the version of the WDB file (hex encoded number)<br/>
   * 
   * @param parFilename
   * 
   * @return hex encoded number of the WDB file
   *  
   * @throws WDBMgr_Exception
   * @throws WDBMgr_FileNotFound
   */
  public static WDBInfo getWDBInfo(String parFilename) throws WDBMgr_FileNotFound,
      WDBMgr_Exception {
    WDBInfo retValue = new WDBInfo();

    File wdbFile = new File(parFilename);
    if (wdbFile.exists() == false) {
      throw new WDBMgr_FileNotFound();
    }

    BufferedInputStream bisInput = null;
    try {
      InputStream ct_insInput = new FileInputStream(new File(parFilename));
      bisInput = new BufferedInputStream(ct_insInput);
    } catch (FileNotFoundException ex) {
      throw new WDBMgr_FileNotFound(ex);
    }

    try {
      // ID
      byte byteBuffer[] = new byte[4];
      bisInput.read(byteBuffer);
      String wdbId = new String(byteBuffer);
      retValue.setWDBId(wdbId);
      // Version number
      bisInput.read(byteBuffer);
      //String wdbVersion = new String(byteVersion);
      int iVal = (byteBuffer[0] & 0xff) + 256 * (byteBuffer[1] & 0xff) + 256
          * 256 * (byteBuffer[2] & 0xff) + 256 * 256 * 256
          * (byteBuffer[3] & 0xff);

      //.out.println(iVal);
      //.out.println(byteVersion[0]);
      //.out.println(byteVersion[1]);

      byte buffByte = byteBuffer[0];
      byteBuffer[0] = byteBuffer[1];
      byteBuffer[1] = buffByte;
      retValue.setWdbBuildVersionHex(Integer.toHexString(iVal));
      retValue.setWdbBuildVersionDec(iVal);

      // next 4 bytes -> client locale
      bisInput.read(byteBuffer);
      buffByte = byteBuffer[0];
      byteBuffer[0] = byteBuffer[3];
      byteBuffer[3] = buffByte;
      buffByte = byteBuffer[1];
      byteBuffer[1] = byteBuffer[2];
      byteBuffer[2] = buffByte;

      // revert chars (eg.: SUne -> enUS)
      String clientLocale = new String(byteBuffer, 0, 4, "UTF-8");
      retValue.setClientLocale(clientLocale);

      bisInput.close();
    } catch (IOException ex) {
      throw new WDBMgr_Exception(ex);
    }
    return retValue;
  }// getWDBInfo


}// WDB_Helper
