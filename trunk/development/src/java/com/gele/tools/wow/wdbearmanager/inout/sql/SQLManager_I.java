/*
 * Created on 19.03.2005
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: SQLManager_I.java 209 2006-09-04 14:34:24Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/sql/SQLManager_I.java $
 * $Rev: 209 $
 *
 */
package com.gele.tools.wow.wdbearmanager.inout.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Collection;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_CouldNotCreateSchemaException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_DatabaseException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoDataAvailableException;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;

/**
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 209 $
 * 
 * This interface describes all possible actions for the user
 * to access the database<br/>
 * 
 * This interface is part of the global API for the WoWWDBManager,
 * meaning users can code scripts and access these functions.<br/>
 *
 */
public interface SQLManager_I {

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
  public abstract ObjectsWritten insertOrUpdateToSQLDB(Collection parItems,
      boolean parUpdate) throws WDBMgr_CouldNotCreateSchemaException,
      WDBMgr_Exception;

  /**
   * Insert or Update an object inside the database<br/>
   * <br/>
   * This method gets the DTO values that were generated from "readWDBFile"
   * and stores them inside the database<br/> 
   * 
   * @param parItem  DTOs with the WDB information
   * @param parUpdate true - also do update
   * @return infos about INSERT, UPDATE
   * 
   * @throws WDBMgr_CouldNotCreateSchemaException This method will set up the database
   * schema, if it does not exist. If this does not work -> this exception is thrown. 
   * @throws WDBMgr_Exception
   */
  public abstract ObjectsWritten insertOrUpdateToSQLDB(DTO_Interface parItem,
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
  public abstract Collection getAllObjects(DTO_Interface parDTO,
      DTO_Interface orderDTO) throws WDBMgr_NoDataAvailableException,
      WDBMgr_Exception;

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
   * Read all contents from the database<br/>
   * <br/>
   * This method receives the name of the table from which
   * it reads all entries.<br/>
   * Possible values are:
   * <ul>
   * <li> creaturecache </li>
   * <li> gameobjectcache </li>
   * <li> itemcache </li>
   * <li> itemtextcaxhe </li>
   * <li> npccache </li>
   * <li> pagetextcache </li>
   * <li> questcache</li>
   * </ul>
   * Make sure you only use lower case letters for the table name!
   * 
   * @param parTablename Read data from this table
   * @return Collection containing DTO_Interface instances
   * @throws WDBMgr_NoDataAvailableException no data inside database
   * @throws WDBMgr_Exception If database access went wrong
   */
  public abstract Collection getAllObjects(String parTablename)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception;

  /**
   * Return number of entries inside the specific database table<br/>
   * 
   * @param parTable name of the database table
   * 
   * @return integer number of objects stored inside the database
   */
  public abstract int getCountOfTable(String parTable);

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
   * @return integer
   */
  public int getCountOf(DTO_Interface parDTO);

  /**
   * Read all contents from the database<br/>
   * <br/>
   * Just another way to retrieve data from the database, equivalent to
   * getAllObjects()<br/>
   * 
   * @param parXMLfilename Name of the XML file that contains the config
   * @return Collection containing DTO_Interface instances
   * @throws WDBMgr_NoDataAvailableException no data inside database
   * @throws WDBMgr_Exception If database access went wrong
   * 
   */
  public Collection getAllObjectsFromXML(String parXMLfilename)
      throws WDBMgr_NoDataAvailableException, WDBMgr_Exception;

  /**
   * Return the JDBC connection<br/>
   * 
   * May be needed to overcome the API<br/>
   * <br/>
   * 
   * 
   * @return JDBC connection
   */
  public Connection getConnection();

  /**
   * The prefix the user set in the config file<br/>
   * 
   * @return name of the prefix (pre-prended before the name)
   */
  public String getDBPrefix();
  
  /**
   * Create database table<br/>
   * 
   * Make sure, the corresponding XML file exists<br/>
   * 
   * @throws WDBMgr_Exception Error while accessing database
   */
  public void createTable( String parTable ) throws WDBMgr_Exception;

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
   * @param parTable Re-create this table and keep all data
   * @param parPrevWDBVersion This was the previous WDB version from that we update
   * 
   */
  public void alterTableAddCol(String parPrevWDBVersion, String parTable) throws WDBMgr_Exception;
  
  /**
   * Alter a table: Rename a column inside the table and
   * dont use the current definition, but a special one.<br/>
   * <br/>
   * This method requires 2 files inside the patch directory:<br/>
   * (eg: itemcache)
   * <ul>
   * <li> itemcache.xml </li>
   * <li> itemcache_dest.xml </li>
   * </ul>
   * itemcache.xml<br/>
   * Contains the description of the table as it is before the patch<br/>
   * itemcache_dest.xml<br/>
   * Contains the description of the table after the patch is applied, the
   * destination. Normally one would expect the "wdbear-config/itemcache.xml",
   * but this special method makes it possible to use another description, except
   * the "current" one. This is very important if columns of a table were
   * renamed AND added.<br/>
   * <br/>
   * 
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
  public void alterTableStepRename(String parPrevWDBVersion, String parTable) throws WDBMgr_Exception;
  

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
  public void alterTableRenameCol(String parPrevWDBVersion, String parTable) throws WDBMgr_Exception;


  public Collection getGroupBy(DTO_Interface parTable,
      String[] parStrArrGroupBy, String[] parStrArrAggrs,
      DTO_Interface parQBEobj) throws WDBMgr_Exception;

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

}// SQLManager_I
