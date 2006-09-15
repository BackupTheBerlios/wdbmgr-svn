/*
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: DTO_Interface.java 216 2006-09-10 16:55:52Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/DTO_Interface.java $
 * $Rev: 216 $
 */

package com.gele.base.dbaccess;


// standard java packages
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/**
 * Interface for all objects, that are stored inside a database, filesystem, etc<br/>
 * <br/>
 * This interface represents a row inside a SQL table, a row inside a CSV,
 * a row read from SAP system, etc etc.<br/>
 * 
 * All attributes can be accessed by "Object getColumnValue( String parNameOfColumn )"
 * To set an attribute, please use "setColumnValue( COL, valueObject )";
 * <br/>
 * Users should not use this interface/direct implementations, this is
 * far to primitive.<br/>
 * <br/>
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock 1997-</a>
 */

public interface DTO_Interface {

  /**
   * Returns all properties, that are not null<br/>
   * <br/>
   * @return Properties  KEY row specifier; VALUE value of the attribute
   */
  public Properties getObjectProperties();

  /**
   * Returns all changed properties, even NULL values<br/>
   * <br/>
   * @return HashMap  KEY row specifier; VALUE value of the attribute
   */
  public HashMap getChangedProperties();

  /**
   * Returns true, if the specified column was changed<br/>
   *
   * @return true, column value was changed, else false
   * @param columnName of the column
   * @throws IllegalArgumentException if the columnName does not belong to this DTO
   */
  public boolean hasChanged(String columnName);

  /**
   * Returns the name of the underlying SQL table<br>
   * <br/>
   * @return tableName  String, reflecting the underlying TABLE
   */
  public String getTableName();

  /**
   * Returns the name of the columns<br/>
   * <br/>
   * @return tableName  String, reflecting the underlying TABLE
   */
  public Iterator getColumns();

  /**
   * Returns the number of columns<br/>
   * <br/>
   *
   * @return number of columns that belong to this object
   */
  public int getColumnsSize();

  /**
   * Return the primary keys of the table that belongs to this object<br/>
   * <br/>
   * Order is according to get PrimaryKeyColumns
   * 
   * @see #getPrimaryKeyColumns()
   * 
   * @return Object that contains the value of the primary key
   */
  public Object[] getPrimaryKeyValues();

  /**
   * Returns a list of primary columns, <br/>
   * @return String that contains the value of the primary key
   */
  public String[] getPrimaryKeyColumns();

  /**
   * Add a primary column, so an object can have zero or many p'keyst<br/>
   * @param cp_strPKey value for the primary key
   */
  public void addPrimaryKeyColumn(String cp_strPKey);

  /**
   * Set an attribute using the column name<br/>
   *
   * @param cp_strColName column to be changed
   * @param cp_objValue   new value for the column
   * @throws IllegalArgumentException if the column was not found
   */
  public void setColumnValue(String cp_strColName, Object cp_objValue)
      throws IllegalArgumentException;

  /**
   * Get an value of a column <br/>
   * May return 'null' if no value was set *or* null was given as an
   * attribute<br/>
   *
   * @param cp_strColName column to be changed
   * @return the value of the COLUMN
   * @throws IllegalArgumentException if the column was not found
   */
  public Object getColumnValue(String cp_strColName)
      throws IllegalArgumentException;

  /**
   * Special implementation of equals to compare DTOs
   *
   * @param anObject compares itself to this object
   * @return true - attributes are identical, false else
   * @throws IllegalArgumentException if the parameter does not implement DTO_Interface
   */
  public boolean equals(Object anObject) throws IllegalArgumentException;

  /**
   * Returns all columns, where these two objects differ<br/>
   *
   * @param anObject compares itself to this object
   * @return Vector containing all columns, that contain different values
   * Vector can be empty (NOT null, but empty); elements are of type "String"
   * @throws IllegalArgumentException if the parameter does not implement DTO_Interface
   */
  public Vector diff(Object anObject) throws IllegalArgumentException;

  /**
   * Copy all attributes from one object to another<br/>
   * <br/>
   * If the are not of the same type, an Exeption is raised during
   * runtime<br/>
   * Changes the Dest object directly
   *
   * @param parObjDest
   */
  public void copyAttributes(DTO_Interface parObjDest);

  /**
   * Reset all attributes to 'not changed'<br/>
   * <br/>
   * Is called eg. when "updateObject" is invoked<br/>
   */
  public void resetChanged();

  /**
   * Return the type of a column<br/>
   *
   * See java.sql.types for more details<br/>
   *
   * @param parColName name of the column
   *
   * @return type of the column, -1 if column was not found
   *
   */
  public int getColumnType(String parColName);

  /**
   * Set the type for a column<br/>
   *
   * See java.sql.types for more infos about the type.
   *
   * @param parColName Name of the column
   * @param parSqlType Value
   */
  public void setColumnType(String parColName, int parSqlType);

  /**
   * Make an identical copy of this object<br/>
   * 
   * Does not copy the values, but all columns, types and
   * table info<br/>
   */
  public DTO_Interface createObject();

} // Interface DTO_Interface
//########## Interface DTO_Interface ##########
