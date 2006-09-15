/*
 * CreateDateTime: 18.03.2005, 20:10:32
 * 
 * API of the WoWWDBManager -> Allow Jython programms to access the
 * BL<br/>
 * 
 * Console, GUI, etc -> They all use this API, nothing more, nothing less
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: WDBearManager_I.java 209 2006-09-04 14:34:24Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/api/WDBearManager_I.java $
 * $Rev: 209 $
 *
 */
package com.gele.tools.wow.wdbearmanager.api;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collection;

import com.gele.base.dbaccess.DTO_Interface;
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
import com.gele.tools.wow.wdbearmanager.helper.WDBInfo;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;
import com.gele.tools.wow.wdbearmanager.inout.SCPWritten;

/**
 * This interface represents the full API of the WoWWDBManager<br/>
 * <br/>
 * The whole functionality of the WoWWDBManager is encapsulated in this
 * API class. The GUI and the console application just use this
 * API to provide functionality.<br/>
 * <br/>
 * 
 * An instance of this class is passed to every Jython script.<br/>
 * The script receives a variable named "wdbmgrapi" and is an
 * instance of the API implementation<br/>
 *<br/>
 * 
 * It offers a complete API to allow the useage of the business logic
 * of the WoWWDBManager.<br/>
 * 
 * API for the WoWWDB-Manager<br/>
 * <br/>
 * Full access to the Business Logic and database of the WDBManager
 * application<br/>
 * <br/>
 * 
 * This API can be used for scripting with Jython (www.jython.org)
 * <br/>
 * Every Jython program receives a variable called "wdbmgrapi" that
 * contains an instance of this API.
 * 
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 209 $
 * 
 */
public interface WDBearManager_I {

  /**
   * Create URL params for pifnet-pifs Translator<br/>
   * <br/>
   * This URL can be used to insert/update a quest in the Translator
   * database<br/>
   * <br/>
   * 
   * eg:
   * String url4Trans = createTranslatorURLfromDTO(...);
   * String myURL = "http://www.XYZ.org/import_original_existing.php?"+url4Trans;
   * 
   * 
   * 
   * @param parOverwrite true - update, false - insert only
   * @param parDTO create URL text for this Quest DTO
   * @param parTranslatorID user id for the Translator 
   * @param parTranslatorPass password for the Translator
   * @param parTranlateVersion translate for this version (eg: de21)
   * 
   * @return Text for the Translator
   */
  public String createTranslatorURLfromDTO(boolean parOverwrite,
      DTO_Interface parDTO, String parTranslatorID, String parTranslatorPass,
      String parTranlateVersion);

  /**
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
   * @throws WDBMgr_Exception problems creating the TMP table and restoring old data
   * @param parPrevWDBVersion This was the previous WDB version from that we update
   */
  public void alterTableAddCol(String parPrevWDBVersion, String parTableName)
      throws WDBMgr_Exception;

  public void alterTableStepRename(String parPrevWDBVersion, String parTableName)
      throws WDBMgr_Exception;

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
      throws WDBMgr_Exception;

  /**
   * Read the contents of a WDB file.<br/>
   * This method returns instances of DTO_Interface. The DTO is constructed
   * out of the XML description.<br/>
   * 
   * To access the values inside a DTO: (see "questcache.xml" for all columns)<br/>
   *<br/>
   *<code>
   * Integer myID = (Integer)myDTO.getColumnValue("wdb_QuestId");<br/>
   * String myName = (String)myDTO.getColumnValue("wdb_name");<br/>
   * Integer myPos0 = (Integer)myDTO.getColumnValue("wdb_position0");<br/>
   * // single (float with single prec) values are stored as String<br/>
   * // inside the database to keep the EXCACT precision <br/>
   * String myPos1  = (String)myDTO.getColumnValue("wdb_position1");<br/>
   * String myPos2  = (String)myDTO.getColumnValue("wdb_position2");<br/>
   * Integer myPos3 = (Integer)myDTO.getColumnValue("wdb_position3");<br/>
   * </code>
   * 
   * If the method could read the WDB, but something weird occurred
   * this method must use "setWarningMessage". So the user can be informed
   * about a "premature end of file", etc.
   * 
   * @param parFilename name of WDB file
   * 
   * @return Collection of DTOs
   * @throws WDBMgr_NoXMLConfigFileForThisWDBException no config file for this WDB file
   * found
   * @throws WDBMgr_ErrorReadingWDBFile error while reading the WDB file
   * @throws WDBMgr_ErrorReadingXMLConfigFile the config file for the WDB could not be read
   * @throws WDBMgr_ErrorInsideXMLConfigFile
   *           the XML config file contained wrong types
   */
  public Collection readWDBfile(String parFilename)
      throws WDBMgr_ErrorInsideXMLConfigFile,
      WDBMgr_NoXMLConfigFileForThisWDBException,
      WDBMgr_ErrorReadingXMLConfigFile, WDBMgr_ErrorReadingWDBFile;

  /**
   * Create a DTO for a specific table<br/>
   * 
   * This method reads the XML description and creates a DTO object.
   * The DTO can be used to make more specific questions to the database.<br/>
   * 
   * A DTO is a 1:1 representation of a column inside a database.
   * You can manipulate all columns using:<br/>
   * <br/>
   * setColumnValue( "COL_NAME", valueObj );<br/>
   * <br/>
   * Retrieve a value:<br/>
   * <br/>
   * Object myObj = getColumnValue( "COL_NAME" );<br/>
   * <br/>
   * The return value (object type) depends on the type of the column:<br/>
   *(see XML config files for each WDB type):
   *
   * <ul>
   *  <li> bigInt  -> Integer </li>
   *  <li> single  -> String ( eg. "1.340000") </li>
   *  <li> varChar -> String </li>
   *  <li> bigHex  -> String </li>
   * </ul>
   * Why is single (float with single prec) mapped to String?<br/>
   * <ul>
   * <li> WAD emu has 6 digits of precision/fraction<br/>
   *      Can you guarantee that your DB Float/Double value does no type conversion?<br/>
   *      Meaning I put in the correct value and I retrieve it 100% identical?<br/>
   *      No way, so String is used.
   * </li>
   * <li> "." is used, not "," like in some countries to seperate the number<br/>
   * Using String in database makes this *very* clear</li>
   * </ul>
   * 
   * @param parTablename name of the table, beware, only lowercase letters allowed
   * 
   * @return A DTO that contains all attributes that were specified
   * inside the XML config file for the table
   * 
   * @throws WDBMgr_Exception object could not be created
   */
  public DTO_Interface createDTOfromTable(String parTablename)
      throws WDBMgr_Exception;

  /**
   * Connect to a database, with user settings.<br/>
   * <br/>
   * One can use all API methods with any database, just connect to
   * it using this method.
   * 
   * @param parJDBCdriver Driver
   * @param parJDBCurl URL of the database
   * @param parJDBCuser username
   * @param parJDBCpassword password
   * @param parDBprefix prefix, optional, can be "" (empty)
   * 
   * @return New API module especially for this database connection
   *  
   * @throws WDBMgr_Exception Connection could not be established
   */
  public WDBearManager_I connectToDatabase(String parJDBCdriver,
      String parJDBCurl, String parJDBCuser, String parJDBCpassword,
      String parDBprefix) throws WDBMgr_Exception;

  /**
   * Patch an SCP file using the entries found inside the database<br/>
   * <br/>
   * Creates 
   * <ul>
   * <li> <oldfile.scp>_patch -> File containg the patched version </li>
   * <li> <oldfile.scp>_patch_info -> infos about the patch itself </li>
   * </ul>
   * 
   * 
   * @param parFilename name of scp file
   * @param parSCPid see patchSCP.xml, configName
   * @param parUseUTF8 write UTF8
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
      boolean parUseUTF8, String parLocale) throws WDBMgr_Exception;

  /**
   * Insert or Update objects inside the database<br/>
   * <br/>
   * This method gets the DTO values that were generated from "readWDBFile"
   * and stores them inside the database<br/> 
   * 
   * @param parItems Collection containing DTOs with the WDB information
   * @param parUpdate true - also do update
   * @return infos about INSERT, UPDATE
   * 
   * @throws WDBMgr_CouldNotCreateSchemaException This method will set up the database
   * schema, if it does not exist. If this does not work -> this exception is thrown. 
   * @throws WDBMgr_Exception
   */
  public ObjectsWritten insertOrUpdateToSQLDB(Collection parItems,
      boolean parUpdate) throws WDBMgr_CouldNotCreateSchemaException,
      WDBMgr_Exception;

  /**
   * Insert or Update objects inside the database<br/>
   * <br/>
   * This method gets the DTO values that were generated from "readWDBFile"
   * and stores them inside the database<br/> 
   * 
   * @param parItem Store this object inside the database
   * @param parUpdate true - also do update
   * @return infos about INSERT, UPDATE
   * 
   * @throws WDBMgr_CouldNotCreateSchemaException This method will set up the database
   * schema, if it does not exist. If this does not work -> this exception is thrown. 
   * @throws WDBMgr_Exception
   */
  public ObjectsWritten insertOrUpdateToSQLDB(DTO_Interface parItem,
      boolean parUpdate) throws WDBMgr_CouldNotCreateSchemaException,
      WDBMgr_Exception;

  /**
   * Return Meta Infos about the database, may be usefull to adjust the program
   * <br/>
   * 
   * See java.sql.DatabaseMetaData for more details<br/>
   * 
   * @return meta data
   * 
   * @throws WDBMgr_DatabaseException metadata could not be read
   * @throws WDBMgr_Exception
   */
  public abstract DatabaseMetaData getDatabaseMetaData()
      throws WDBMgr_DatabaseException, WDBMgr_Exception;

  /**
   * Return all objects that are found inside the database
   * for this given example object<br/>
   * <br/>
   * 
   * @param parDTO use this object to identify the table inside
   * the database.
   * 
   * @return Collection of DTO instances with values from the
   * database
   * 
   * @throws WDBMgr_NoDataAvailableException no data inside database
   * @throws WDBMgr_Exception If database access went wrong
   */
  public abstract Collection getAllObjects(DTO_Interface parDTO)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception;

  /**
   * Return all objects that are found inside the database
   * for this given example object<br/>
   * <br/>
   * 
   * @param parDTO use this object to identify the table inside
   * the database.
   * @param orderDTO set an attribute to value "ASC" or "DESC"
   * and the results will be sorted desc/asc for this column.
   * 
   * eg:
   * DTO_Interface myDTO = ...
   * myDTO.setColumnValue( "wdb_name", "*Gerry*" );
   * ...
   * DTO_Interface orderDTO = myDTO.createObject();
   * orderDTO.setColumnValue( "wdb_Id", "ASC" );
   * ..
   * 
   * Read all entries, where "wdb_name" contains "Gerry" and
   * sort the results by "wdb_Id" ascending.
   * 
   * 
   * @return Collection of DTO instances with values from the
   * database
   * 
   * @throws WDBMgr_NoDataAvailableException no data inside database
   * @throws WDBMgr_Exception If database access went wrong
   */
  public Collection getAllObjects(DTO_Interface parDTO, DTO_Interface orderDTO)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception;

  /**
   * Read all contents from the database<br/>
   * <br/>
   * This method receives the name of the table from which
   * it reads all entries.<br/>
   * Possible values are:
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
   * Make sure you only use lower case letters for the table name!<br/>
   * <br/>
   * The name of the tables are stored inside the XML config files for
   * the WDB files. Attribute "wdbId"<br/>
   * 
   * @param parTablename Read data from this table
   * @return Collection containing DTO_Interface instances
   * @throws WDBMgr_NoDataAvailableException no data inside database
   * @throws WDBMgr_Exception If database access went wrong
   */
  public abstract Collection getAllObjectsTable(String parTablename)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception;

  /**
   * Return number of entries inside the specific database table<br/>
   * 
   * @param parTable name of the database table
   * 
   * @return integer number of objects stored inside the database
   *  
   * @throws WDBMgr_Exception error while reading the objects
   */
  public abstract int getCountOfTable(String parTable) throws WDBMgr_Exception;

  /**
   * Return number of entries inside the specific database table<br/>
   * 
   * Set columns to specific values to restrict the search.
   * 
   * Eg:
   * 
   * myDTO.setColumnValue( "wdb_desc", "Something" );
   * ..
   * int num = mySQLMgr.getCountOf( myDTO );
   * ..
   * 
   * -> Will return the number of objects, with "wdb_desc == Something"
   * 
   * @param parDTO DTO with specific query attributes set
   * 
   * @exception WDBMgr_Exception an error occurred
   * 
   * @return integer
   */
  public int getCountOf(DTO_Interface parDTO) throws WDBMgr_Exception;

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
  public int deleteObject(DTO_Interface parObj) throws WDBMgr_Exception;

  /**
   * Write the Collection of DTOs to a simple TXT file.<br/>
   * <br/>
   * The name of the column is uses as KEY and the value
   * is used as (hmm) value<br/>
   * 
   * 
   * @param parOutputDir Filename + Dir where to write the txt
   * @param parItems Collection with DTO values
   * 
   * @return Message, only interesting for user feedback
   * 
   * @throws WDBMgr_ErrorWritingTXTFile text file could not be written
   */
  public String writeTXT(File parOutputDir, Collection parItems)
      throws WDBMgr_ErrorWritingTXTFile;

  /**
   * Use the info in the DTOs and write a CSV file<br/>
   * <br>/
   * All existing files are overwritten without warning!
   * 
   * @param parOutputDir write here, name is taken from the WDB with ".csv"
   * @param parItems Collection containing the DTO instances
   * @return message for the user, can be ignored
   */
  public String writeCSV(File parOutputDir, Collection parItems)
      throws WDBMgr_ErrorWritingCSVFile;

  /**
   * Return the JDBC connection<br/>
   * 
   * May be needed to overcome the API<br/>
   * <br/>
   * 
   * 
   * @return JDBC connection
   * @throws WDBMgr_Exception Error while accessing database
   */
  public Connection getConnection() throws WDBMgr_Exception;

  /**
   * The prefix the user set in the config file<br/>
   * 
   * @return name of the prefix (pre-prended before the name)
   * @throws WDBMgr_Exception Error while accessing database
   */
  public String getDBPrefix() throws WDBMgr_Exception;

  /**
   * Create database table<br/>
   *<br/>
   * This method takes the XML definition file (default)
   * and creates a database table.<br/>
   * <br/>
   *
   * Make sure, the corresponding XML file exists<br/>
   * 
   * @param parTable Name of the database table
   * 
   * @throws WDBMgr_Exception Error while accessing database
   */
  public void createTable(String parTable) throws WDBMgr_Exception;

  /**
   * Return the name of the config file used to configure
   * db access<br/>
   * 
   * @return name of the config file
   */
  public String getDBconfigFile();

  /**
   * Return a filled WDB Info class<br/>
   * 
   * @param parFilename
   * 
   * @return WDBInfo class
   *  
   * @throws WDBMgr_Exception
   * @throws WDBMgr_FileNotFound WDB file could not be found
   */
  public WDBInfo getWDBInfo(String parFilename) throws WDBMgr_FileNotFound,
      WDBMgr_Exception;

  /**
   * Maybe a warning occurred, not an error but a warning.<br/>
   * <br/>
   * Currently only supported by "readWDBfile" 
   * 
   * @param parMsg
   */
  public void setWarningMessage(String parMsg);

  /**
   * Return warning message, maybe empty String ("") if no
   * warning occurred<br/>
   * 
   * @return
   */
  public String getWarningMessage();

  public Collection getGroupBy(DTO_Interface parTable,
      String[] parStrArrGroupBy, String[] parStrArrAggrs,
      DTO_Interface parQBEobj) throws WDBMgr_Exception;

  /**
   * Return the internal version of the WDBearManager<br/>
   * 
   * @return String representing the version 
   */
  public String getVersion();

}//WDBearManager_I
