/*
 * CreateDateTime: 16.03.2005, 13:55:02
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: SQLManager.java 218 2006-09-14 23:45:59Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-15 01:45:59 +0200 (Fr, 15 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 218 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/sql/SQLManager.java $
 * $Rev: 218 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.inout.sql;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.DB_Generic_DAO;
import com.gele.base.dbaccess.DTO_Interface;
import com.gele.base.dbaccess.Generic_DTO;
import com.gele.base.dbaccess.exception.DatabaseTroubleException;
import com.gele.base.dbaccess.exception.ObjectNotFoundException;
import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.api.WDBManagerConstants;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_CouldNotCreateSchemaException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_DatabaseException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_MissingConfigParameterException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoDataAvailableException;
import com.gele.tools.wow.wdbearmanager.helper.WDB_Helper;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;
import com.gele.utils.ReadPropertiesFile;

public class SQLManager implements SQLManager_I {

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(SQLManager.class);

  // JDBC String to connect to the database
  private String jdbcurl = "";

  private String username = "";

  private String password = "";

  private String jdbcdriver = "";

  private String dbprefix = "";

  // We are dealing with this kind of DTO
  // Is filled inside constructor
  // DTO_Interface myPP = null;

  // Contains config settings for the database
  ConfigureDB myConfDB = null;

  // Data Access Object
  // (see constructor -> init)
  DB_Generic_DAO myGeneric_DAO = null;

  // Store all existing tables here -> saves time
  private Vector vecTabExists = new Vector();

  /**
   * Create an instance of the SQLManager for a user defined database connection<br/>
   * <b/>
   * 
   * @param parJDBCdriver
   *          classname of the JDBC driver
   * @param parJDBCurl
   *          JDBC url to access the database
   * @param parJDBCuser
   *          username
   * @param parJDBCpassword
   *          password
   * @param parDBprefix
   *          when tables are created, use this "prefix" for every table name.
   *          So one can install the schema multiple times by just specifying a
   *          different prefix.
   * 
   * @throws WDBMgr_MissingConfigParameterException
   * @throws WDBMgr_CouldNotCreateSchemaException
   */
  public SQLManager(String parJDBCdriver, String parJDBCurl,
      String parJDBCuser, String parJDBCpassword, String parDBprefix)
      throws WDBMgr_MissingConfigParameterException,
      WDBMgr_CouldNotCreateSchemaException {
    this.jdbcdriver = parJDBCdriver;
    this.jdbcurl = parJDBCurl;
    this.username = parJDBCuser;
    this.password = parJDBCpassword;

    this.establishConnection();
  }

  /**
   * Create database access module using the default user settings <br/> See
   * wdbmanager_sql.properties<br/> <br/>
   * 
   * @throws WDBMgr_MissingConfigParameterException
   * @throws WDBMgr_CouldNotCreateSchemaException
   */
  public SQLManager(String parDBconfigScript)
      throws WDBMgr_MissingConfigParameterException,
      WDBMgr_CouldNotCreateSchemaException {

    init(parDBconfigScript);
  }

  public SQLManager() throws WDBMgr_MissingConfigParameterException,
      WDBMgr_CouldNotCreateSchemaException {

    init(WDBManagerConstants.WDB_DB_CONFIG_FILE);
  }

  private void init(String parDBconfigScript)
      throws WDBMgr_MissingConfigParameterException,
      WDBMgr_CouldNotCreateSchemaException {
    Properties myProps = null;
    try {
      myProps = ReadPropertiesFile.readProperties(parDBconfigScript);
    } catch (Exception ex) {
      String msg = "Could not open properties file: '" + parDBconfigScript
          + "'";
      this.myLogger.error(msg);
    }
    // retrieve parameters from config file
    // and validate them
    this.jdbcurl = myProps.getProperty("jdbcurl");
    if (this.jdbcurl == null) {
      String msg = "Param 'jdbcurl' not present in config file: '"
          + parDBconfigScript + "'";
      this.myLogger.error(msg);
      throw new WDBMgr_MissingConfigParameterException(msg);
    }
    this.username = myProps.getProperty("username");
    if (this.username == null) {
      String msg = "Param 'username' not present in config file: '"
          + parDBconfigScript + "'";
      this.myLogger.error(msg);
      throw new WDBMgr_MissingConfigParameterException(msg);
    }
    this.password = myProps.getProperty("password");
    if (this.password == null) {
      String msg = "Param 'password' not present in config file: '"
          + parDBconfigScript + "'";
      this.myLogger.error(msg);
      throw new WDBMgr_MissingConfigParameterException(msg);
    }
    this.jdbcdriver = myProps.getProperty("jdbcdriver");
    if (this.jdbcdriver == null) {
      String msg = "Param 'jdbcdriver' not present in config file: '"
          + parDBconfigScript + "'";
      this.myLogger.error(msg);
      throw new WDBMgr_MissingConfigParameterException(msg);
    }
    // table prefix, optional
    this.dbprefix = myProps.getProperty("dbprefix");
    if (this.dbprefix == null) {
      this.dbprefix = "";
    }
    this.establishConnection();
  }// constructor

  private void establishConnection() {
    this.myLogger.debug("establishConnection");
    // so, we got all params, now lets establish a JDBC connection
    // to the database
    this.myConfDB = new ConfigureDB();
    this.myConfDB.setJdbcdriver(this.jdbcdriver);
    this.myConfDB.setJdbcurl(this.jdbcurl);
    this.myConfDB.setUsername(this.username);
    this.myConfDB.setPassword(this.password);

    // debug info
    this.myLogger.debug("JDBC-Driver: " + this.jdbcdriver);
    this.myLogger.debug("JDBC-URL: " + this.jdbcurl);

    // database configured -> lets go (hey ho)
    // See, if the db scheme is ok
    // if not -> create tables
    this.myGeneric_DAO = new DB_Generic_DAO();
    this.myGeneric_DAO.setConfiguration(this.myConfDB);
    // do not use a PrimaryKeyCreator -> the database has
    // a special column ID of type identity (sql 200n standard)
    this.myGeneric_DAO.setCreatePrimaryKey(false);
    this.myGeneric_DAO.setDBprefix(this.dbprefix);

    if (this.myConfDB.getConnection() == null) {
      this.myLogger
          .error("Error accessing database - Please check if the database is available");
      this.myLogger.error("Fatal Error...");
      this.myLogger.error(WDBearManager.VERSION_INFO);

      // System. err.println(msg);
      //System.exit(0);
    }
  }// establishConnection

  private void testIfTableExistsAndCreate(DTO_Interface parDTO)
      throws WDBMgr_CouldNotCreateSchemaException {

    // .out.println("TXT: parDTO " + parDTO);
    // .out.println("TXT: vecTabExists "+ vecTabExists);
    if (this.vecTabExists.contains(parDTO.getTableName())) {
      return;
    }

    // remember this one
    DTO_Interface myDTO = parDTO.createObject();
    try {
      this.myLogger.debug("Testing, if TABLE exists");

      int num = this.myGeneric_DAO.getCountObject(myDTO);
      this.myLogger.debug("Number of entries in table '" + myDTO.getTableName()
          + "': " + num);
      this.vecTabExists.addElement(parDTO.getTableName());
    } catch (ObjectNotFoundException ex) {
      this.vecTabExists.addElement(parDTO.getTableName());
      // ex.printStackTrace();
      // do nothing -> there r just no entries
      // Hmm, is the scheme up to date?
    } catch (DatabaseTroubleException ex) {
      // ex.printStackTrace();
      String msg = "Database scheme not found -> creating table: "
          + myDTO.getTableName();
      this.myLogger.info(msg);
      // tables do not exist, CREATE them
      this.createDatabaseScheme(myDTO);
      this.vecTabExists.addElement(parDTO.getTableName());
    } catch (Exception ex) {
      // ex.printStackTrace();
      // this should never happen -> system crash
    }
    // this.checkIfSchemeIsUpToDate(parDTO);
  }// testIfTableExistsAndCreate

  /**
   * The prefix the user set in the config file<br/>
   * 
   * @return name of the prefix (is prepended for all tables)
   */
  public String getDBPrefix() {
    return this.dbprefix;
  }

  /**
   * Return the JDBC connection<br/>
   * 
   * May be needed to overcome the API<br/> <br/>
   * 
   * 
   * @return JDBC connection
   */
  public Connection getConnection() {
    return this.myConfDB.getConnection();
  }

  /**
   * Create a database table that can store the contents of this DTO<br/> <br/>
   * 
   * @param parDTO
   *          create schema for this DTO
   * 
   * @throws WDBMgr_Exception
   *           table could not be created
   */
  private void createDatabaseScheme(DTO_Interface parDTO)
      throws WDBMgr_CouldNotCreateSchemaException {
    StringBuffer createTable = new StringBuffer();
    createTable.append("create table ");
    createTable.append(this.dbprefix);
    createTable.append(parDTO.getTableName());
    createTable.append(" (");
    Iterator itColumns = parDTO.getColumns();
    String strColName = "";
    int colType = -1;

    // MYSQL -> varchar has max 255 chars, use TEXT
    String varcharType = " varchar(2048) ";
    String dbName = "";
    try {
      dbName = this.getDatabaseMetaData().getDatabaseProductName();
      if (dbName.toLowerCase().startsWith("mysql")) {
        this.myLogger.info("MySQL detected");
        varcharType = " TEXT ";
      }
    } catch (Exception ex) {
      // ignore
    }

    while (itColumns.hasNext()) {
      strColName = (String) itColumns.next();
      colType = parDTO.getColumnType(strColName);

      createTable.append(strColName);

      switch (colType) {
      // TINYINT - java.sql.Types.TINYINT 1 byte signed int
      case java.sql.Types.TINYINT:
        createTable.append(" integer ");
        break;
      // smallint - java.sql.Types.SMALLINT 2 byte signed int
      case java.sql.Types.SMALLINT:
        createTable.append(" integer ");
        break;
      // integer - java.sql.Types.INTEGER 4 byte signed int
      case java.sql.Types.INTEGER:
        createTable.append(" integer ");
        break;
      // varChar - java.sql.Types.VARCHAR string, ends with NULL
      case java.sql.Types.VARCHAR:
      case java.sql.Types.CHAR:
        // createTable.append(" varchar(2048) ");
        createTable.append(varcharType);
        break;
      // bigIntHex - java.sql.Types.NUMERIC 4 byte int value, gets hex encoded
      // after read from WDB
      case java.sql.Types.NUMERIC:
        createTable.append(" varchar(64) ");
        break;
      // single - java.sql.Types.FLOAT 4 byte float, single precision
      case java.sql.Types.FLOAT:
        createTable.append(" varchar(64) ");
        break;
      }// switch
      if (itColumns.hasNext()) {
        createTable.append(", ");
      }
    }// loop... columns
    createTable.append(")");

    try {
      String msg = "CREATE TABLE " + parDTO.getTableName();
      this.myLogger.info(msg);
      if (dbName.toLowerCase().startsWith("mysql")) {
        createTable.append(" ");
        createTable.append("CHARACTER SET utf8;");
        varcharType = " TEXT ";
      }
      this.myLogger.debug(createTable.toString());
      this.myGeneric_DAO.sendSQLupdate(createTable.toString());
    } catch (Exception ex) {
      String msg = "Error creating table '" + parDTO.getTableName()
          + "' inside database";
      msg += "\n " + this.maxCharsPerLine(createTable.toString(), 80) + "\n"
          + this.maxCharsPerLine(ex.getMessage(), 80);
      this.myLogger.error(msg);
      throw new WDBMgr_CouldNotCreateSchemaException(msg, ex);
    }

  }// createDatabaseScheme

  /**
   * Create a TMP database table that can store the contents of this DTO<br/>
   * <br/>
   * 
   * @param parDTO
   *          create schema for this DTO
   * 
   * @throws WDBMgr_Exception
   *           table could not be created
   */
  private void createDatabaseSchemeTMP(DTO_Interface parDTO)
      throws WDBMgr_CouldNotCreateSchemaException {
    StringBuffer createTable = new StringBuffer();
    createTable.append("create table ");
    createTable.append(this.dbprefix);
    createTable.append(parDTO.getTableName());
    createTable.append("_TMP (");
    Iterator itColumns = parDTO.getColumns();
    String strColName = "";
    int colType = -1;

    // MYSQL -> varchar has max 255 chars, use TEXT
    String varcharType = " varchar(2048) ";
    String dbName = "";
    try {
      dbName = this.getDatabaseMetaData().getDatabaseProductName();
      if (dbName.toLowerCase().startsWith("mysql")) {
        this.myLogger.info("MySQL detected");
        varcharType = " TEXT ";
      }
    } catch (Exception ex) {
      // ignore
    }

    while (itColumns.hasNext()) {
      strColName = (String) itColumns.next();
      colType = parDTO.getColumnType(strColName);

      createTable.append(strColName);
      switch (colType) {
      // TINYINT - java.sql.Types.TINYINT 1 byte signed int
      case java.sql.Types.TINYINT:
        createTable.append(" integer ");
        break;
      // smallint - java.sql.Types.SMALLINT 2 byte signed int
      case java.sql.Types.SMALLINT:
        createTable.append(" integer ");
        break;
      // integer - java.sql.Types.INTEGER 4 byte signed int
      case java.sql.Types.INTEGER:
        createTable.append(" integer ");
        break;
      // varChar - java.sql.Types.VARCHAR string, ends with NULL
      case java.sql.Types.VARCHAR:
      case java.sql.Types.CHAR:
        // createTable.append(" varchar(2048) ");
        createTable.append(varcharType);
        break;
      // bigIntHex - java.sql.Types.NUMERIC 4 byte int value, gets hex encoded
      // after read from WDB
      case java.sql.Types.NUMERIC:
        createTable.append(" varchar(64) ");
        break;
      // single - java.sql.Types.FLOAT 4 byte float, single precision
      case java.sql.Types.FLOAT:
        createTable.append(" varchar(64) ");
        break;
      }// switch
      if (itColumns.hasNext()) {
        createTable.append(", ");
      }
    }// loop... columns
    createTable.append(")");

    try {
      String msg = "CREATE TABLE " + parDTO.getTableName();
      this.myLogger.info(msg);
      if (dbName.toLowerCase().startsWith("mysql")) {
        createTable.append(" ");
        createTable.append("CHARACTER SET utf8;");
        varcharType = " TEXT ";
      }
      this.myLogger.debug(createTable.toString());
      this.myGeneric_DAO.sendSQLupdate(createTable.toString());
    } catch (Exception ex) {
      String msg = "Error creating table '" + parDTO.getTableName()
          + "' inside database";
      msg += "\n " + this.maxCharsPerLine(createTable.toString(), 80) + "\n"
          + this.maxCharsPerLine(ex.getMessage(), 80);
      this.myLogger.error(msg);
      throw new WDBMgr_CouldNotCreateSchemaException(msg, ex);
    }

  }// createDatabaseSchemeTMP

  private String maxCharsPerLine(String parBreakThis, int parMaxLength) {
    StringBuffer retValue = new StringBuffer();

    for (int i = 0; i < parBreakThis.length(); i++) {
      retValue.append(parBreakThis.charAt(i));
      if (((i + 1) % parMaxLength) == 0) {
        retValue.append("\n");
      }
    }

    return retValue.toString();
  }// maxCharsPerLine

  /**
   * Insert or Update an object inside the database<br/> <br/> This method gets
   * the DTO values that were generated from "readWDBFile" and stores them
   * inside the database<br/>
   * 
   * @param parItem
   *          DTOs with the WDB information
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
    this.myLogger.debug("insertOrUpdateToSQLDB, Single Object...");

    ObjectsWritten retValue = new ObjectsWritten();
    // Connect to database
    // - done
    // Create DAO
    // - done

    // first dto -> to identify the data
    testIfTableExistsAndCreate(parItem);

    // do stuff
    int numInsert = 0;
    int numUpdate = 0;
    int numErrorInsert = 0;
    int numErrorUpdate = 0;
    int numSkipped = 0;
    DTO_Interface queryDB = parItem.createObject();
    // firstCol == ID of the object
    String firstCol = "";
    Iterator itDummy = queryDB.getColumns();
    if (itDummy.hasNext()) {
      firstCol = (String) itDummy.next();
    }
    queryDB.addPrimaryKeyColumn(firstCol);
    queryDB.addPrimaryKeyColumn(WDB_Helper.INF_LOCALE);
    try {
      // insert or update?
      queryDB.setColumnValue(firstCol, parItem.getColumnValue(firstCol));
      queryDB.setColumnValue(WDB_Helper.INF_LOCALE, parItem
          .getColumnValue(WDB_Helper.INF_LOCALE));

      //.out.println(queryDB.toString());

      // this.myLogger.debug("Writing object: "+ loopDTO.toString() );
      if (this.myGeneric_DAO.getCountObject(queryDB) != 0) {
        if (parUpdate == true) {
          this.myGeneric_DAO.updateObject(parItem);
          numUpdate++;
        } else {
          numSkipped++;
        }
      } else {
        this.myGeneric_DAO.insertObject(parItem);
        numInsert++;
      }
    } catch (Exception ex) {
      this.myLogger.debug(ex.getMessage());
      //ex.printStackTrace();
      numErrorInsert++;
    }
    retValue.setNumErrorInsert(numErrorInsert);
    retValue.setNumErrorUpdate(numErrorUpdate);
    retValue.setNumInsert(numInsert);
    retValue.setNumUpdate(numUpdate);
    retValue.setNumSkipped(numSkipped);

    // Disconnect from database
    return retValue;

  }// insertOrUpdateToSQLDB

  /**
   * Insert or Update objects inside the database<br/>
   * 
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
   */
  public ObjectsWritten insertOrUpdateToSQLDB(Collection parItems,
      boolean parUpdate) throws WDBMgr_CouldNotCreateSchemaException {

    this.myLogger.debug("insertOrUpdateToSQLDB, Collection...");
    ObjectsWritten retValue = new ObjectsWritten();
    // Connect to database
    // - done
    // Create DAO
    // - done

    if (parItems.size() == 0) {
      this.myLogger.debug("Empty Collection to insert -> returning");
      return retValue;
    }

    // first dto -> to identify the data
    DTO_Interface myDTO = null;
    Iterator itWDBS = parItems.iterator();
    if (itWDBS.hasNext()) {
      myDTO = (DTO_Interface) itWDBS.next();
    }
    testIfTableExistsAndCreate(myDTO);

    // do stuff
    this.myLogger.debug("Number of objects: " + parItems.size());
    Iterator itItems = parItems.iterator();
    DTO_Interface loopDTO = null;
    int numInsert = 0;
    int numUpdate = 0;
    int numErrorInsert = 0;
    int numErrorUpdate = 0;
    int numSkipped = 0;
    DTO_Interface queryDB = myDTO.createObject();
    // firstCol == ID of the object
    String firstCol = "";
    Iterator itDummy = queryDB.getColumns();
    if (itDummy.hasNext()) {
      firstCol = (String) itDummy.next();
    }
    System.out.println("firstCol: " + firstCol);
    queryDB.addPrimaryKeyColumn(firstCol);
    queryDB.addPrimaryKeyColumn(WDB_Helper.INF_LOCALE);

    //    String pKey1 = queryDB.getPrimaryKeyColumns()[0];
    //    String pKey2 = queryDB.getPrimaryKeyColumns()[1];
    //    String tabName = queryDB.getTableName();
    //    String wdbLocale = "'"
    //        + myDTO.getColumnValue(WDB_Helper.INF_LOCALE).toString() + "'";

    while (itItems.hasNext()) {
      loopDTO = (DTO_Interface) itItems.next();
      try {
        // insert or update?

        // Not needed for "speed"
        queryDB.setColumnValue(firstCol, loopDTO.getColumnValue(firstCol));
        queryDB.setColumnValue(WDB_Helper.INF_LOCALE, loopDTO
            .getColumnValue(WDB_Helper.INF_LOCALE));

        //.out.println(queryDB.toString());

        // this.myLogger.debug("Writing object: "+ loopDTO.toString() );
        //if (this.myGeneric_DAO.getCountObject(queryDB) != 0) {
        if (this.myGeneric_DAO.getCountObject(queryDB) != 0) {
          if (parUpdate == true) {
            int numUpd = this.myGeneric_DAO.updateObject(loopDTO);
            //            if (numUpd == 0 ) {
            //              System.out.println(numUpd);
            //              System.out.println(queryDB);
            //              System.exit(0);
            //            }
            numUpdate++;
          } else {
            numSkipped++;
          }
        } else {
          //          System.out.println("INS: "+ this.myGeneric_DAO.getCountObject(queryDB));
          //          System.out.println("INS: "+ queryDB);
          //          System.exit(0);
          this.myGeneric_DAO.insertObject(loopDTO);
          numInsert++;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        this.myLogger.debug(ex.getMessage());
        numErrorInsert++;
      }
    }
    retValue.setNumErrorInsert(numErrorInsert);
    retValue.setNumErrorUpdate(numErrorUpdate);
    retValue.setNumInsert(numInsert);
    retValue.setNumUpdate(numUpdate);
    retValue.setNumSkipped(numSkipped);

    // Disconnect from database

    return retValue;
  }// insertOrUpdate

  /**
   * Return Meta Infos about the database, may be usefull to adjust the program
   * <br/>
   * 
   * @return meta data
   * 
   * @throws WDBMgr_DatabaseException
   *           metadata could not be read
   * @throws WDBMgr_Exception
   *           connection to database not available
   */
  public DatabaseMetaData getDatabaseMetaData()
      throws WDBMgr_DatabaseException, WDBMgr_Exception {
    DatabaseMetaData retValue = null;
    try {
      retValue = this.myConfDB.getConnection().getMetaData();
    } catch (SQLException ex) {
      throw new WDBMgr_DatabaseException("Error reading database meta data", ex);
    } catch (NullPointerException ex) {
      throw new WDBMgr_Exception(
          "Error reading metadata - maybe database is down", ex);
    }
    return retValue;
  }// getDatabaseMetaData

  public Collection getAllObjects(DTO_Interface parDTO)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception {
    // check if DB schema exists
    testIfTableExistsAndCreate(parDTO);

    try {
      return this.myGeneric_DAO.getAllObjects(parDTO);
    } catch (ObjectNotFoundException ex) {
      throw new WDBMgr_NoDataAvailableException("getAllObjects", ex);
    } catch (Exception ex) {
      throw new WDBMgr_Exception("getAllObjects", ex);
    }
  }// getAllObjects

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
    // check if DB schema exists
    testIfTableExistsAndCreate(parDTO);

    try {
      // this.myGeneric_DAO.getAllObjects(parDTO, orderDTO);
      return this.myGeneric_DAO.getAllObjects(parDTO, orderDTO);
      // this.myGeneric_DAO.getObjects(parDTO, orderDTO, 1,
      // Integer.MAX_VALUE);
    } catch (ObjectNotFoundException ex) {
      throw new WDBMgr_NoDataAvailableException("getAllObjects", ex);
    } catch (Exception ex) {
      throw new WDBMgr_Exception("getAllObjects", ex);
    }
  }// getAllObjects

  /**
   * Read all contents from the database<br/> <br/>
   * 
   * @param parTablename
   *          Read data from this table
   * @return Collection containing DTO_Interface instances
   * @throws Exception
   *           If database access went wrong
   */
  public Collection getAllObjects(String parTablename)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception {
    Generic_DTO myDTO = WDB_Helper
        .createDTOfromXML(null, parTablename + ".xml");
    testIfTableExistsAndCreate(myDTO);
    try {
      return this.myGeneric_DAO.getAllObjects(myDTO);
    } catch (ObjectNotFoundException ex) {
      throw new WDBMgr_NoDataAvailableException("getAllObjects", ex);
    } catch (Exception ex) {
      throw new WDBMgr_Exception("getAllObjects", ex);
    }
  }// getAllObjects

  /**
   * Read all contents from the database<br/> <br/> Just another way to
   * retrieve data from the database, equivalent to getAllObjects()<br/>
   * 
   * @param parXMLfilename
   *          Name of the XML file that contains the config
   * @return Collection containing DTO_Interface instances
   * @throws Exception
   *           If database access went wrong
   */
  public Collection getAllObjectsFromXML(String parXMLfilename)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception {
    Generic_DTO myDTO = WDB_Helper.createDTOfromXML(null, parXMLfilename);
    testIfTableExistsAndCreate(myDTO);
    try {
      return this.myGeneric_DAO.getAllObjects(myDTO);
    } catch (ObjectNotFoundException ex) {
      throw new WDBMgr_NoDataAvailableException("getAllObjects", ex);
    } catch (Exception ex) {
      throw new WDBMgr_Exception("getAllObjectsFromXML", ex);
    }
  }

  /**
   * Return number of entries inside the specific database table<br/>
   * 
   * @param parTable
   *          name of the database table
   * 
   * @return integer
   */
  public int getCountOfTable(String parTable) {
    try {
      Generic_DTO qbeDTO = WDB_Helper.createDTOfromXML(null, parTable + ".xml");
      testIfTableExistsAndCreate(qbeDTO);
      // System. out.println("getCountOf");
      return this.myGeneric_DAO.getCountObject(qbeDTO);
    } catch (Exception ex) {
      // ex.printStackTrace();
      return 0;
    }
  }// getCountOfTable

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
  public int getCountOf(DTO_Interface parDTO) {

    try {
      testIfTableExistsAndCreate(parDTO);
      // System. out.println("getCountOf");
      return this.myGeneric_DAO.getCountObject(parDTO);
    } catch (Exception ex) {
      // ex.printStackTrace();
      return 0;
    }
  }// getCountOf

  /**
   * Create database table<br/>
   * 
   * Make sure, the corresponding XML file exists<br/>
   * 
   * @throws WDBMgr_Exception
   *           Error while accessing database
   */
  public void createTable(String parTable) throws WDBMgr_Exception {
    try {
      Generic_DTO qbeDTO = WDB_Helper.createDTOfromXML(null, parTable + ".xml");
      this.vecTabExists.remove(parTable);
      testIfTableExistsAndCreate(qbeDTO);
    } catch (Exception ex) {
      // ex.printStackTrace();
    }
  }// createTable

  /**
   * A table is changed. A column is renamed<br/>
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
   * @param parTable Re-create this table and keep all data
   * @param parPrevWDBVersion This was the previous WDB version from that we update
   * 
   */
  public void alterTableAddCol(String parPrevWDBVersion, String parTable)
      throws WDBMgr_Exception {
    this.myLogger.debug("Updating existing table...");
    // Create a TMP table with the new specification 
    // Copy all data from SRC table to TMP
    // Drop table SRC
    // Create the table SRC_NEW with the new specification
    // Copy all data from TMP table to the SRC_NEW table
    // Drop table TMP

    Generic_DTO newDTO = WDB_Helper.createDTOfromXML(null, parTable + ".xml");
    Generic_DTO tmpDTO = WDB_Helper.createDTOfromXML(null, parPrevWDBVersion
        + "/" + parTable + ".xml");
    tmpDTO.setTableName(parTable + "_TMP");
    Generic_DTO oldDTO = WDB_Helper.createDTOfromXML(null, parPrevWDBVersion
        + "/" + parTable + ".xml");

    this.myLogger.debug("Creating TMP table");
    createDatabaseSchemeTMP(oldDTO);

    this.myLogger.debug("Copying data from OLD to TMP");
    try {
      this.myGeneric_DAO.copyContents(oldDTO, tmpDTO);
    } catch (SQLException ex) {
      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.myLogger.debug(mySW.getBuffer().toString());

      throw new WDBMgr_Exception(
          "Could not copy data to TMP table for db update", ex);
    }

    // Drop existing table
    StringBuffer dropOldTable = new StringBuffer();
    dropOldTable.append("drop table ");
    dropOldTable.append(this.dbprefix);
    dropOldTable.append(parTable);
    this.myLogger.debug("Dropping OLD table: " + parTable);
    try {
      this.myGeneric_DAO.sendSQLupdate(dropOldTable.toString());
    } catch (Exception ex) {
      String msg = "Error dropping old table '" + parTable
          + "' inside database";
      this.myLogger.error(msg);
      throw new WDBMgr_CouldNotCreateSchemaException(msg, ex);
    }

    // Re-Create table
    this.myLogger.debug("Re-create OLD table with new specs: "
        + newDTO.getTableName());
    createDatabaseScheme(newDTO);

    this.myLogger.debug("Copying data from TMP to NEW: "
        + tmpDTO.getTableName() + "_" + newDTO.getTableName());
    try {
      this.myGeneric_DAO.copyContents(tmpDTO, oldDTO); //newDTO );
    } catch (SQLException ex) {
      throw new WDBMgr_Exception(
          "Could not copy data to NEW table for db update", ex);
    }

    // Drop TMP table
    StringBuffer dropTMPTable = new StringBuffer();
    dropTMPTable.append("drop table ");
    dropTMPTable.append(this.dbprefix);
    dropTMPTable.append(parTable + "_TMP");
    this.myLogger.debug("Dropping TMP table: " + parTable + "_TMP");
    try {
      this.myGeneric_DAO.sendSQLupdate(dropTMPTable.toString());
    } catch (Exception ex) {
      String msg = "Error dropping TMP table '" + parTable
          + "' inside database";
      this.myLogger.error(msg);
      throw new WDBMgr_CouldNotCreateSchemaException(msg, ex);
    }

  }// alterTableAddCol

  /**
   * A table is changed. A column is renamed<br/>
   * This method is needed if several updates to a database
   * table have to be done.<br/>
   * It is impossible to rename a column and to add a column to
   * a table.<br/>
   * 
   * So one can use this method and provide an extra XML config
   * file, which can differ from the original one.<br/>
   * This patch method will look for an extra XML config
   * file which has a special name (_dest.xml suffix).<br/>
   * You provide a special patch directory, which contains the
   * current version of the table (eg. itemcache.xml) and the destination
   * config (eg. itemcache_dest.xml) which contains the table with the
   * renamed column(s)<br/>
   * Using this method, you can rename a column and then apply other patches,
   * eg adding a column. So you can do many steps.<br/>
   * 
   * This has to be done in several single steps.
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
   * @param parTable Re-create this table and keep all data
   * @param parPrevWDBVersion This was the previous WDB version from that we update
   * 
   */
  public void alterTableStepRename(String parPrevWDBVersion, String parTable)
      throws WDBMgr_Exception {
    this.myLogger.debug("Updating existing table...");
    // Create a TMP table with the new specification 
    // Copy all data from SRC table to TMP
    // Drop table SRC
    // Create the table SRC_NEW with the new specification
    // Copy all data from TMP table to the SRC_NEW table
    // Drop table TMP

    Generic_DTO newDTO = WDB_Helper.createDTOfromXML(null, parPrevWDBVersion
        + "/" + parTable + "_dest" + ".xml");
    Generic_DTO tmpDTO = WDB_Helper.createDTOfromXML(null, parPrevWDBVersion
        + "/" + parTable + ".xml");
    tmpDTO.setTableName(parTable + "_TMP");
    Generic_DTO oldDTO = WDB_Helper.createDTOfromXML(null, parPrevWDBVersion
        + "/" + parTable + ".xml");

    this.myLogger.debug("Creating TMP table");
    createDatabaseSchemeTMP(oldDTO);

    this.myLogger.debug("Copying data from OLD to TMP");
    try {
      this.myGeneric_DAO.copyContents(oldDTO, tmpDTO);
    } catch (SQLException ex) {
      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.myLogger.debug(mySW.getBuffer().toString());

      throw new WDBMgr_Exception(
          "Could not copy data to TMP table for db update", ex);
    }

    // Drop existing table
    StringBuffer dropOldTable = new StringBuffer();
    dropOldTable.append("drop table ");
    dropOldTable.append(this.dbprefix);
    dropOldTable.append(parTable);
    this.myLogger.debug("Dropping OLD table: " + parTable);
    try {
      this.myGeneric_DAO.sendSQLupdate(dropOldTable.toString());
    } catch (Exception ex) {
      String msg = "Error dropping old table '" + parTable
          + "' inside database";
      this.myLogger.error(msg);
      throw new WDBMgr_CouldNotCreateSchemaException(msg, ex);
    }

    // Re-Create table
    this.myLogger.debug("Re-create OLD table with new specs: "
        + newDTO.getTableName());
    createDatabaseScheme(newDTO);

    this.myLogger.debug("Copying data from TMP to NEW: "
        + tmpDTO.getTableName() + "_" + newDTO.getTableName());
    try {
      this.myGeneric_DAO.copyContents(tmpDTO, newDTO); //newDTO );
    } catch (SQLException ex) {
      throw new WDBMgr_Exception(
          "Could not copy data to NEW table for db update", ex);
    }

    // Drop TMP table
    StringBuffer dropTMPTable = new StringBuffer();
    dropTMPTable.append("drop table ");
    dropTMPTable.append(this.dbprefix);
    dropTMPTable.append(parTable + "_TMP");
    this.myLogger.debug("Dropping TMP table: " + parTable + "_TMP");
    try {
      this.myGeneric_DAO.sendSQLupdate(dropTMPTable.toString());
    } catch (Exception ex) {
      String msg = "Error dropping TMP table '" + parTable
          + "' inside database";
      this.myLogger.error(msg);
      throw new WDBMgr_CouldNotCreateSchemaException(msg, ex);
    }

  }// alterTableStepRename

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
    this.myLogger.debug("alterTableRenameCol: Updating existing table...");
    // Create a TMP table with the new specification 
    // Copy all data from SRC table to TMP
    // Drop table SRC
    // Create the table SRC_NEW with the new specification
    // Copy all data from TMP table to the SRC_NEW table
    // Drop table TMP

    Generic_DTO newDTO = WDB_Helper.createDTOfromXML(null, parTable + ".xml");
    Generic_DTO tmpDTO = WDB_Helper.createDTOfromXML(null, parPrevWDBVersion
        + "/" + parTable + ".xml");
    tmpDTO.setTableName(parTable + "_TMP");
    Generic_DTO oldDTO = WDB_Helper.createDTOfromXML(null, parPrevWDBVersion
        + "/" + parTable + ".xml");

    this.myLogger.debug("Creating TMP table");
    createDatabaseSchemeTMP(oldDTO);

    this.myLogger.debug("Copying data from OLD to TMP");
    try {
      this.myGeneric_DAO.copyContents(oldDTO, tmpDTO);
    } catch (SQLException ex) {
      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.myLogger.debug(mySW.getBuffer().toString());

      throw new WDBMgr_Exception(
          "Could not copy data to TMP table for db update", ex);
    }

    // Drop existing table
    StringBuffer dropOldTable = new StringBuffer();
    dropOldTable.append("drop table ");
    dropOldTable.append(this.dbprefix);
    dropOldTable.append(parTable);
    this.myLogger.debug("Dropping OLD table: " + parTable);
    try {
      this.myGeneric_DAO.sendSQLupdate(dropOldTable.toString());
    } catch (Exception ex) {
      String msg = "Error dropping old table '" + parTable
          + "' inside database";
      this.myLogger.error(msg);
      throw new WDBMgr_CouldNotCreateSchemaException(msg, ex);
    }

    // Re-Create table
    this.myLogger.debug("Re-create OLD table with new specs: "
        + newDTO.getTableName());
    createDatabaseScheme(newDTO);

    this.myLogger.debug("Copying data from TMP to NEW: "
        + tmpDTO.getTableName() + "_" + newDTO.getTableName());
    try {
      this.myGeneric_DAO.copyContents(tmpDTO, newDTO); //newDTO );
    } catch (SQLException ex) {
      throw new WDBMgr_Exception(
          "Could not copy data to NEW table for db update", ex);
    }

    // Drop TMP table
    StringBuffer dropTMPTable = new StringBuffer();
    dropTMPTable.append("drop table ");
    dropTMPTable.append(this.dbprefix);
    dropTMPTable.append(parTable + "_TMP");
    this.myLogger.debug("Dropping TMP table: " + parTable + "_TMP");
    try {
      this.myGeneric_DAO.sendSQLupdate(dropTMPTable.toString());
    } catch (Exception ex) {
      String msg = "Error dropping TMP table '" + parTable
          + "' inside database";
      this.myLogger.error(msg);
      throw new WDBMgr_CouldNotCreateSchemaException(msg, ex);
    }

  }// alterTableRenameCol

  public Collection getGroupBy(DTO_Interface parTable,
      String[] parStrArrGroupBy, String[] parStrArrAggrs,
      DTO_Interface parQBEobj) throws WDBMgr_Exception {

    try {
      return this.myGeneric_DAO.getGroupBy(parTable, parStrArrGroupBy,
          parStrArrAggrs, parQBEobj);
    } catch (ObjectNotFoundException ex) {
      throw new WDBMgr_NoDataAvailableException("getGroupBy", ex);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new WDBMgr_Exception("getGroupBy", ex);
    }
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
    try {
      return this.myGeneric_DAO.deleteObject(parObj);
    } catch (Exception ex) {
      throw new WDBMgr_Exception("deleteObject", ex);
    }
  }

}// SQLManager
