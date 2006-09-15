
/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: DAO_Interface.java 205 2006-08-16 14:49:30Z gleibrock $
 * 
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-08-16 16:49:30 +0200 (Mi, 16 Aug 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 205 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/DAO_Interface.java $
 * $Rev: 205 $
 * 
 *
 */

package com.gele.base.dbaccess;

/**
 * Base Interface for all DAO classes.<br/>
 *
 * Every class that implements this Interface guarantees a minimum of
 * functionality for the user. Since these methods are very generic (eg.
 * DTO_Interface instead of the real implementation) they are kind of dangerous
 * for the user, since no type-checking is done.<br/>
 *
 */

// standard java packages
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import com.gele.base.dbaccess.exception.DatabaseTroubleException;
import com.gele.base.dbaccess.exception.ObjectNotDeletedException;
import com.gele.base.dbaccess.exception.ObjectNotFoundException;
import com.gele.base.dbaccess.exception.ObjectNotStoredException;
import com.gele.base.dbaccess.search.DBT_SearchConfig;

public interface DAO_Interface {

  /**
   * Set the config file for the dao
   * @param config includes methods to read out configdata like
   * dttype etc.
   */
  public void setConfiguration(DBConfig_I config);
  
  /**
   * Return the configuration from the dao
   * 
   * @return Class that contains the configuration for the DAO
   */
  public DBConfig_I getConfiguration();
  
  /**
   * Removes the object from the EIS, ie the relational database<br/>
   * This method will not delete any objects, that are referenced
   * by this one<br/>
   *
   * @see #deleteObject
   * 
   * @param parObj object, that should be deleted
   * @return int  number of deleted objects; objects that match the attributes
   *              that were set
   * @throws ObjectNotDeletedException problems occurred
   * @throws SQLException database problems
   */
  public int deleteObject(DTO_Interface parObj)
    throws ObjectNotDeletedException, DatabaseTroubleException;

  /**
   * Return the number of objects from the EIS that match the given object<br/>
   *
   * @param parQBEobj tries to retrieve objects that have similiar/equal attributes
   * like this one
   * @return number of objects found
   * @throws ObjectNotFoundException
   * @throws DatabaseTroubleException
   * @throws Exception
   */
  public int getCountObject(DTO_Interface parQBEobj)
    throws ObjectNotFoundException, DatabaseTroubleException, Exception;

  /**
   * Get objects out of the database using an optional SQL WHERE clause<br/>
   * <br/>
   *
   * @throws ObjectNotFoundException object could not be found inside database
   * @throws DatabaseTroubleException problems with the database
   * @param cp_strQuery  String, added to the select statement
   * @return number of objects
   */
  public int getCountObject(String cp_strQuery)
    throws ObjectNotFoundException, DatabaseTroubleException;

  /**
   * Inserts an object of the specific class inside the database<br/>
   * <br/>
   * This method calls the generic "insertObject()"<br/>
   * <br/>
   *
   * @see DTO_Interface
   * @see #insertObject
   *
   * @param parObj  Object to be stored inside the database
   * @return the object, it is possible that the DAO had to change some attributes
   * (eg assigned a primary key, etc)
   *
   * @throws ObjectNotStoredException  if the object has no properties,
   *         a null value was provided or there were database problems
   */
  public DTO_Interface insertObject(DTO_Interface parObj)
    throws ObjectNotStoredException;

  /**
   * Update an existing object inside a relational database<br/>
   *
   * Warning:<br/>
   * Only functions, if the primary key is numeric!<br/>
   *
   * @return number of updated objects
   * @param cp_dtoUpdateMe    - new object, store this object's attributes inside the database
   * @throws ObjectNotStoredException object could not be stored inside the database
   */
  public int updateObject(DTO_Interface cp_dtoUpdateMe)
    throws ObjectNotStoredException;

  /**
   * Get objects out of the database using an optional SQL WHERE clause<br/>
   * <br/>
   * @throws ObjectNotFoundException object could not be found inside database
   * @throws DatabaseTroubleException problems with the database
   * @throws Exception something else went wrong
   * @return Vector contains all found objects
   *
   * @param cp_strQuery  String, added to the select statement
   */
  public Vector getAllObjects(String cp_strQuery)
    throws ObjectNotFoundException, DatabaseTroubleException, Exception;

  /**
   * Returns all objects that match a given QueryByExample object<br/>
   * <br/>
   * @throws ObjectNotFoundException object could not be found inside database
   * @throws DatabaseTroubleException problems with the database
   * @throws Exception something else went wrong
   * @return Vector contains all found objects
   * @param parQBEobj find objects that match the attributes that were set
   */
  public Vector getAllObjects(DTO_Interface parQBEobj)
    throws ObjectNotFoundException, DatabaseTroubleException, Exception;

  /**
   * Returns all objects that match a given QueryByExample object<br/>
   * <br/>
   * The 'orderBy-object' contains "ASC" or "DESC" as values for
   * the attributes. They must be set using the 'setColumnValue()'<br/>
   * <br/>
   * eg:
   * orderByObj.setColumnValue( "ID", "DESC" );
   * -> this will order the objects descending by the ID column<br/>
   * It is also possible to set multiple sorting criterias <em>but</em> there
   * is no guaranteed order of the evaluation<br/>
   * eg:
   * orderByObj.setColumnValue( "ID", "DESC" );
   * orderByObj.setColumnValue( "NAME", "ASC" );
   * -> It is not clear, if the result set will be ordered by ID descending and
   * then by NAME ascending or vice versa!<br/>
   * <br/>
   * @throws ObjectNotFoundException object could not be found inside database
   * @throws DatabaseTroubleException problems with the database
   * @throws Exception something else went wrong
   * @return Vector contains all found objects
   * @param parQBEobj find objects that match the attributes that were set
   * @param parOrderBy set attributes to "ASC" or "DESC"
   */
  public Vector getAllObjects(
    DTO_Interface parQBEobj,
    DTO_Interface parOrderBy)
    throws ObjectNotFoundException, DatabaseTroubleException, Exception;

  /**
   * Returns objects that match a given QueryByExample object<br/>
   * <br/>
   * <br/>
   * Attention:!<br/>
   * Reading starts with number 1, not 0!<br/>
   * <br/>
   * @throws ObjectNotFoundException object could not be found inside database
   * @throws DatabaseTroubleException problems with the database
   * @throws Exception something else went wrong
   * @return Vector contains all found objects
   * @param parQBEobj find objects that match the attributes that were set
   * @param ipStart read beginning from this entry, must be >= 1
   * @param ipEnd read including this entry
   */
  public Vector getObjects(DTO_Interface parQBEobj, int ipStart, int ipEnd)
    throws ObjectNotFoundException, DatabaseTroubleException, Exception;

  /**
   * Returns all objects that match a given QueryByExample object<br/>
   * <br/>
   * The 'orderBy-object' contains "ASC" or "DESC" as values for
   * the attributes. They must be set using the 'setColumnValue()'<br/>
   * <br/>
   * eg:
   * orderByObj.setColumnValue( "ID", "DESC" );
   * -> this will order the objects descending by the ID column<br/>
   * It is also possible to set multiple sorting criterias <em>but</em> there
   * is no guaranteed order of the evaluation<br/>
   * eg:
   * orderByObj.setColumnValue( "ID", "DESC" );
   * orderByObj.setColumnValue( "NAME", "ASC" );
   * -> It is not clear, if the result set will be ordered by ID descending and
   * then by NAME ascending or vice versa!<br/>
   * <br/>
   * @throws ObjectNotFoundException object could not be found inside database
   * @throws DatabaseTroubleException problems with the database
   * @throws Exception something else went wrong
   * @return Vector contains all found objects
   * @param parQBEobj find objects that match the attributes that were set
   * @param parOrderBy set attributes to "ASC" or "DESC"
   * @param ipStart read beginning from this entry, must be >= 1
   * @param ipEnd read including this entry
   */
  public Vector getObjects(
    DTO_Interface parQBEobj,
    DTO_Interface parOrderBy,
    int ipStart,
    int ipEnd)
    throws ObjectNotFoundException, DatabaseTroubleException, Exception;

  /**
   * Get objects out of the database using an optional SQL WHERE clause<br/>
   * <br/>
   * Attention:!<br/>
   * Reading starts with number 1, not 0!<br/>
   * <br/>
   * <br/>
   * @throws ObjectNotFoundException object could not be found inside database
   * @throws DatabaseTroubleException problems with the database
   * @throws Exception something else went wrong
   * @return Vector contains all found objects
   *
   * @param cp_strQuery  String, added to the select statement
   *  contains "<TABLE_NAME> WHERE <blah blah>" May also contain more than one
   *  <table_name>.
   *  Eg: getObjects( <obj_instance>, "<table_name> where <table_name>.<col> = 1", 1, 20 );
   * @param ipStart retrieve object beginning from this index, must be >= 1
   * @param ipEnd   up to and including this index
   */
  public Vector getObjects(String cp_strQuery, int ipStart, int ipEnd)
    throws ObjectNotFoundException, DatabaseTroubleException, Exception;


  /**
   * Return the connection, if one was set<br/>
   *
   * @return Connection, see setConnection, maybe null if none was set
   */
  public Connection getConnection();

  /**
   * Read objects from the database using a specific search configuration<br/>
   *
   * @param parQBEobj Read objects of this kind
   * @param parSearchConfig Use this search configuration
   * @param cp_OrderByObj specify sorting
   * @param ipStart read beginning from this entry (starts with 1, not 0)
   * @param ipEnd read including this entry
   *
   * @return Vector containing found elements
   *
   * @throws ObjectNotFoundException No objects were found inside the EIS
   * @throws DatabaseTroubleException Problems accessing the EIS
   * @throws Exception Runtime error occurred, may OutOfMemory, etc.
   */
  public Vector getObjects(
    DTO_Interface parQBEobj,
    DBT_SearchConfig parSearchConfig,
    DTO_Interface cp_OrderByObj,
    int ipStart,
    int ipEnd)
    throws ObjectNotFoundException, DatabaseTroubleException, Exception;

  /**
  * Creates an SQL query string out of the object<br/>
  * <br/>
  * The given object is a 100% copy of this class, except that all types
  * are mapped to String and the value for an attribute can be a special
  * search string, which is resolved here<br/>
  * eg<br/>
  * Column     Value
  * BIRTHDAY   01-04-1980~01-04-2000
  * -> where BIRTHDAY between 01-04-1980, 01-04-2000
  *
  * @throws IllegalArgumentException  one of the search criterias did not match
  *  the given grammar (see PPC-Manager "Implementierungskonzept",
  *  "4.1.5 Filtern und Suchen von Auftr�gen"
  * @throws Exception  something else went wrong
  * @param cp_dtoObj  Object, that contains the search strings
  * @return String[] 0 -> all needed tables
  *                  1 -> "WHERE" String[1], meaning without "WHERE"
  */
  public String[] generateWhereClause(DTO_Interface cp_dtoObj)
    throws IllegalArgumentException, Exception;

  /**
  * Generate the order_by statement<br/>
  * <br/>
  * The 'orderBy-object' contains "ASC" or "DESC" as values for
  * the attributes. They must be set using the 'setColumnValue()'<br/>
  * <br/>
  * eg:
  * orderByObj.setColumnValue( "ID", "DESC" );
  * -> this will order the objects descending by the ID column<br/>
  * It is also possible to set multiple sorting criterias <em>but</em> there
  * is no guaranteed order of the evaluation<br/>
  * eg:
  * orderByObj.setColumnValue( "ID", "DESC" );
  * orderByObj.setColumnValue( "NAME", "ASC" );
  * -> It is not clear, if the result set will be ordered by ID descending and
  * then by NAME ascending or vice versa!<br/>
  *
  * @param cp_dtoObj these object's properties are used
  * @return Returns the ORDER_BY clause, (does not contain 'ORDER BY')
  * @throws Exception something went wrong when generating the statement
  */

  public String generateOrderBy(DTO_Interface cp_dtoObj) throws Exception;
  
  /**
   * Tribute to all SQL purists -> send a SQL update String directly to
   * the database<br/>
   * <br/>
   * 
   * @param parUpdString delete, create table, etc. 
   */
  public int sendSQLupdate(String parUpdString) throws SQLException;

} //endOfClass
