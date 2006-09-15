/*
 * CreateDateTime: 18.03.2005, 20:10:32
 * 
 * This class represents the full API of the WDBearManager<br/>
 * <br/>
 * The whole functionality of the WDBearManager is encapsulated in this
 * APU class. The GUI and the console application just use this
 * API to provide functionality.<br/>
 * <br/>
 * 
 * An instance of this class is passed to the Jython scripts<br/>
 * named "wdbmgrapi" and is an instance of the API implementation<br/>
 *<br/>
 * 
 * It offers a complete API to allow the useage of the business logic
 * of the WDBearManager.<br/>
 * 
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: WDBearManager_API.java 209 2006-09-04 14:34:24Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-04 16:34:24 +0200 (Mo, 04 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 209 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/api/WDBearManager_API.java $
 * $Rev: 209 $
 */
package com.gele.tools.wow.wdbearmanager.api;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCP;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_CouldNotCreateSchemaException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_DatabaseException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorInsideXMLConfigFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorReadingWDBFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorReadingXMLConfigFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorWritingCSVFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorWritingTXTFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_FileNotFound;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoDataAvailableException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoXMLConfigFileForThisWDBException;
import com.gele.tools.wow.wdbearmanager.helper.TranslatorHelper;
import com.gele.tools.wow.wdbearmanager.helper.WDBInfo;
import com.gele.tools.wow.wdbearmanager.helper.WDB_Helper;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;
import com.gele.tools.wow.wdbearmanager.inout.SCPWritten;
import com.gele.tools.wow.wdbearmanager.inout.csv.WriteCSV;
import com.gele.tools.wow.wdbearmanager.inout.scp.GenericPatchSCP;
import com.gele.tools.wow.wdbearmanager.inout.sql.SQLManager;
import com.gele.tools.wow.wdbearmanager.inout.sql.SQLManager_I;
import com.gele.tools.wow.wdbearmanager.inout.txt.WriteTXT;
import com.gele.tools.wow.wdbearmanager.inout.wdb.ReadWDB;

/**
 * Implementation of the API for the WDBearManager<br/>
 * 
 * If you don't like this implementation, feel free to replace it<br/>
 * 
 * @author $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz
 *         Urazgubi.</a>
 * @version $Rev: 209 $
 * 
 * 
 */
public class WDBearManager_API implements WDBearManager_I {

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(WDBearManager_API.class);

  SQLManager_I mySQLManager = null;

  // Name of the DB config file
  private String dbConfigFile = WDBManagerConstants.WDB_DB_CONFIG_FILE;

  /**
   * Instantiate the API
   * 
   * Connects to the database, throws Exception, if errors occurred<br/>
   * 
   */
  public WDBearManager_API() {
  }

  /**
   * Create API using database config script<br/>
   * 
   * @param parDBconfigScript
   */
  public WDBearManager_API(String parDBconfigScript) {
    this.dbConfigFile = parDBconfigScript;
  }

  /**
   * Create API using instance of a SQL manager<br/>
   * 
   * @param parSQLManager_I
   */
  public WDBearManager_API(SQLManager_I parSQLManager_I) {
    this.mySQLManager = parSQLManager_I;
  }

  /**
   * Read the contents of a WDB file.<br/> This method returns instances of
   * DTO_Interface. The DTO is constructed out of the XML description.<br/>
   * 
   * To access the values inside a DTO:
   * 
   * (We use "questcache" as example)
   * 
   * Integer myID = (Integer)myDTO.getColumnValue("wdb_QuestId");
   * 
   * @param parFilename
   *          name of WDB file
   * @return Collection of DTOs
   * @throws WDBMgr_NoXMLConfigFileForThisWDBException
   *           no config file for this WDB file found
   * @throws WDBMgr_ErrorReadingWDBFile
   *           error while reading the WDB file
   * @throws WDBMgr_ErrorReadingXMLConfigFile
   *           the config file for the WDB could not be read
   * @throws WDBMgr_ErrorInsideXMLConfigFile
   *           the XML config file contained wrong types
   */
  public Collection readWDBfile(String parFilename)
      throws WDBMgr_ErrorInsideXMLConfigFile,
      WDBMgr_NoXMLConfigFileForThisWDBException,
      WDBMgr_ErrorReadingXMLConfigFile, WDBMgr_ErrorReadingWDBFile {

    //.out.println("readWDBfile: "+ parFilename);

    Collection retValue = null;
    // Open WDB
    String xmlConfig = "";
    //File wdbFile = new File(parFilename);
    try {
      xmlConfig = WDB_Helper.determineXMLconfigForWDB(this, parFilename);
    } catch (WDBMgr_NoXMLConfigFileForThisWDBException ex) {
      //ex.printStackTrace();
      throw new WDBMgr_NoXMLConfigFileForThisWDBException(
          "No XML format definition for the WDB\n"
              + "The format of the WDB file is unknown; '" + parFilename + "'");
    }
    ReadWDB myReader = new ReadWDB();
    //.out.println("readWDBfile: "+ xmlConfig);
    retValue = myReader.readWDBFile(xmlConfig, parFilename);
    // Warning message
    this.setWarningMessage(myReader.getWarningMessage());
    return retValue;
  }// readWDBfile

  /**
   * Connect to a database, with user settings.<br/> <br/> One can use all API
   * methods with any database, just connect to it using this method.
   * 
   * @param parJDBCdriver
   *          Driver
   * @param parJDBCurl
   *          URL of the database
   * @param parJDBCuser
   *          username
   * @param parJDBCpassword
   *          password
   * @param parDBprefix
   *          prefix, optional, can be "" (empty)
   * 
   * @return New API module especially for this database connection
   * 
   * @throws WDBMgr_Exception
   *           Connection could not be established
   */
  public WDBearManager_I connectToDatabase(String parJDBCdriver,
      String parJDBCurl, String parJDBCuser, String parJDBCpassword,
      String parDBprefix) throws WDBMgr_Exception {
    return new WDBearManager_API(new SQLManager(parJDBCdriver, parJDBCurl,
        parJDBCuser, parJDBCpassword, parDBprefix));
  }

  /**
   * Insert or Update objects inside the database<br/> <br/> This method gets
   * the DTO values that were generated from "readWDBFile" and stores them
   * inside the database<br/>
   * 
   * @param parItems
   *          Collection containing DTOs with the WDB information
   * @param parUpdate
   *          true - also do update
   * @return infos about INSERT, UPDATE
   * 
   * @throws WDBMgr_CouldNotCreateSchemaException
   *           This method will set up the database schema, if it does not
   *           exist. If this does not work -> this exception is thrown.
   * @throws WDBMgr_Exception
   */
  public ObjectsWritten insertOrUpdateToSQLDB(Collection parItems,
      boolean parUpdate) throws WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.insertOrUpdateToSQLDB(parItems, parUpdate);
  }

  /**
   * Insert or Update objects inside the database<br/> <br/> This method gets
   * the DTO values that were generated from "readWDBFile" and stores them
   * inside the database<br/>
   * 
   * @param parItem
   *          Store this object inside the database
   * @param parUpdate
   *          true - also do update
   * @return infos about INSERT, UPDATE
   * 
   * @throws WDBMgr_CouldNotCreateSchemaException
   *           This method will set up the database schema, if it does not
   *           exist. If this does not work -> this exception is thrown.
   * @throws WDBMgr_Exception
   */
  public ObjectsWritten insertOrUpdateToSQLDB(DTO_Interface parItem,
      boolean parUpdate) throws WDBMgr_CouldNotCreateSchemaException,
      WDBMgr_Exception {
    return this.mySQLManager.insertOrUpdateToSQLDB(parItem, parUpdate);

  }

  /**
   * Return Meta Infos about the database, may be usefull to adjust the program
   * <br/>
   * 
   * See java.sql.DatabaseMetaData for more details<br/>
   * 
   * @return meta data
   * 
   * @throws WDBMgr_DatabaseException
   *           metadata could not be read
   * @throws WDBMgr_Exception
   */
  public DatabaseMetaData getDatabaseMetaData() throws WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getDatabaseMetaData();
  }

  /**
   * Return all objects that are found inside the database for this given
   * example object<br/> <br/>
   * 
   * @param parDTO
   *          use this object to identify the table inside the database.
   * 
   * @return Collection of DTO instances with values from the database
   * 
   * @throws WDBMgr_NoDataAvailableException
   *           no data inside database
   * @throws WDBMgr_Exception
   *           If database access went wrong
   */
  public Collection getAllObjects(DTO_Interface parDTO)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getAllObjects(parDTO);
  }

  /**
   * Send a group by to the database and receive results.<br/> This method also
   * supports aggregates like SUM, MAX, MIN, etc. <br/>
   * 
   * Example:<br/> <br/> <code>
   *     try { <br/>
   *      myGeneric_DAO.getGroupBy(<br/>
   *        new Kosten_export(),  // Source table<br/>
   *        new String[]{ "ARTIKEL", "WE_NUMMER", "ProbenID_Extern",<br/>
   *                      "FREIGABEDATUM", "Kostenstelle", "AUFTRAG" },<br/>
   *        new String[]{ "SUM(KOSTENPUNKTE)"},<br/>
   *        (DTO_Interface)null<br/>
   *      );<br/>
   *    }catch( Exception ex ) {<br/>
   *      ex.printStackTrace();<br/>
   *      System.exit(1);<br/>
   *    }<br/>
   * </code>
   * <br/> Dies liefert folgende SQL Anweisung:<br/> <br/> SELECT
   * SUM(KOSTENPUNKTE), ARTIKEL, WE_NUMMER, <br/> ProbenID_Extern,
   * FREIGABEDATUM, Kostenstelle, AUFTRAG <br/> FROM KOSTEN_EXPORT <br/> group
   * by ARTIKEL, WE_NUMMER, ProbenID_Extern,<br/> FREIGABEDATUM, Kostenstelle,
   * AUFTRAG<br/>
   * 
   * @param parTable
   *          create query for this table
   * 
   * @param parStrArrGroupBy
   *          Array containing all columns for the group by statement
   * 
   * @param parStrArrAggrs
   *          Array containing the fully fledged aggregate statements
   * 
   * @param parQBEobj
   *          query by example, optional (can be null)
   * 
   * @return Collection containing the results. The Collection contains
   *         HashMaps, one for each row of the resultset. Entries inside the
   *         HashMap can be identified by the names provided with the String
   *         arrays<br/>
   * 
   * @throws WDBMgr_Exception
   */

  public Collection getGroupBy(DTO_Interface parTable,
      String[] parStrArrGroupBy, String[] parStrArrAggrs,
      DTO_Interface parQBEobj) throws WDBMgr_Exception {

    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getGroupBy(parTable, parStrArrGroupBy,
        parStrArrAggrs, parQBEobj);
  }

  /**
   * Return all objects that are found inside the database for this given
   * example object<br/> <br/>
   * 
   * @param parDTO
   *          use this object to identify the table inside the database.
   * @param orderDTO
   *          set an attribute to value "ASC" or "DESC" and the results will be
   *          sorted desc/asc for this column.
   * 
   * eg: DTO_Interface myDTO = ... myDTO.setColumnValue( "wdb_name", "*Gerry*" );
   * ... DTO_Interface orderDTO = myDTO.createObject(); orderDTO.setColumnValue(
   * "wdb_Id", "ASC" ); ..
   * 
   * Read all entries, where "wdb_name" contains "Gerry" and sort the results by
   * "wdb_Id" ascending.
   * 
   * 
   * @return Collection of DTO instances with values from the database
   * 
   * @throws WDBMgr_NoDataAvailableException
   *           no data inside database
   * @throws WDBMgr_Exception
   *           If database access went wrong
   */
  public Collection getAllObjects(DTO_Interface parDTO, DTO_Interface orderDTO)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getAllObjects(parDTO, orderDTO);
  }// getAllObjects

  /**
   * Read all contents from the database<br/> <br/> This method receives the
   * name of the table from which it reads all entries.<br/> Possible values
   * are:
   * <ul>
   * <li> creaturecache </li>
   * <li> gameobjectcache </li>
   * <li> itemcache </li>
   * <li> itemnamecache </li>
   * <li> itemtextcaxhe </li>
   * <li> npccache </li>
   * <li> pagetextcache </li>
   * <li> questcache</li>
   * </ul>
   * Make sure you only use lower case letters for the table name!<br/> <br/>
   * The name of the tables are stored inside the XML config files for the WDB
   * files. Attribute "wdbId"<br/>
   * 
   * @param parTablename
   *          Read data from this table
   * @return Collection containing DTO_Interface instances
   * @throws WDBMgr_NoDataAvailableException
   *           no data inside database
   * @throws WDBMgr_Exception
   *           If database access went wrong
   */
  public Collection getAllObjectsTable(String parTablename)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getAllObjects(parTablename);
  }

  /**
   * Return number of entries inside the specific database table<br/>
   * 
   * @param parTable
   *          name of the database table
   * 
   * @return integer number of objects stored inside the database
   * 
   * @throws WDBMgr_Exception
   *           error while reading the objects
   */
  public int getCountOfTable(String parTable) throws WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getCountOfTable(parTable);
  }

  /**
   * Delete an entry inside the database<br/>
   * 
   * The provided object is used as a Query By Example object.
   * Make sure you set the attributes correctly.
   * 
   * @param parObject
   *          Delete all objects of this kind
   * 
   * @return integer number of objects stored inside the database
   * 
   * @throws WDBMgr_Exception
   *           error while reading the objects
   */
  public int deleteObject(DTO_Interface parObj) throws WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.deleteObject(parObj);
  }

  /**
   * Return number of entries inside the specific database table<br/>
   * 
   * Set columns to specific values to restrict the search.
   * 
   * Eg:
   * 
   * myDTO.setColumnValue( "wdb_desc", "Something" ); .. int num =
   * mySQLMgr.getCountOf( myDTO ); ..
   *  -> Will return the number of objects, with "wdb_desc == Something"
   * 
   * @param parDTO
   *          DTO with specific query attributes set
   * 
   * @return integer
   */
  public int getCountOf(DTO_Interface parDTO) throws WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getCountOf(parDTO);
  }

  /**
   * Create a DTO for a specific table<br/>
   * 
   * This method reads the XML description and creates a DTO object. The DTO can
   * be used to make more specific questions to the database.<br/>
   * 
   * A DTO is a 1:1 representation of a column inside a database. You can
   * manipulate all columns using:<br/> <br/> setColumnValue( "COL_NAME",
   * valueObj );<br/> <br/> Retrieve a value:<br/> <br/> Object myObj =
   * getColumnValue( "COL_NAME" );<br/> <br/> The return value (object type)
   * depends on the type of the column:<br/> (see XML config files for each WDB
   * type):
   * 
   * <ul>
   * <li> bigInt -> Integer </li>
   * <li> single -> String ( eg. "1.340000") </li>
   * <li> varChar -> String </li>
   * <li> bigHex -> String </li>
   * </ul>
   * Why is single (float with single prec) mapped to String?<br/>
   * <ul>
   * <li> WAD emu has 6 digits of precision/fraction<br/> Can you guarantee
   * that your DB Float/Double value does no type conversion?<br/> Meaning I
   * put in the correct value and I retrieve it 100% identical?<br/> No way, so
   * String is used. </li>
   * <li> "." is used, not "," like in some countries to seperate the number<br/>
   * Using String in database makes this *very* clear</li>
   * </ul>
   * 
   * @param parTablename
   *          name of the table, beware, only lowercase letters allowed
   * 
   * @return A DTO that contains all attributes that were specified inside the
   *         XML config file for the table
   * 
   * @throws WDBMgr_Exception
   *           object could not be created
   */
  public DTO_Interface createDTOfromTable(String parTablename)
      throws WDBMgr_Exception {
    return WDB_Helper.createDTOfromXML(null, parTablename + ".xml");
  }

  /**
   * Patch an SCP file using the entries found inside the database<br/> <br/>
   * Creates
   * <ul>
   * <li> <oldfile.scp>_patch -> File containg the patched version </li>
   * <li> <oldfile.scp>_patch_info -> infos about the patch itself </li>
   * </ul>
   * 
   * 
   * @param parFilename
   *          name of scp file
   * @param parSCPid
   *          see patchSCP.xml, configName
   * @param parUseUTF8
   *          write UTF8
   *          
   * @param parLocale use this locale information (enUS, enGB, deDE, etc), if set to "ALL"
   * locale is ignored
   * 
   * @return statistic infos
   * 
   * @throws WDBMgr_Exception
   *           patch could not be applied
   */
  public SCPWritten patchSCP(String parFilename, String parSCPid,
      boolean parUseUTF8, String parLocale) throws WDBMgr_Exception {
    GenericPatchSCP myGenericPatchSCP = new GenericPatchSCP();
    HashMap scp2Patch = new HashMap();

    PatchSCP myPSCP = null;
    try {
      myPSCP = WDB_Helper.readXML4SCP(WDB_Helper.PATCH_XML);
      // System .out.println("Anzahl PatchSCP: "+ myArr.length);
    } catch (Exception ex) {
      this.myLogger.error("Fatal error: Could not open: '"
          + WDB_Helper.PATCH_XML + "'");
      this.myLogger.error(ex.getMessage());
      System.exit(0);
    }

    PatchSCPItem[] myArr = myPSCP.getPatchSCPItem();
    for (int i = 0; i < myArr.length; i++) {
      scp2Patch.put(myArr[i].getPatchConfig().getName(), myArr[i]
          .getPatchConfig());
    }

    PatchConfig myPCfg = (PatchConfig) scp2Patch.get(parSCPid);
    if (myPCfg == null) {
      this.myLogger.error("Aborting patch: '" + parFilename + "'");
      this.myLogger.error("The value '" + parSCPid + "' you specified for '"
          + WDBearManager.PATCH_SCP
          + "' is not a valid patchConfig entry inside 'patchSCP.xml'");
      this.myLogger.error("\nEXIT - ERROR");
      System.exit(0);
    }

    SCPWritten mySCPW = myGenericPatchSCP.merge(this, parFilename, myPCfg,
        parUseUTF8, false, parLocale);

    return mySCPW;
  }// patchSCP

  /**
   * Create URL params for pifnet-pifs Translator<br/> <br/> This URL can be
   * used to insert/update a quest in the Translator database<br/> <br/>
   * 
   * eg: String url4Trans = createTranslatorURLfromDTO(...); String myURL =
   * "http://www.XYZ.org/import_original_existing.php?"+url4Trans;
   * 
   * 
   * 
   * @param parOverwrite
   *          true - update, false - insert only
   * @param parDTO
   *          create URL text for this Quest DTO
   * @param parTranslatorID
   *          user id for the Translator
   * @param parTranslatorPass
   *          password for the Translator
   * @param parTranlateVersion
   *          translate for this version (eg: de21)
   * 
   * @return Text for the Translator
   */
  public String createTranslatorURLfromDTO(boolean parOverwrite,
      DTO_Interface parDTO, String parTranslatorID, String parTranslatorPass,
      String parTranlateVersion) {
    return TranslatorHelper.createTranslatorURLfromDTO(parOverwrite, parDTO,
        parTranslatorID, parTranslatorPass, parTranlateVersion);
  }

  /**
   * Write the Collection of DTOs to a simple TXT file.<br/> <br/>
   * 
   * @param parOutputDir
   *          Filename + Dir where to write the txt
   * @param parItems
   *          Collection with DTO values
   * 
   * @return Message, only interesting for user feedback
   * 
   * @throws WDBMgr_ErrorWritingTXTFile
   *           text file could not be written
   */
  public String writeTXT(File parOutputDir, Collection parItems)
      throws WDBMgr_ErrorWritingTXTFile {
    return WriteTXT.writeTXT(parOutputDir, parItems);
  }

  /**
   * Use the info in the DTOs and write a CSV file<br/> <br>/ All existing
   * files are overwritten without warning!
   * 
   * @param parOutputDir
   *          write here, name is taken from the WDB with ".csv"
   * @param parItems
   *          Collection containing the DTO instances
   * @return message for the user, can be ignored
   */
  public String writeCSV(File parOutputDir, Collection parItems)
      throws WDBMgr_ErrorWritingCSVFile {
    return WriteCSV.writeCSV(parOutputDir, parItems);
  }

  /**
   * Return the JDBC connection<br/>
   * 
   * May be needed to overcome the API<br/> <br/>
   * 
   * 
   * @return JDBC connection
   * @throws WDBMgr_Exception
   *           Error while accessing database
   */
  public Connection getConnection() throws WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getConnection();
  }

  /**
   * The prefix the user set in the config file<br/>
   * 
   * @return name of the prefix (pre-prended before the name)
   * @throws WDBMgr_Exception
   *           Error while accessing database
   */
  public String getDBPrefix() throws WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    return this.mySQLManager.getDBPrefix();
  }

  /**
   * Alter a table, add a new column<br/>
   * <br/>
   * Copy data from SRC table to DEST table<br/>
   * <br/>
   * Sometimes it is necessary to update an existing table, if the
   * WDB specs were changed<br/>.
   * Eg, a new column, etc<br/>
   * <br/>
   * This method will do the following:<br/>
   * <ul>
   * <li> Create a TMP table with the new specification </li>
   * <li> Copy all data from SRC table to TMP </li>
   * <li> Drop table SRC </li>
   * <li> Create the table SRC_NEW with the new specification </li>
   * <li> Copy all data from TMP table to the SRC_NEW table </li>
   * <li> Drop table TMP </li>
   * </ul>
   * 
   * @param parTablename Re-create this table and keep all data
   * 
   * @throws WDBMgr_Exception problems creating the TMP table and restoring old data
   * @param parPrevWDBVersion This was the previous WDB version from that we update
   */
  public void alterTableAddCol(String parPrevWDBVersion, String parTableName)
      throws WDBMgr_Exception {
    // Create a TMP table with the new specification 
    // Copy all data from SRC table to TMP
    // Drop table SRC
    // Create the table SRC_NEW with the new specification
    // Copy all data from TMP table to the SRC_NEW table
    // Drop table TMP

    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    this.mySQLManager.alterTableAddCol(parPrevWDBVersion, parTableName);
  }

  public void alterTableStepRename(String parPrevWDBVersion, String parTableName)
      throws WDBMgr_Exception {
    // Create a TMP table with the new specification 
    // Copy all data from SRC table to TMP
    // Drop table SRC
    // Create the table SRC_NEW with the new specification
    // Copy all data from TMP table to the SRC_NEW table
    // Drop table TMP

    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    this.mySQLManager.alterTableStepRename(parPrevWDBVersion, parTableName);
  }

  /**
   * A table is changed - one or more columns were renamed
   * (types stayed the same).<br/>
   * 
   * This method will do the following:<br/>
   * <ul>
   * <li> Create a TMP table with the new specification </li>
   * <li> Copy all data from SRC table to TMP </li>
   * <li> Drop table SRC </li>
   * <li> Create the table SRC_NEW with the new specification </li>
   * <li> Copy all data from TMP table to the SRC_NEW table </li>
   * <li> Drop table TMP </li>
   * </ul>
   * 
   * @param parTable Re-create this table and keep all data
   * @param parPrevWDBVersion This was the previous WDB version from that we update
   * 
   */
  public void alterTableRenameCol(String parPrevWDBVersion, String parTable)
      throws WDBMgr_Exception {
    // Create a TMP table with the new specification 
    // Copy all data from SRC table to TMP
    // Drop table SRC
    // Create the table SRC_NEW with the new specification
    // Copy all data from TMP table to the SRC_NEW table
    // Drop table TMP

    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    this.mySQLManager.alterTableRenameCol(parPrevWDBVersion, parTable);
  }

  /**
   * Create database table<br/>
   * 
   * Make sure, the corresponding XML file exists<br/>
   * 
   * @throws WDBMgr_Exception
   *           Error while accessing database
   */
  public void createTable(String parTable) throws WDBMgr_Exception {
    if (this.mySQLManager == null) {
      this.mySQLManager = new SQLManager(this.dbConfigFile);
    }
    this.mySQLManager.createTable(parTable);
  }

  /**
   * Return the name of the config file used to configure db access<br/>
   * 
   * @return name of the config file
   */
  public String getDBconfigFile() {
    return this.dbConfigFile;
  }

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
  public WDBInfo getWDBInfo(String parFilename) throws WDBMgr_FileNotFound,
      WDBMgr_Exception {
    return WDB_Helper.getWDBInfo(parFilename);
  }// getWDBVersion

  private String warningMessage = "";

  public void setWarningMessage(String parMsg) {
    this.warningMessage = parMsg;
  }

  public String getWarningMessage() {
    return this.warningMessage;
  }

  /**
   * Return the internal version of the WDBearManager<br/>
   * 
   * @return String representing the version 
   */
  public String getVersion() {
    return WDBearManager.VERSION_INFO;
  }

}// WDBearManager_API
