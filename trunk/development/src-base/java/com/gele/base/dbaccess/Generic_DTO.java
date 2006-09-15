/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: Generic_DTO.java 216 2006-09-10 16:55:52Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/Generic_DTO.java $
 * $Rev: 216 $
 */

package com.gele.base.dbaccess;

/**
 * Klasse f�ür alle DTO-Datenbank-Objekte.<br/>
 * <br/>
 * Die Datenbankobjekte repr�sentieren direkt Tabellen einer SQL-Datenbank.
 * Zur effizienten Nutzung dieser Objekte implementieren diese das Interface
 * DBT_DataObject<br/>
 * Fremdschlü�sselbeziehungen werden nicht unterstü�tzt!<br/>
 *
 */

// standard java packages
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public class Generic_DTO implements DTO_Interface, java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 6299066929320232646L;

  private Category getLogger() {
    // DebugLevel; default is FATAL, which means only FATAL messages will
    // be logged (See log4j 1.2.9 or above)
    //Category.getInstance(this.getClass()).setLevel(Level.INFO);
    return Logger.getLogger(this.getClass());
  }

  // precision
  // For CHAR this means the size of the string
  // etc.
  protected int precision = 0;

  // display debugInfo
  protected boolean displayDebugInfo = false;

  // contains all columns and values
  protected HashMap c_hmaColumns = new HashMap();

  // column-name<>Boolean // to monitor changes
  protected HashMap c_hmaColumnBoolean = new HashMap();

  // name of the table
  protected String c_strTableName = "";

  // name of the primary key column
  protected Vector vecPKeyColNames = new Vector();

  // Column->Type
  protected Hashtable c_htaColumnType = new Hashtable();

  // columns, in the order the user provided them
  // using the hashtable returns the cols in a random order
  protected Vector vecColums = new Vector();

  /**
   * Set the columns for this class<br/>
   *
   * @param cpa_arrColumns String array with the columns
   */
  public void setColumns(String cpa_arrColumns[]) {
    this.c_hmaColumns = new HashMap();
    this.c_hmaColumnBoolean = new HashMap();
    this.vecColums = new Vector();

    for (int i = 0; i < cpa_arrColumns.length; i++) {
      this.c_hmaColumns.put(cpa_arrColumns[i], null);
      this.vecColums.add(cpa_arrColumns[i]);
    }
    // automatically adds the columns to the "changed" monitor
    resetChanged();
  } //setColumns

  public void addColumn(String parColumn) {
    this.c_hmaColumns.put(parColumn, null);
    this.vecColums.add(parColumn);
    resetChanged();
  }

  /**
   * Add a column with a specific type<br/>
   * 
   * @param parColumn  name of the column
   * @param parColType java.sql.Types
   */
  public void addColumn(String parColumn, int parColType) {
    this.c_hmaColumns.put(parColumn, null);
    this.c_htaColumnType.put(parColumn, new Integer(parColType));
    this.vecColums.add(parColumn);
  }

  /**
   * Set the columns for this class<br/>
   *
   * @param cp_iteColumns String Iterator with the columns
   */
  public void setColumns(Iterator cp_iteColumns) {
    this.c_hmaColumns = new HashMap();
    this.c_hmaColumnBoolean = new HashMap();
    this.vecColums = new Vector();

    while (cp_iteColumns.hasNext()) {
      String ct_strCol = (String) cp_iteColumns.next();

      this.c_hmaColumns.put(ct_strCol, null);
      this.vecColums.add(ct_strCol);
    }
    // automatically adds the columns to the "changed" monitor
    resetChanged();
  } //setColumns

  /**
   * Returns all properties, that are not null<br/>
   * <br/>
   * Very generic implementation, works for all objects, where the columns
   * were set<br/>
   *
   * @return Properties  KEY row specifier; VALUE value of the attribute
   */
  public Properties getObjectProperties() {
    Properties ct_prpProps = new Properties();

    for (Iterator ct_iteProps = this.getColumns(); ct_iteProps.hasNext();) {
      String ct_strCol = (String) ct_iteProps.next();

      if (this.getColumnValue(ct_strCol) != null) {
        ct_prpProps.put(ct_strCol, this.getColumnValue(ct_strCol));
      }
    }
    return ct_prpProps;
  } // getObjectProperties

  //########## getObjectProperties ##########

  // Reset all attributes to 'not changed'
  public void resetChanged() {
    for (Iterator ct_iteProps = this.getColumns(); ct_iteProps.hasNext();) {
      String ct_strCol = (String) ct_iteProps.next();

      this.c_hmaColumnBoolean.put(ct_strCol, new Boolean(false));
    }
  } // resetChanged

  /**
   * Return the type of a column<br/>
   *
   * See java.sql.types for more details<br/>
   *
   * @return type of the column, -1 if column was not found
   *
   * @param parColName name of the column
   */
  public int getColumnType(String parColName) {
    //int retValue = -1;
    Integer ct_intRet = (Integer) this.c_htaColumnType.get(parColName);
    if (ct_intRet == null) {
      return -1;
    } else {
      return ct_intRet.intValue();
    }
  } // getColumnType

  /**
   * Set the type for a column<br/>
   *
   * See java.sql.types for more infos about the type.
   *
   * @param parColName Name of the column
   * @param parSqlType Value
   */
  public void setColumnType(String parColName, int parSqlType) {
    this.c_htaColumnType.put(parColName, new Integer(parSqlType));
  } // setColumnType

  //########## resetChanged ##########

  /**
   * Returns all changed properties, even NULL values<br/>
   * <br/>
   * A value is considered as "changed", if the correspondig "set...()" method
   * was called<br/>
   *
   * @return HashMap  KEY row specifier; VALUE value of the attribute
   */
  public HashMap getChangedProperties() {
    HashMap ct_hmaHMap = new HashMap();

    for (Iterator ct_iteProps = this.getColumns(); ct_iteProps.hasNext();) {
      String ct_strCol = (String) ct_iteProps.next();
      Boolean ct_booIsChanged = (Boolean) this.c_hmaColumnBoolean
          .get(ct_strCol);

      if ((ct_booIsChanged != null) && ct_booIsChanged.booleanValue())
        ct_hmaHMap.put(ct_strCol, this.getColumnValue(ct_strCol));
    }
    return ct_hmaHMap;
  } // getChangedProperties

  /**
   * Returns true, if the specified column was changed<br/>
   *
   * @return true, column value was changed, else false
   * @param columnName name of the column
   * @throws IllegalArgumentException if the columnName does not belong to this DTO
   */
  public boolean hasChanged(String columnName) {
    if (!this.c_hmaColumns.containsKey(columnName)) {
      getLogger().debug("hasChanged: " + this);
      throw new IllegalArgumentException("hasChanged: " + this + "\n"
          + "Column: '" + columnName + "' not found");
    }

    Boolean ct_booIsChanged = (Boolean) this.c_hmaColumnBoolean.get(columnName);

    if ((ct_booIsChanged != null) && ct_booIsChanged.booleanValue())
      return true;
    else
      return false;
  } // hasChanged

  /**
   * Returns the name of the underlying SQL table<br/>
   * <br/>
   * @return tableName  String, reflecting the underlying TABLE
   */
  public String getTableName() {
    return this.c_strTableName;
  }

  /**
   * Set the tablename<br/>
   * @param cp_strTableName name of the database table; default is empty string
   */
  public void setTableName(String cp_strTableName) {
    this.c_strTableName = cp_strTableName;
  } // setTableName

  //########## setTableName ##########

  /**
   * Return all columns<br/>
   * This method returns an Iterator that contains all collumns that belong
   * to the specific database table<br/>
   * Using this method in combination with "getTableName()" provides full
   * information about the database table and the columns<br/>
   *
   * @return Column names (String)
   */
  public Iterator getColumns() {
    return this.vecColums.iterator();
    //return this.c_hmaColumns.keySet().iterator();
  } // getColumns

  //########## getColumns #########

  /**
   * Returns the number of columns<br/>
   * <br/>
   *
   * @return number of columns that belong to this object
   */
  public int getColumnsSize() {
    return this.c_hmaColumns.keySet().size();
  } // getColumnsSize

  //########## getColumnsSize ##########

  /**
   * Set an attribute using the column name<br/>
   *
   * @param cp_strColName column to be changed
   * @param cp_objValue   new value for the column
   * @throws IllegalArgumentException if the column was not found
   */
  public void setColumnValue(String cp_strColName, Object cp_objValue)
      throws IllegalArgumentException {
    displayDebug("Generic_DTO: setColumnValue: " + cp_strColName);
    if (!this.c_hmaColumns.containsKey(cp_strColName)) {
      getLogger().debug("** this: " + this.getClass());
      throw new IllegalArgumentException("** this: " + this.getClass()
          + "\n" + "Column: '" + cp_strColName + "' not found");
    }
    // 2003-02-03
    // There seems to be a bug in the Oracle Driver
    // concerning PreparedStatements
    // If one wants to insert "" (empty string),
    // Oracle converts it to NULL
    // This "patch" fixes it for the DTOs
    if (cp_objValue instanceof String) {
      if (cp_objValue != null) {
        String tmpString = (String) cp_objValue;
        //        if (tmpString.length() == 0) {
        //          // empty String
        //          cp_objValue = null;
        //        }
      }
    }
    // column found -> set the value
    this.c_hmaColumns.put(cp_strColName, cp_objValue);
    // mark it as "changed"
    this.c_hmaColumnBoolean.put(cp_strColName, new Boolean(true));
    displayDebug("Generic_DTO: setColumnValue// END");
  } // setColumnValue

  //########## setColumnValue ##########

  /**
   * Get an value of a column <br/>
   * May return 'null' if no value was set *or* null was given as an attribute<br/>
   *
   * @param cp_strColName column to be changed
   * @return the value of the COLUMN
   * @throws IllegalArgumentException if the column was not found
   */
  public Object getColumnValue(String cp_strColName)
      throws IllegalArgumentException {
    if (!this.c_hmaColumns.containsKey(cp_strColName))
      throw new IllegalArgumentException("Column: '" + cp_strColName
          + "' not found");
    // column found -> set the value
    return this.c_hmaColumns.get(cp_strColName);
  } // getColumnValue

  //########## getColumnValue ##########

  /**
   * Return the primary key of the table that belongs to this object<br/>
   * @return Object that contains the value of the primary key
   */
  public Object[] getPrimaryKeyValues() {
    Object[] retValue = new Object[this.vecPKeyColNames.size()];
    for( int i = 0; i<vecPKeyColNames.size(); i++ ) {
      String colName = (String)vecPKeyColNames.elementAt(i);
      retValue[i] = getColumnValue(colName);
    }
    return retValue;
  }

  /**
   * Returns a list of primary columns, <br/>
   * @return String that contains the value of the primary key
   */
  public String[] getPrimaryKeyColumns() {
    String[] retValue = new String[vecPKeyColNames.size()];
    for( int i = 0; i<vecPKeyColNames.size(); i++ ) {
      retValue[i] = (String)vecPKeyColNames.elementAt(i);
    }
    
    return retValue;
  }

  /**
   * Set the name of the primary key for the object<br/>
   * @param cp_strPKey value for the primary key
   */
  public void addPrimaryKeyColumn(String cp_strPKey) {
    this.vecPKeyColNames.add( cp_strPKey );
  }

  /**
   * Displays debug infos, if "displayDebugInfo" is set to true<br/>
   * Message is printed using "getLogger().debug()"<br/>
   * <br/>
   * Warning: Uses class global variable "displayDebugInfo"
   *
   * @param cp_strText debug message
   */
  private void displayDebug(String cp_strText) {
    if (this.displayDebugInfo)
      getLogger().debug(cp_strText);
  } //displayDebug

  //########## displayDebug ##########

  /**
   * Creates the SQL "order by" statement for a given DTO<br/>
   * <br/>
   * Every attribute is considered a String like "ASC" or "DESC"
   * to specify the sorting<br/>
   * If, eg. the attribute (column) "PERSON" is set to "ASC" the a statement
   * will be added "ORDER BY TABLE.PERSON ASC"<br/>
   *
   * @throws IllegalArgumentException
   * @throws Exception  something else went wrong
   * @param cp_dtoObj  Object, that contains the search strings
   * @return String  maybe emtpy, if the user did not specify any of the
   * attributes, else a SQL "order by..." string
   */
  private String generateOrderBy(DTO_Interface cp_dtoObj)
      throws IllegalArgumentException, Exception {
    String ct_strRetValue = "";

    String orderBy = "";

    for (Iterator ct_iteProps = this.getColumns(); ct_iteProps.hasNext();) {
      String ct_strCol = (String) ct_iteProps.next();

      orderBy = (String) this.getColumnValue(ct_strCol);
      if (orderBy != null) {
        if (orderBy.toUpperCase().equals("ASC")
            || orderBy.toUpperCase().equals("DESC")) {
        } else {
          throw new IllegalArgumentException("OrderBy: Column '" + ct_strCol
              + "' contains value '" + orderBy + "', must be 'ASC' or 'DESC'");
        }
        if (ct_strRetValue.length() != 0)
          ct_strRetValue += ", ";
        ct_strRetValue += cp_dtoObj.getTableName() + "." + ct_strCol + " "
            + orderBy;
      }
    }
    //    if( ct_strRetValue.length() != 0 ) ct_strRetValue = " ORDER BY "+ct_strRetValue;
    return ct_strRetValue;

  } // generateOrderBy

  // generateOrderBy

  /**
   * Special implementation of equals to compare DTOs
   *
   * @param anObject compares itself to this object
   * @return true - attributes are identical, false else
   * @throws IllegalArgumentException if the parameter does not implement DTO_Interface
   */
  public boolean equals(Object anObject) throws IllegalArgumentException {
    boolean retValue = true;

    if (!(anObject instanceof DTO_Interface)) {
      return false;
    }

    Object attrVale = null;
    Generic_DTO dtoObject = (Generic_DTO) anObject;

    for (Iterator ct_iteProps = this.getColumns(); ct_iteProps.hasNext();) {
      String ct_strCol = (String) ct_iteProps.next();

      attrVale = this.getColumnValue(ct_strCol);
      // null?
      if (dtoObject.getColumnValue(ct_strCol) == null) {
        if (attrVale != null) { // NOT equal
          return false;
        }
      } else {
        // maybe the PARAM object is null
        if (attrVale == null) { // THIS not null, anObject -> NULL
          return false;
        }
        if (!dtoObject.getColumnValue(ct_strCol).equals(attrVale)) {
          return false;
        }
      } // attribute of THIS not null
    } // for... all attributes
    return retValue;
  } // equals

  /**
   * Returns all columns, where these two objects differ<br/>
   *
   * @param anObject compares itself to this object
   * @return Vector containing all columns, that contain different values
   * Vector can be empty (NOT null, but empty); elements are of type "String"
   * @throws IllegalArgumentException if the parameter does not implement DTO_Interface
   */
  public Vector diff(Object anObject) throws IllegalArgumentException {
    Vector retValue = new Vector();

    if (!(anObject instanceof DTO_Interface)) {
      throw new IllegalArgumentException(
          "Parameter does not implement DTO_Interface");
    }

    Object attrVale = null;
    Generic_DTO dtoObject = (Generic_DTO) anObject;

    for (Iterator ct_iteProps = this.getColumns(); ct_iteProps.hasNext();) {
      String ct_strCol = (String) ct_iteProps.next();

      attrVale = this.getColumnValue(ct_strCol);
      // null?
      if (dtoObject.getColumnValue(ct_strCol) == null) {
        if (attrVale != null) { // NOT equal
          retValue.addElement(ct_strCol);
          continue;
        }
      } else {
        // maybe the PARAM object is null
        if (attrVale == null) { // THIS not null, anObject -> NULL
          retValue.addElement(ct_strCol);
          continue;
        }
        if (!dtoObject.getColumnValue(ct_strCol).equals(attrVale)) {
          retValue.addElement(ct_strCol);
          continue;
        }
      } // attribute of THIS not null
    } // for... all attributes
    return retValue;
  } // diff

  /**
   * Copy all attributes from one object to another<br/>
   * <br/>
   * If the are not of the same type, an Exeption is raised during
   * runtime, because the attribute may not match.
   * If the objects are different, but the source object contains
   * all attributes of the destination object, no Exception is raised.<br/>
   * Changes the Dest object directly
   *
   * @param parObjDest
   */
  public void copyAttributes(DTO_Interface parObjDest) {
    String ct_strPropName = "";
    for (Iterator e = this.getColumns(); e.hasNext();) {
      ct_strPropName = (String) e.next();
      parObjDest.setColumnValue(ct_strPropName, this
          .getColumnValue(ct_strPropName));
    }
  } // copyAttributes

  /**
   * Make an identical copy of this object
   *  
   */
  public DTO_Interface createObject() {
    Generic_DTO retValue = new Generic_DTO();

    retValue.setTableName(this.getTableName());
    // column types
    Iterator itCols = this.getColumns();
    String colName = "";
    while (itCols.hasNext()) {
      colName = (String) itCols.next();
      retValue.addColumn(colName, this.getColumnType(colName));
    }
    if (this.getPrimaryKeyColumns() != null) {
      retValue.setPrimaryKeyColumns(this.getPrimaryKeyColumns());
    }

    return retValue;
  }// createObject
  
  private void setPrimaryKeyColumns(String[] parPKCols) {
    this.vecPKeyColNames = new Vector();
    for( int i = 0; i<parPKCols.length ; i++ ) {
      String colName = parPKCols[i];
      vecPKeyColNames.add(colName);
    }
  }

  /**
   * Returns the precision of the datatype. If the columnType is
   * CHAR this represents the size of the string.<br/>
   * Normally this value is 0 (zero)<br/>
   * 
   * @return precision of the datatype
   */
  public int getPrecision() {
    return precision;
  }

  public void setPrecision(int precision) {
    this.precision = precision;
  }
  
  public String toString() {
    StringBuffer retValue = new StringBuffer();

    retValue.append("[ TABLENAME: "+this.getTableName()+"\n");
    for (Iterator ct_iteProps = this.getColumns(); ct_iteProps.hasNext();) {
      String ct_strCol = (String) ct_iteProps.next();

      retValue.append("  ("+ct_strCol+", '"+this.getColumnValue(ct_strCol)+"')\n");
    }
    retValue.append("]\n");
    return retValue.toString();
  }

} // Generic_DTO.java

//########## Generic_DTO.java ##########
