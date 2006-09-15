/*
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: DB_Generic_DAO.java 216 2006-09-10 16:55:52Z gleibrock $
 *
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 *
 * 
 * $LastChangedDate: 2006-09-10 18:55:52 +0200 (So, 10 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 216 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/DB_Generic_DAO.java $
 * $Rev: 216 $
 */

package com.gele.base.dbaccess;

/**
 * Klasse, die DAO Funktionalität für einfache Objekte in einer relationalen
 * Datenbank generisch implementiert. <br/>
 * 
 * Die hier bereitgestellten Funktionen schreiben ein Objekt in genau eine
 * Tabelle; Fremdschlüsselbeziehungen müssen händig aufgelöst und
 * implementiert werden. <br/>
 * 
 * Generisch implementiert wurden: <br/>
 * <ul>
 * <li>DELETE</li>
 * <li>GETCOUNT</li>
 * <li>INSERT</li>
 * <li>UPDATE</li>
 * <li>GETALL</li>
 * </ul>
 * 
 * 
 * Diese Klasse wurde mit dbTool und dem Modul 'Java' $Revision: 216 $ erzeugt.
 * <br/>
 * 
 * dbTool und das Modul 'Java' sind beide (C) by Gerhard Leibrock 1997-2003
 * <br/>
 * 
 * 
 * First release: 26 March 2002 <br/>Changes:
 * <ul>
 * <li> Added "dbprefix", all tables (tablename is stored inside DTO) can have a
 * prefix now: dbprefix_<TABLE> </li>
 * <li>2004-02-03 <br/>Added "IN(...)" for text and numeric elements
 * (searching)</li>
 * <li>2003-04-08</li>
 * Added switch, to turn on/off the resolving of attributes for the search
 * language <br/>
 * <li>2003-02-17</li>
 * Added getObjects_LWO, getCountObject_Column, getAggregate
 * <ul>
 * <li>getObjects_LWO: Only retrieve a subset of attributes (speed)</li>
 * <li>getCountObject_Colum: getCountObjects() needs "ID" column, now the user
 * is free to specify whatever column is necessary</li>
 * <li>getAggregate: retrieve SUM, MAX, MIN, AVG, etc. aggregates</li>
 * </ul>
 * <li>02 Aug 2002</li>
 * Rewrote "resolveNumber" -> Now accepts "MIN", "MAX" as keyword
 * </ul>
 * <br/>Important: <br/>The methods implemented are very generic, like the name
 * states. This means methods from this class function like the specialized
 * implementation, but are not optimized for a special case, like resolving of
 * foreign keys, optimized generation of WHERE clause, etc.
 * 
 * @author <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 * 
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 * 
 * @version $Revision: 216 $
 */

// standard java packages
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.exception.DatabaseTroubleException;
import com.gele.base.dbaccess.exception.ObjectNotDeletedException;
import com.gele.base.dbaccess.exception.ObjectNotFoundException;
import com.gele.base.dbaccess.exception.ObjectNotStoredException;
import com.gele.base.dbaccess.search.DBT_SearchConfig;
import com.gele.base.dbaccess.search.DBT_SearchCriteria;
import com.gele.base.dbaccess.search.ResolveSearchConfig;
import com.gele.base.dbaccess.search.ResolveSearchConfig_SQL;

public class DB_Generic_DAO implements DAO_Interface {

  // Using 'ident' one can get the version number of this class
  static String rcsid = "$Revision: 216 $";

  // Global (for the class) that contains the connection
  protected Connection sbConnection = null;

  // One Statement for all methods
  // protected Statement sbStatement = null;

  // store the PrimaryKeyCreator instance, if the user wants to set one
  protected PrimaryKeyCreator c_pkcUserInstance = null;

  // resolve attributes for a specific syntax
  // true -> use PPC like search language
  // false -> do not use any replacement for searching
  protected boolean resolveSearch = true;

  // Default comparators for specific types of data
  public static final String DEF_COMPARATOR_DATE = "=";

  public static final String DEF_COMPARATOR_TEXT = "LIKE";

  public static final String DEF_COMPARATOR_NUMBER = "=";

  // use this for search configurations
  private ResolveSearchConfig myRSC_SQL = new ResolveSearchConfig_SQL();

  Logger mylogger = Logger.getLogger(DB_Generic_DAO.class);

  // Place to store the different accepted date formats
  private String[] dateFormats = null;

  // // Dateformats specified inside dao_config.xml
  private static int guidateformat = 0;

  private static int sqldateformat = 1;

  private static int guitimestampformat = 2;

  private static int sqltimestampformat = 3;

  private static int sqltimestampformatquery = 4;

  // Configuration for the DAO
  private DBConfig_I config = null;

  // Use primary key factory to create PRIMARY_KEY for columns
  boolean bCreatePrimaryKey = true;

  // prefix for tables
  // tablename is stored inside DTO -> for every database access
  // the value of "prefix" is added to the tablename
  private String dbprefix = "";

  /**
   * Add a prefix to all table names<br/>
   * 
   * @param parDBprefix
   *          prefix, eg "my_"
   */
  public void setDBprefix(String parDBprefix) {
    this.dbprefix = parDBprefix;
  }

  public String getDBprefix() {
    return this.dbprefix;
  }

  protected Logger getLogger() {
    return this.mylogger;
  } // getLogger

  public DB_Generic_DAO() {
  } // empty constructor

  protected DB_Generic_DAO(DBConfig_I config) {
    this.config = config;
  }

  /**
   * Set the config file for the dao
   * 
   * @param config
   *          includes methods to read out configdata like dttype etc.
   */
  public void setConfiguration(DBConfig_I config) {
    this.config = config;
  }

  /**
   * Return the configuration from the dao
   * 
   * @see com.gele.base.dbaccess.DBConfig_I
   * 
   * @return Config for the Data Access Object
   */
  public DBConfig_I getConfiguration() {
    return this.config;
  }

  // -------------------------------------------------------------------
  // checkForWarning
  // Checks for and displays warnings. Returns true if a warning
  // existed
  // -------------------------------------------------------------------
  public boolean checkForWarning(SQLWarning warn) throws SQLException {
    boolean rc = false;

    // If a SQLWarning object was given, display the
    // warning messages. Note that there could be
    // multiple warnings chained together
    if (warn != null) {
      getLogger().debug("\n *** Warning ***\n");
      rc = true;
      while (warn != null) {
        getLogger().debug("SQLState: " + warn.getSQLState());
        getLogger().debug("Message:  " + warn.getMessage());
        getLogger().debug("Vendor:   " + warn.getErrorCode());
        getLogger().debug("");
        warn = warn.getNextWarning();
      }
    }
    return rc;
  } // checkForWarning

  // ########## checkForWarning ##########

  public void printSQLException(SQLException ex) {

    getLogger().debug("\n*** SQLException caught ***\n");

    while (ex != null) {
      this.printStackTrace(ex);
      getLogger().debug("SQLState: " + ex.getSQLState());
      getLogger().debug("Message:  " + ex.getMessage());
      getLogger().debug("Vendor:   " + ex.getErrorCode());
      ex = ex.getNextException();
      getLogger().debug("");
    }
  } // ########## printSQLException ##########

  private void printStackTrace(Exception ex) {
    if (ex == null) {
      return;
    }
    ByteArrayOutputStream myBOS = new ByteArrayOutputStream();
    PrintStream myPS = new PrintStream(myBOS);
    ex.printStackTrace(myPS);
    getLogger().debug(myBOS.toString());

  }

  /**
   * Return the connection, if one was set <br/>
   * 
   * @return Connection, see setConnection, maybe null if none was set
   */
  public Connection getConnection() {
    this.sbConnection = this.config.getConnection();

    if (this.sbConnection == null) {
      throw new NullPointerException("GenericDAO: Connection-object not set");
    }
    return this.sbConnection;
  } // ########## getConnection ##########

  /**
   * Returns the maximum value from a given column of a specific table <br/>
   * <br/> May be used to determine the maximum value of a column; for internal
   * usage only <br/> <br/> Set the attribute you want to retrieve the maximum
   * value <br/> <br/> method constructs a SQL string, sends it to the database
   * and returns the value the database returned <br/> <br/>
   * 
   * @param parObj
   *          Object with set property
   * 
   * @return int max value of the column
   */
  public int getMaxValueFromTable(DTO_Interface parObj) {

    int itRetValue = 2;

    try {
      String query = "SELECT MAX( " + getFirstProperty(parObj) + " ) FROM "
          + getDBprefix() + parObj.getTableName();
      Statement sbStatement = getConnection().createStatement(
          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      ResultSet rs = sbStatement.executeQuery(query);

      while (rs.next()) {
        itRetValue = rs.getInt(1);
      }
      rs.close();
      sbStatement.close();
    } catch (SQLException ex) {
      printSQLException(ex);
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
    }
    itRetValue++;
    if (itRetValue <= 1)
      itRetValue = 2;

    return itRetValue;
  } // ########## getMaxValueFromTable ##########

  /**
   * Returns a unique primary key for a given DTO_Interface <br/>
   * 
   * This is a very simple method, that connects to the database and retrieves
   * MAX( parObj.getPrimaryKeyColumn()s.firstValue ) from the parObj.getTableName()
   * database table. This method assumes, that the FIRST primary key for this object
   * is an integer. <br/>
   * Make sure, that getPrimaryKeyColumn() method returns the
   * name of the column that represents the primary key and make sure that the
   * primary key is an integer <br/>
   * 
   * WARNING: <br/> This is a very simple solution for the "find a unique
   * primary key" problem. If you set the TransactionLevel to
   * Connection.TRANSACTION_SERIALIZABLE than this method can be quite useful
   * <br/>
   * This method will crash, if you defined more than one pkey.
   * 
   * @param parObj
   *          Get the MAX(parObj.getPrimaryKeyColumn())
   * @return it is assumed, that the primary key is an integer
   * @throws Exception
   *           if something went wrong (eg database connection, etc)
   */
  public long createPrimaryKey(DTO_Interface parObj) throws Exception {

    // did the user set a PrimaryKeyCreator
    if (this.getPrimaryKeyCreator() != null) {
      this.getPrimaryKeyCreator().setConnection(this.getConnection());
      return this.getPrimaryKeyCreator().createPrimaryKey(parObj);
    }

    long ltRetValue = 2;

    try {
      Statement sbStatement = getConnection().createStatement();
      String query = "SELECT MAX( " + parObj.getPrimaryKeyColumns()[0]
          + " ) FROM " + getDBprefix() + parObj.getTableName();
      ResultSet rs = sbStatement.executeQuery(query);

      while (rs.next()) {
        ltRetValue = rs.getLong(1);
      }
      rs.close();
      sbStatement.close();
    } catch (SQLException ex) {
      printSQLException(ex);
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
    }
    ltRetValue++;
    if (ltRetValue <= 1)
      ltRetValue = 2;

    return ltRetValue;
  } // ########## createPrimaryKey ##########

  /**
   * Set the class that generates a primary key needed to insert a new object
   * <br/>
   * 
   * @param cp_pkcUserInstance
   *          User specific implementation of a class that implements
   *          PrimaryKeyCreator interface. If not set, this class uses a default
   *          implementation.
   */
  public void setPrimaryKeyCreator(PrimaryKeyCreator cp_pkcUserInstance) {
    this.c_pkcUserInstance = cp_pkcUserInstance;
  } // ########## setPrimaryKeyCreator ##########

  /**
   * Return the class that generates a primary key needed to insert a new object
   * <br/><br/>
   * 
   * @return cp_pkcUserInstance May return null, if user did not set an
   *         implementation.
   */
  public PrimaryKeyCreator getPrimaryKeyCreator() {
    if (this.c_pkcUserInstance == null) {
      try {
        this.c_pkcUserInstance = PKC_Factory.createInstance(this.config);
      } catch (Exception ex) {
        this.getLogger().error(
            "get PKC() Error!!!!" + ex.getLocalizedMessage() + "\n");
        this.printStackTrace(ex);
        // tell me what to do
      }
    }

    return this.c_pkcUserInstance;
  } // getPrimaryKeyCreator

  // ########## getPrimaryKeyCreator ##########

  /**
   * Generates an SQL WHERE clause string for the given parameters <br/><br/>
   * The Vector contains object instances and any set property is used to
   * generate the "order_by" statement. <br/>
   * 
   * @param parQBEobj
   *          Query by Example object
   * @param parSORTobjs
   *          sorting criteria
   * @return SQL string (WHERE clause, including 'WHERE' statement)
   */
  protected String resolveSimpleSearchAndSorting(DTO_Interface parQBEobj,
      Vector parSORTobjs) {
    return resolveSimpleSearchAndSorting("", parQBEobj, parSORTobjs);
  }

  protected String resolveSimpleSearchAndSorting(String cp_strOptPrefix,
      DTO_Interface parQBEobj, Vector parSORTobjs) {
    //this.getLogger().debug(">> DB_Generic_DAO: resolveSimpleSearchAndSorting");

    StringBuffer sbQuery = new StringBuffer(); // parQBEobj.getTableName();

    // user did specify a WHERE clause (including WHERE)
    if (cp_strOptPrefix.length() != 0) {
      sbQuery.append(cp_strOptPrefix);
    }
    if (parQBEobj.getObjectProperties().size() > 0) {
      // user did specify a WHERE clause (including WHERE)
      if (cp_strOptPrefix.length() == 0) {
        sbQuery.append(" WHERE ");
      } else {
        sbQuery.append(" AND ");
      }
      Properties ct_propProps = parQBEobj.getObjectProperties();
      String ct_strPropName = "";
      Object ct_objPropValue = "";
      int itCounter = 0;

      for (Enumeration e = ct_propProps.propertyNames(); e.hasMoreElements();) {
        ct_strPropName = (String) e.nextElement();
        ct_objPropValue = ct_propProps.get(ct_strPropName);

        int itSQLtype = parQBEobj.getColumnType(ct_strPropName);

        switch (itSQLtype) {
        case java.sql.Types.DATE:
        case java.sql.Types.TIME:
        case java.sql.Types.TIMESTAMP:
          if (itCounter != 0)
            sbQuery.append(" AND ");
          try {
            sbQuery.append(getDBprefix());
            sbQuery.append(parQBEobj.getTableName());
            sbQuery.append(".");
            sbQuery.append(ct_strPropName);
            sbQuery.append(this.resolveDate(getDBprefix()
                + parQBEobj.getTableName(), ct_strPropName, ct_objPropValue
                .toString()));
          } catch (Exception ex) {
            sbQuery.append(getDBprefix() + parQBEobj.getTableName() + "."
                + ct_strPropName + " = '" + ct_objPropValue + "'");
          }
          break;

        case java.sql.Types.ARRAY:
        case java.sql.Types.BINARY:
        case java.sql.Types.BLOB:
        case java.sql.Types.CHAR:
        case java.sql.Types.JAVA_OBJECT:
        case java.sql.Types.LONGVARBINARY:
        case java.sql.Types.LONGVARCHAR:
        case java.sql.Types.OTHER:
        case java.sql.Types.STRUCT:
        case java.sql.Types.VARBINARY:
        case java.sql.Types.VARCHAR:
          if (itCounter != 0)
            sbQuery.append(" AND ");
          try {
            sbQuery.append(getDBprefix());
            sbQuery.append(parQBEobj.getTableName());
            sbQuery.append(".");
            sbQuery.append(ct_strPropName);
            sbQuery.append(this.resolveText(getDBprefix()
                + parQBEobj.getTableName(), ct_strPropName, ct_objPropValue
                .toString()));
          } catch (Exception ex) {
            sbQuery.append(getDBprefix() + parQBEobj.getTableName() + "."
                + ct_strPropName + " = '" + ct_objPropValue + "'");
          }
          break;

        default: // maybe datasource specific type
          if (itCounter != 0)
            sbQuery.append(" AND ");
          try {

            sbQuery.append(getDBprefix());
            sbQuery.append(parQBEobj.getTableName());
            sbQuery.append(".");
            sbQuery.append(ct_strPropName);
            sbQuery.append(this.resolveNumber(getDBprefix()
                + parQBEobj.getTableName(), ct_strPropName, ct_objPropValue
                .toString()));
          } catch (Exception ex) {
            sbQuery.append(getDBprefix() + parQBEobj.getTableName() + "."
                + ct_strPropName + " = " + ct_objPropValue);
          }
          break;
        } // switch
        itCounter++;
      } // QueryByExample object
    }
    // Sort: START
    if (parSORTobjs != null) {
      sbQuery.append(" ORDER BY ");
      boolean addComma = false;

      for (Enumeration enu = parSORTobjs.elements(); enu.hasMoreElements();) {
        DTO_Interface parSORTobj = (DTO_Interface) enu.nextElement();

        Properties ct_propProps = parSORTobj.getObjectProperties();
        String ct_strPropName = "";
        Object ct_objPropValue = "";
        int itCounter = 0;

        for (Enumeration e = ct_propProps.propertyNames(); e.hasMoreElements();) {
          ct_strPropName = (String) e.nextElement();
          // now we have all relevant items; the value is irrelevant
          // (the value is only relevant for the QBE object)
          // add "," (comma)
          if (addComma == false)
            addComma = true;
          else
            sbQuery.append(", ");
          sbQuery.append(getDBprefix() + parQBEobj.getTableName() + "."
              + ct_strPropName);

          itCounter++;
        } // QueryByExample object
      }
    }// parSort != null
    // Sort: END
    return sbQuery.toString();
  } // resolveSimpleSearchAndSorting  // ########## resolveSimpleSearchAndSorting ##########

  /**
   * Checks for an open Connection and closes it, if already open. <br/><br/>
   * Nota bene: <br/>Use this method to release the database resources or else
   * you may run out of resources very quickly and your runtime system may
   * become instable! <br/><br/>Warning <br/>After calling "release()" database
   * access is impossible! This method will free <em>all</em> resources needed
   * to access the database!
   */
  public void release() {
    try {
      // try {
      // if( sbStatement != null ) {
      // sbStatement.close();
      // sbStatement = null;
      // }
      // }catch (Exception exx ) {}
      try {
        if (this.sbConnection != null) {
          this.sbConnection.close();
          this.sbConnection = null;
        }
      } catch (Exception exxx) {
      }
    } catch (Exception ex) { // do nothing
    }
  } // release

  // ########## release ##########

  /**
   * Inserts an object of the specific class inside the database <br/><br/>If
   * the object was already stored, use the corresponding update...() method
   * <br/><br/>Only 'set' attributes are stored. This means you have to set an
   * attribute to make sure it will be stored inside the database <br/> <br/>eg
   * <br/>Calling 'setName( ... )' for a specific object will set the 'Name'
   * attribute and so it will be stored <br/><br/>This is a generic method for
   * all objects. <br/>This method which will store the instance. This is
   * possible since all Classes that cooperate with the database implement the
   * DTO_Interface interface <br/>
   * 
   * Warning: <br/>Will use "createPrimaryKey" which returns a long value and
   * sets this value using "parObj.setPrimaryKey( new Long(...) )"<br/>Make
   * sure that the primary key is a Long value!
   * 
   * @see com.gele.base.dbaccess.DTO_Interface
   * @see #insertObject
   * 
   * @param parObj
   *          Object to be stored inside the database
   * @return the object, it is possible that the DAO had to change some
   *         attributes (eg assigned a primary key, etc)
   * 
   * @throws ObjectNotStoredException
   *           if the object has no properties, a null value was provided or
   *           there were database problems
   */
  public DTO_Interface insertObject(DTO_Interface parObj)
      throws ObjectNotStoredException {
    getLogger().debug("Generic_DAO-insertObject: START");
    // Maybe the user forgot... (to avoid stupid NullPointer Exception)
    if (parObj == null) {
      throw new ObjectNotStoredException(
          "Object is null -> impossible to store");
    }

    if (this.usePrimaryKeyCreator()) {
      // create a primary key and set it
      long ltPKey = 0;

      //
      // to be fixed
      // 2006-09-09
      // Added more than one primary key

      try {
        ltPKey = createPrimaryKey(parObj);
      } catch (Exception ex) {
        throw new ObjectNotStoredException("Could not create primary key");
      }
      getLogger().debug("Generic_DAO-insertObject: setPrimaryKeyValue");
      getLogger().debug("Generic_DAO-insertObject: wert- " + ltPKey);
      parObj.setColumnValue(parObj.getPrimaryKeyColumns()[0], new Long(ltPKey));
    }
    getLogger().debug("Generic_DAO-insertObject: los gehts");
    try {
      String query = "INSERT INTO " + getDBprefix() + parObj.getTableName()
          + " ( ";
      String rows = "";
      String values = " VALUES ( ";

      Properties ct_propProps = parObj.getObjectProperties();
      if (parObj.getObjectProperties().size() > 0) {
        String ct_strPropName = "";
        Object ct_objPropValue = "";
        int itCounter = 0;

        for (Enumeration e = ct_propProps.propertyNames(); e.hasMoreElements();) {
          ct_strPropName = (String) e.nextElement();
          ct_objPropValue = ct_propProps.get(ct_strPropName);

          if (itCounter != 0) { // do not forget the ","
            rows += ", ";
            values += ", ";
          }
          if (ct_objPropValue == null) {
            query += ct_strPropName + " = NULL";
          } else {
            // Use prepared statement
            // (Handling of Date/Timestamp will break if not)
            rows += getDBprefix() + parObj.getTableName() + "."
                + ct_strPropName;
            values += "?";
          }
          itCounter++;
        } // QueryByExample object
      } else {
        throw new ObjectNotStoredException(
            "Object has no properties -> impossible to store \n"
                + parObj.toString());
      }
      // construct the insert statement
      query = query + rows + " ) " + values + " )";
      getLogger().debug("insertObject: " + query);
      PreparedStatement ppStatement = getConnection().prepareStatement(query);
      String colName = "";
      int counter = 1;
      for (Enumeration enu = ct_propProps.propertyNames(); enu
          .hasMoreElements();) {
        colName = (String) enu.nextElement();
        ppStatement.setObject(counter, parObj.getColumnValue(colName));
        counter++;
      }
      int res = ppStatement.executeUpdate();

      ppStatement.close();
      getLogger().debug("Anzahl eingefügter Daten: " + res);
    } // try()
    catch (SQLException ex) {
      throw new ObjectNotStoredException(ex.getMessage());
    }
    return parObj;
  } // insertObject

  // ########## insertObject ##########

  /**
   * Update an existing object inside a relational database <br/>
   * 
   * Warning: <br/>Only functions, if the primary key is numeric! <br/>
   * 
   * Calls "resetChanged" on the parameter to reset all changed attributes <br/>
   * 
   * @return number of updated objects
   * @param parObj -
   *          new object, store this object's attributes inside the database
   * @throws ObjectNotStoredException
   *           object could not be stored inside the database
   */
  public int updateObject(DTO_Interface parObj) throws ObjectNotStoredException {
    // Maybe the user forgot... (to avoid stupid NullPointer Exception)
    if (parObj == null) {
      throw new ObjectNotStoredException(
          "Object is null -> impossible to store");
    }
    if (parObj.getPrimaryKeyColumns().length == 0) {
      throw new ObjectNotStoredException(
          "Object has no primary key column - NULL");
    }

    //    if (parObj.getPrimaryKeyColumns().length == 1) {
    //      System.out.println("OUT: Nur 1 Prim�rschl�ssel!");
    //      System.out.println("Tabelle: "+ parObj.getTableName());
    //      for( int i = 0; i<parObj.getPrimaryKeyColumns().length; i++) {
    //        System.out.println(parObj.getPrimaryKeyColumns()[i]);
    //      }
    //      try {
    //        throw new Exception();
    //      }catch( Exception ex ) {
    //        ex.printStackTrace();
    //      }
    //      System.exit(0);
    //    }

    HashMap ct_propProps = parObj.getChangedProperties();
    String[] arrPKeys = parObj.getPrimaryKeyColumns();
    try {
      StringBuffer sbQuery = new StringBuffer();
      sbQuery.append("UPDATE " + getDBprefix() + parObj.getTableName()
          + " SET ");

      Vector vecCols = new Vector();
      if (parObj.getChangedProperties().size() > 0) {
        String ct_strPropName = "";
        Object ct_objPropValue = "";
        int itCounter = 0;

        for (Iterator e = ct_propProps.keySet().iterator(); e.hasNext();) {
          ct_strPropName = (String) e.next();
          ct_objPropValue = ct_propProps.get(ct_strPropName);

          for (int i = 0; i < arrPKeys.length; i++) {
            if (ct_strPropName.equals(arrPKeys[i])) {
              continue;
            }
          }

          if (itCounter != 0) { // do not forget the "," 
            sbQuery.append(", ");
          }
          if (ct_objPropValue == null) {
            sbQuery.append(ct_strPropName + " = NULL");
          } else {
            // Use prepared statement
            // (Handling of Date/Timestamp will break if not)

            sbQuery.append(ct_strPropName + " = ?");
            vecCols.addElement(ct_strPropName);
          }
          itCounter++;
        } // QueryByExample object
      } else {
        throw new ObjectNotStoredException(
            "Object has no properties -> impossible to store");
      }
      // retrieve the WHERE clause
      sbQuery.append(" WHERE ");
      for (int i = 0; i < arrPKeys.length; i++) {
        if (i != 0) {
          sbQuery.append(" AND ");
        }
        sbQuery.append(arrPKeys[i] + " = ?");
      }
      // + cp_dtoUpdateMe.getPrimaryKeyValue().toString();

      int counter = 1;
      PreparedStatement ppStatement = getConnection().prepareStatement(
          sbQuery.toString());
      String colName = "";
      for (Enumeration enu = vecCols.elements(); enu.hasMoreElements();) {
        colName = (String) enu.nextElement();
        ppStatement.setObject(counter, parObj.getColumnValue(colName));
        counter++;
      }
      getLogger().debug("updateObject: " + sbQuery.toString());
      for (int i = 0; i < arrPKeys.length; i++) {
        ppStatement.setObject(counter + i, parObj.getColumnValue(arrPKeys[i]));
      }
      int itRetValue = ppStatement.executeUpdate();
      ppStatement.close();

      // Statement sbStatement = getConnection().createStatement();
      // int itRetValue = sbStatement.executeUpdate(query);
      // sbStatement.close();

      parObj.resetChanged();

      // DEBUG
      //      if (itRetValue != 1) {
      //        System.out.println("OUT: query: " + sbQuery.toString());
      //        System.out.println("Anzahl: " + itRetValue);
      //        for (int i = 0; i < parObj.getPrimaryKeyColumns().length; i++) {
      //          System.out.println("PK: " + parObj.getPrimaryKeyColumns()[i]);
      //          System.out.println("VAL: "
      //              + parObj.getColumnValue(parObj.getPrimaryKeyColumns()[i]));
      //        }
      //        try {
      //          throw new Exception();
      //        } catch (Exception ex) {
      //          ex.printStackTrace();
      //          System.exit(0);
      //        }
      //      }

      return itRetValue;
    } // try()
    catch (Exception ex) {
      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.mylogger.debug(mySW.getBuffer().toString());

      throw new ObjectNotStoredException(ex.getMessage());
    }
  } // updateObject

  /**
   * Update an existing object inside a relational database <br/><br/>This
   * method can be used if the primary key column is <em>not</em> numeric,
   * since this method does not use the primary key to identify the row that
   * should be updated. <br/><em>If</em> your primary key is numeric, please
   * refer to the other implementation of "updateObject()" <br/>
   * 
   * Calls "resetChanged" on the parameter to reset all changed attributes <br/>
   * 
   * @return number of updated objects
   * @param parQBEobj -
   *          old object, means this object composes the WHERE clause
   * @param parObj -
   *          new object, store this object's attributes inside the database
   * @throws ObjectNotStoredException
   *           object could not be stored inside the database
   * @throws Exception
   *           something else went wrong
   */
  public int updateObject(DTO_Interface parQBEobj, DTO_Interface parObj)
      throws ObjectNotStoredException, Exception {
    // Maybe the user forgot... (to avoid stupid NullPointer Exception)
    if (parObj == null) {
      throw new ObjectNotStoredException(
          "Object is null -> impossible to store");
    }
    HashMap ct_propProps = parObj.getChangedProperties();
    try {
      String query = "UPDATE " + getDBprefix() + parObj.getTableName()
          + " SET ";

      if (parObj.getChangedProperties().size() > 0) {
        String ct_strPropName = "";
        Object ct_objPropValue = "";
        int itCounter = 0;

        for (Iterator e = ct_propProps.keySet().iterator(); e.hasNext();) {
          ct_strPropName = (String) e.next();
          ct_objPropValue = ct_propProps.get(ct_strPropName);

          if (itCounter != 0) // do not forget the ","
            query += ", ";
          if (ct_objPropValue == null) {
            query += ct_strPropName + " = NULL";
          } else {
            // Use prepared statement
            // (Handling of Date/Timestamp will break if not)

            query += ct_strPropName + " = ?";
          }
          itCounter++;
        } // QueryByExample object
      } else {
        throw new ObjectNotStoredException(
            "Object has no properties -> impossible to store");
      }
      query += resolveSimpleSearchAndSorting(parQBEobj, null);
      getLogger().debug("updateObject: " + query);

      PreparedStatement ppStatement = getConnection().prepareStatement(query);
      int itCounter = 1;
      String ct_strPropName = null;

      for (Iterator e = ct_propProps.keySet().iterator(); e.hasNext();) {
        ct_strPropName = (String) e.next();

        ppStatement.setObject(itCounter, ct_propProps.get(ct_strPropName));
        itCounter++;
      }
      int itRetValue = ppStatement.executeUpdate();
      ppStatement.close();

      parQBEobj.resetChanged();
      return itRetValue;
    } // try()
    catch (Exception ex) {
      throw new ObjectNotStoredException(ex.getMessage());
    }
  } // updateObject

  // ########## updateObject ##########

  /**
   * Deletes an object from the database <br/>
   * <br/>
   * Returns the number of deleted objects <br/>
   * 
   * @param parObj
   *          instance to be deleted from the database
   * @return int number of deleted objects; objects that match the attributes
   *         that were set
   * 
   * @throws ObjectNotDeletedException
   *           problems occurred
   * @throws SQLException
   */
  public int deleteObject(DTO_Interface parObj)
      throws ObjectNotDeletedException, DatabaseTroubleException {
    Statement sbStatement = null;

    try {
      sbStatement = getConnection().createStatement();
    } catch (SQLException ex) {
      throw new DatabaseTroubleException(ex);
    }

    try {
      String query = "DELETE FROM ";

      query += getDBprefix() + parObj.getTableName()
          + resolveSimpleSearchAndSorting(parObj, null);
      getLogger().debug("deleteObject: " + query);

      // getLogger().debug( "deleteObject_QUERY: "+ query );

      int itRetValue = sbStatement.executeUpdate(query);

      sbStatement.close();
      return itRetValue;
    } // try()
    catch (SQLException ex) {
      throw new ObjectNotDeletedException(ex.getMessage());
    }
  } // deleteObject

  // ########## deleteObject ##########

  // /**
  // * Retrieves the count of objects that match the specific
  // 'DBT_SearchConfig' entries<br/>
  // * <br/>
  // * @return number of objects that match the criteria
  // * @param parObj objects of this kind
  // * @param parSCobj search configuration
  // * @throws ObjectNotFoundException
  // * @throws DatabaseTroubleException
  // */
  // public int getCountObject( DTO_Interface parObj, DBT_SearchConfig parSCobj
  // )
  // throws ObjectNotFoundException,
  // DatabaseTroubleException {
  //
  // return getCountObject(parObj, parSCobj, null);
  // }//getCountObject
  // ########## getCountObject ##########

  // public int getCountObject( DTO_Interface parObj, DBT_SearchConfig
  // parSCobj, Vector cp_vecSortObjs )
  // throws ObjectNotFoundException,
  // DatabaseTroubleException {
  //
  // return getCountObject(
  // parObj,
  // resolveSearchAndSorting(parObj.getTableName(), parSCobj, cp_vecSortObjs)
  // );
  // }//getCountObject
  // ########## getCountObject ##########

  public int getCountObject(DTO_Interface parQBEobj)
      throws ObjectNotFoundException, DatabaseTroubleException, Exception {

    return getCountObject(parQBEobj, (Vector) null);
  } // getCountObject

  /**
   * Returns the number of found objects according to a given column <br/>
   * 
   * To use this method, the user *must* set an attribute to a value (the value
   * itself is not important.). This method scans for any set attribute and uses
   * it to count.
   * 
   * eg. <quote> Person myPerson = new Person();
   * myPerson.setNAME( "something" ); // value does not matter
   *                                  // the column is important
   * ...
   * int result = myDAO.getCountObject_Column( myPerson, null );
   * </quote>
   * <br/>
   * 
   * This produces a query like:
   * 
   * select count( distinct PERSON.NAME ) ) from ...<br/>
   *     *
   * @param parColObj Use this object to retrieve the number of entries
   *   This method searches the object for the set attribute and counts
   *   the found entries.
   * @param parQBEobj Use this object to specify the WHERE condition, can be null
   * 
   * @return number of entries found, may be 0 (ZERO)
   * @throws DatabaseTroubleException
   * @throws Exception
   */
  public int getCountObject_Column(DTO_Interface parColObj,
      DTO_Interface parQBEobj) throws DatabaseTroubleException, Exception {

    int retValue = -1;

    String query = "";
    String notNull = this.getFirstProperty(parColObj);

    query += "Select count(" + getDBprefix() + parColObj.getTableName() + "."
        + notNull + ") from " + getDBprefix() + parColObj.getTableName();

    if (parQBEobj != null) {
      query += " where ";
      String colName = "";
      Properties objProps = parQBEobj.getObjectProperties();
      for (Enumeration enu = objProps.keys(); enu.hasMoreElements();) {
        colName = (String) enu.nextElement();
        query += colName + " = ? ";
        if (enu.hasMoreElements()) {
          query += " and ";
        }
      }
    }
    query += ";";
    this.getLogger().debug(query);
    PreparedStatement ppStatement = getConnection().prepareStatement(query);
    int counter = 1;
    if (parQBEobj != null) {
      String colName = "";
      Properties objProps = parQBEobj.getObjectProperties();
      for (Enumeration enu = objProps.keys(); enu.hasMoreElements();) {
        colName = (String) enu.nextElement();
        ppStatement.setObject(counter, parQBEobj.getColumnValue(colName));
        counter++;
      }
    }
    ResultSet ct_rstRes = null;
    try {
      ct_rstRes = ppStatement.executeQuery();

      ct_rstRes.next();
      retValue = ((java.lang.Number) ct_rstRes.getObject(1)).intValue();

    } catch (SQLException ex) {
      ex.printStackTrace();
      throw new DatabaseTroubleException(ex);
    }

    return retValue;

    /*
     * String query = "";
     * 
     * if (parQBEobj != null) { query = parQBEobj.getTableName() +
     * this.resolveSimpleSearchAndSorting(parQBEobj, null); } else { query =
     * parColObj.getTableName(); }
     * 
     * return this.getCountObject_Column(parColObj, query);
     */
  } // getCountObject_Column

  /**
   * Return COUNT (number) of objects for a given criteria <br/>
   * 
   * @param parObj
   *          Specifies the column to be used for the COUNT
   * @param cp_strQuery
   *          Optional WHERE clause, must include "WHERE" statement
   * @return number of found entries, may be 0 (ZERO)
   * @throws DatabaseTroubleException
   * @throws Exception
   */
  public int getCountObject_Column(DTO_Interface parObj, String cp_strQuery)
      throws DatabaseTroubleException, Exception {
    int it_ReturnVal = 0;

    Statement sbStatement = null;
    ResultSet ct_rstRes = null;
    try {
      if (cp_strQuery == null)
        cp_strQuery = new String();
      // Read all entries that match to the WHERE clause
      String query = "";

      String column = this.getFirstProperty(parObj);

      query = "SELECT COUNT ( distinct " + getDBprefix()
          + parObj.getTableName() + "." + column + " ) FROM " + " "
          + cp_strQuery;
      getLogger().debug("getCountObject_Column: " + query);

      sbStatement = getConnection().createStatement();
      ct_rstRes = sbStatement.executeQuery(query);

      ct_rstRes.next();
      it_ReturnVal = ((java.lang.Number) ct_rstRes.getObject(1)).intValue();

    } // try
    catch (SQLException ex) {
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
      getLogger().error("getCountObject_Column: Exception");
    } finally {
      getLogger().debug("getCountObject_Column: finally");
      try {
        if (ct_rstRes != null) {
          ct_rstRes.close();
        }
        if (sbStatement != null) {
          sbStatement.close();
        }
      } catch (SQLException ex) {
        getLogger().error(
            "getCountObject_Column: Error closing resultset/statement");
        throw new DatabaseTroubleException(ex.getMessage());
      }
    }
    return it_ReturnVal;
  } // getCountObject_Column()

  /**
   * Return Aggregates from a query <br/>
   * 
   * Supported aggregates are:
   * 
   * MIN, MAX COUNT SUM, AVG
   * 
   * The value set inside the DTO is not checked against these supported
   * aggregates, to be open for new ones <br/>
   * 
   * eg. Salary mySalary = new Salary(); mySalary.setColumnValue( "AMOUNT",
   * "SUM" ); mySalary.setColumnValue( "AMOUNT", "MIN" );
   * mySalary.setColumnValue( "AMOUNT", "MAX" ); ... Collection retVal =
   * mySalaryDAO.getAggregate( mySalary, (DTO_Interface)null ); ...
   * 
   * This produces a query like:
   * 
   * select sum(salary.amount), min(salary.amount), max(salary.amount) ...
   * 
   * The returned Collection contains the aggregate values in the same order
   * they were specified for retrieval.
   * 
   * @param parCollAggrs
   *          The Collection that contains the objects with the aggregates Each
   *          object inside the column can only contain one aggregate.
   * 
   * @param parQBEobj
   *          Optional object to specify a WHERE clause. Set attributes to build
   *          the WHERE clause. Eg. setName( "Muller" ) will result in a WHERE
   *          clause containing "AND Name = 'Muller'.
   * 
   * @return Collection containing objects that represent the values
   * 
   * @throws DatabaseTroubleException
   * @throws Exception
   */
  public Collection getAggregate(Collection parCollAggrs,
      DTO_Interface parQBEobj) throws DatabaseTroubleException, Exception {

    String query = "";

    if (parQBEobj != null) {
      query = getDBprefix() + parQBEobj.getTableName()
          + this.resolveSimpleSearchAndSorting(parQBEobj, null);
    } else {
      Iterator itAggrs = parCollAggrs.iterator();

      if (itAggrs.hasNext() == false) {
        throw new IllegalArgumentException(
            "Generic_DAO: getAggregate -> Collection is empty");
      }

      query = getDBprefix() + ((DTO_Interface) itAggrs.next()).getTableName();
    }

    return this.getAggregate(parCollAggrs, query);
  } // getAggregate

  public Collection getGroupBy(Collection parCollGroupBy,
      Collection parCollAggrs, DTO_Interface parQBEobj)
      throws DatabaseTroubleException, Exception {

    String query = "";

    if (parQBEobj != null) {
      query = getDBprefix() + parQBEobj.getTableName()
          + this.resolveSimpleSearchAndSorting(parQBEobj, null);
    } else {
      Iterator itAggrs = parCollAggrs.iterator();

      if (itAggrs.hasNext() == false) {
        throw new IllegalArgumentException(
            "Generic_DAO: getAggregate -> Collection is empty");
      }

      query = getDBprefix() + ((DTO_Interface) itAggrs.next()).getTableName();
    }

    return this.getGroupBy(parCollGroupBy, parCollAggrs, query);
  } // getGroupBy

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
   * <br/> Dies liefert folgende SQL Anweisung:<br/>
   * <br/>
   * SELECT
   *        SUM(KOSTENPUNKTE), ARTIKEL, WE_NUMMER, <br/>
   *        ProbenID_Extern, FREIGABEDATUM, Kostenstelle, AUFTRAG <br/>
   * FROM KOSTEN_EXPORT <br/>
   * group by ARTIKEL, WE_NUMMER, ProbenID_Extern,<br/>
   *          FREIGABEDATUM, Kostenstelle,
   *          AUFTRAG<br/>
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
   * @throws DatabaseTroubleException
   * @throws Exception
   */
  public Collection getGroupBy(DTO_Interface parTable,
      String[] parStrArrGroupBy, String[] parStrArrAggrs,
      DTO_Interface parQBEobj) throws DatabaseTroubleException, Exception {

    String query = "";

    if (parQBEobj != null) {
      query = getDBprefix() + parQBEobj.getTableName()
          + this.resolveSimpleSearchAndSorting(parQBEobj, null);
    } else {
      // Iterator itAggrs = parStrArrGroupBy.iterator();

      if ((parStrArrGroupBy == null) || parStrArrGroupBy.length == 0) {
        throw new IllegalArgumentException(
            "Generic_DAO: getAggregate -> Collection is empty");
      }
      query = getDBprefix() + parTable.getTableName();
    }

    return this.getGroupBy(parStrArrGroupBy, parStrArrAggrs, query);
  } // getGroupBy

  public Collection getGroupBy(String[] parStrArrGroupBy,
      String[] parStrArrAggrs, String cp_strQuery)
      throws DatabaseTroubleException, Exception {
    Vector ct_vecReturnVal = new Vector();

    try {
      if (cp_strQuery == null) {
        cp_strQuery = new String();
      }
      // Read all entries that match to the WHERE clause
      String query = "";
      String aggQuery = "";
      String column = "";
      String queryGroupBy = "";

      String[] staColNamnes = new String[parStrArrGroupBy.length
          + parStrArrAggrs.length];

      // SUM, MAX, MIN, ETC
      int numElements = 0;

      String aggObj = null;
      for (int i = 0; i < parStrArrAggrs.length; i++) {
        aggObj = parStrArrAggrs[i];
        aggQuery += aggObj;
        // this.getLogger().debug(numElements +"/"+ aggObj );
        staColNamnes[numElements] = aggObj;
        if ((i + 1) < parStrArrAggrs.length) {
          aggQuery += ", ";
        }
        // number of specified aggregates
        numElements++;
      }

      // GROUP BY
      if (parStrArrGroupBy.length > 0 && aggQuery.length() != 0) {
        aggQuery += ", ";
        //queryGroupBy += "group by ";
      }
      // String gByObj = null;
      for (int j = 0; j < parStrArrGroupBy.length; j++) {
        column = parStrArrGroupBy[j];
        aggQuery += column;
        queryGroupBy += column;
        staColNamnes[numElements] = column;
        // getLogger().debug(numElements +"/"+ column );
        if ((j + 1) < parStrArrGroupBy.length) {
          aggQuery += ", ";
          queryGroupBy += ", ";
        }
        // number of specified aggregates
        numElements++;
      }

      query = "SELECT " + aggQuery + " FROM " + " " + cp_strQuery + " "
          + "group by " + queryGroupBy;

      getLogger().debug("getAggregate: " + query);

      Statement sbStatement = getConnection().createStatement();
      ResultSet ct_rstRes = sbStatement.executeQuery(query);

      // parse resultset
      HashMap record = null;
      while (ct_rstRes.next()) {
        record = new HashMap();
        for (int i = 1; i <= numElements; i++) {
          // this.getLogger().debug("i : "+ i);
          // this.getLogger().debug(staColNamnes[i-1]);
          record.put(staColNamnes[i - 1], ct_rstRes.getObject(i));
        }
        ct_vecReturnVal.addElement(record);
      }// loop

      ct_rstRes.close();
      sbStatement.close();
    } // try
    catch (SQLException ex) {
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.mylogger.debug(mySW.getBuffer().toString());

      this.mylogger.debug(ex.getMessage());
    }
    return ct_vecReturnVal;
  } // getGroupBy

  /**
   * 
   * 
   * 
   * @param parCollGroupBy
   *          DTO_Interface instances, set an attribute (Column) to an arbitrary
   *          value, then this column is used for the group by statement
   * 
   * @param parCollAggrs
   *          The Collection that contains the objects with the aggregates Each
   *          object inside the column can only contain one aggregate.
   * 
   * @param cp_strQuery
   * 
   * @return Collection containing the return values, the order is: aggregates,
   *         then the group by values
   * 
   * @throws DatabaseTroubleException
   * @throws Exception
   */
  public Collection getGroupBy(Collection parCollGroupBy,
      Collection parCollAggrs, String cp_strQuery)
      throws DatabaseTroubleException, Exception {
    Vector ct_vecReturnVal = new Vector();

    try {
      if (cp_strQuery == null)
        cp_strQuery = new String();
      // Read all entries that match to the WHERE clause
      String query = "";
      String aggQuery = "";
      String column = "";
      String queryGroupBy = "";

      // SUM, MAX, MIN, ETC
      Iterator itLoop = parCollAggrs.iterator();
      DTO_Interface ct_loop;
      int numElements = 0;

      while (itLoop.hasNext()) {
        ct_loop = (DTO_Interface) itLoop.next();
        column = this.getFirstProperty(ct_loop);
        aggQuery += ct_loop.getColumnValue(column) + "(" + column + ")";
        if (itLoop.hasNext()) {
          aggQuery += ", ";
        }
        // number of specified aggregates
        numElements++;
      }
      // GROUP BY
      itLoop = parCollGroupBy.iterator();
      if (itLoop.hasNext() && aggQuery.length() != 0) {
        aggQuery += ", ";
        queryGroupBy += "group by ";
      }
      while (itLoop.hasNext()) {
        ct_loop = (DTO_Interface) itLoop.next();
        column = this.getFirstProperty(ct_loop);
        aggQuery += column;
        queryGroupBy += column;
        if (itLoop.hasNext()) {
          aggQuery += ", ";
          queryGroupBy += ", ";
        }
        // number of specified aggregates
        numElements++;
      }

      query = "SELECT " + aggQuery + " FROM " + " " + cp_strQuery + " "
          + queryGroupBy;

      getLogger().debug("getAggregate: " + query);

      Statement sbStatement = getConnection().createStatement();
      ResultSet ct_rstRes = sbStatement.executeQuery(query);

      ct_rstRes.next();
      for (int i = 1; i <= numElements; i++) {
        ct_vecReturnVal.addElement(ct_rstRes.getObject(i));
      }

      ct_rstRes.close();
      sbStatement.close();
    } // try
    catch (SQLException ex) {
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
    }
    return ct_vecReturnVal;
  } // getGroupBy

  /**
   * Return Aggregates from the database <br/>
   * 
   * 
   * Hint: <br/><b>This is a generic method. This means it is not optimized for
   * a special database table and may be too simple for more sophisticated
   * tasks.</b><br/>
   * 
   * Supported aggregates are:
   * 
   * <ul>
   * <li>MIN, MAX</li>
   * <li>COUNT</li>
   * <li>SUM, AVG</li>
   * </ul>
   * 
   * The value set inside the DTO is not checked against these supported
   * aggregates, to be open for new ones <br/>
   * 
   * eg. <br/> Salary mySalary = null; Vector vecAggregate = new Vector();
   * 
   * mySalary = new Salary(); mySalary.setColumnValue( "AMOUNT", "SUM" );
   * vecAggregate.addElement( mySalary );
   * 
   * mySalary = new Salary(); mySalary.setColumnValue( "AMOUNT", "MIN" );
   * vecAggregate.addElement( mySalary );
   * 
   * mySalary = new Salary(); mySalary.setColumnValue( "AMOUNT", "MAX" );
   * vecAggregate.addElement( mySalary ); ... Collection retVal =
   * mySalaryDAO.getAggregate( vecAggregate, (DTO_Interface)null ); ...
   * 
   * This produces a query like:
   * 
   * select sum(salary.amount), min(salary.amount), max(salary.amount) ...
   * 
   * The returned Collection contains the aggregate values in the same order
   * they were specified for retrieval.
   * 
   * @param parCollAggrs
   *          The Collection that contains the objects with the aggregates Each
   *          object inside the column can only contain one aggregate.
   * 
   * @param cp_strQuery
   *          Optional WHERE clause, must include "WHERE" statement Make sure
   *          that you also include any tables that are needed for your query.
   * 
   * @return Collection containing objects that represent the values
   * 
   * @throws DatabaseTroubleException
   * @throws Exception
   */
  public Collection getAggregate(Collection parCollAggrs, String cp_strQuery)
      throws DatabaseTroubleException, Exception {
    Vector ct_vecReturnVal = new Vector();

    try {
      if (cp_strQuery == null)
        cp_strQuery = new String();
      // Read all entries that match to the WHERE clause
      String query = "";
      String aggQuery = "";
      String column = "";

      Iterator itAggrs = parCollAggrs.iterator();
      DTO_Interface ct_loop;
      int numAggs = 0;

      while (itAggrs.hasNext()) {
        ct_loop = (DTO_Interface) itAggrs.next();
        column = this.getFirstProperty(ct_loop);
        aggQuery += ct_loop.getColumnValue(column) + "(" + column + ")";
        if (itAggrs.hasNext()) {
          aggQuery += ", ";
        }
        // number of specified aggregates
        numAggs++;
      }

      query = "SELECT " + aggQuery + " FROM " + " " + cp_strQuery;

      getLogger().debug("getAggregate: " + query);

      Statement sbStatement = getConnection().createStatement();
      ResultSet ct_rstRes = sbStatement.executeQuery(query);

      ct_rstRes.next();
      for (int i = 1; i <= numAggs; i++) {
        ct_vecReturnVal.addElement(ct_rstRes.getObject(i));
      }

      ct_rstRes.close();
      sbStatement.close();
    } // try
    catch (SQLException ex) {
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
    }
    return ct_vecReturnVal;
  } // getAggregate

  /**
   * Return the number of objects found. <br/>
   * 
   * Only functions, if the used object has a column/attribute named "ID" <br/>
   * 
   * Sends a query like "select count( distinct TABLE.id ) from ... TABLE" <br/>
   * 
   * @param parQBEobj
   * @param parSORTobjs
   * @return number of objects found
   * @throws ObjectNotFoundException
   * @throws DatabaseTroubleException
   * @throws IllegalArgumentException
   * @throws Exception
   */
  public int getCountObject(DTO_Interface parQBEobj, Vector parSORTobjs)
      throws ObjectNotFoundException, DatabaseTroubleException,
      IllegalArgumentException, Exception {

    String query = "";

    // Maybe the user forgot...
    if (parQBEobj == null)
      throw new IllegalArgumentException(
          "GenericDAO, getCountObject-> DTO_Interface: Object not set...");

    query = getDBprefix() + parQBEobj.getTableName()
        + resolveSimpleSearchAndSorting(parQBEobj, parSORTobjs);

    // System. out.println(parQBEobj.getTableName());
    // System. out.println(query);

    return getCountObject(parQBEobj, query);
  } // getCountObject

  // ########## getCountObject ##########

  /**
   * Get objects out of the database using an optional SQL WHERE clause <br/>
   * <br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @param cp_strQuery
   *          String, added to the select statement
   * @return number of objects
   */
  public int getCountObject(String cp_strQuery) throws ObjectNotFoundException,
      DatabaseTroubleException {
    int it_ReturnVal = 0;

    Statement sbStatement = null;
    ResultSet ct_rstRes = null;
    try {
      getLogger().debug("getCountObject: " + cp_strQuery);

      sbStatement = getConnection().createStatement();
      ct_rstRes = sbStatement.executeQuery(cp_strQuery);

      ct_rstRes.next();
      it_ReturnVal = ((java.lang.Number) ct_rstRes.getObject(1)).intValue();

    } // try
    catch (SQLException ex) {
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
      getLogger().error("getCountObject (String): Exception");
    } finally {
      try {
        if (ct_rstRes != null) {
          ct_rstRes.close();
        }
        if (sbStatement != null) {
          sbStatement.close();
        }
      } catch (SQLException ex) {
        getLogger().error(
            "getCountObject (String): Error closing resultset/statement");
        throw new DatabaseTroubleException(ex.getMessage());
      }
    }

    return it_ReturnVal;
  } // getCountObject

  /**
   * Get objects out of the database using an optional SQL WHERE clause <br/>
   * <br/>
   * 
   * @param cp_strQuery
   *          String, added to the select statement
   * @param parObj
   *          objects of this kind
   * @throws ObjectNotFoundException
   * @throws DatabaseTroubleException
   * @throws Exception
   * @return number of found objects
   */
  public int getCountObject(DTO_Interface parObj, String cp_strQuery)
      throws ObjectNotFoundException, DatabaseTroubleException {
    int it_ReturnVal = 0;

    Statement sbStatement = null;

    ResultSet ct_rstRes = null;
    try {
      if (cp_strQuery == null)
        cp_strQuery = new String();
      // Read all entries that match to the WHERE clause
      String query = "";

      query = "SELECT COUNT(*) FROM " + cp_strQuery;
      // System. out.println(query);
      getLogger().debug("getCountObject: " + query);

      //PreparedStatement ppStatement = getConnection().prepareStatement(query);

      sbStatement = getConnection().createStatement();
      ct_rstRes = sbStatement.executeQuery(query);

      ct_rstRes.next();
      it_ReturnVal = ((java.lang.Number) ct_rstRes.getObject(1)).intValue();

    } // try
    catch (SQLException ex) {
      // ex.printStackTrace();
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
      // ex.printStackTrace();
      getLogger().error("getCountObject (DTO, String): Exception");
    } finally {
      try {
        if (ct_rstRes != null) {
          ct_rstRes.close();
        }
        if (sbStatement != null) {
          sbStatement.close();
        }
      } catch (SQLException ex) {
        getLogger().error("getCountObject: Error closing resultset/statement");
        throw new DatabaseTroubleException(ex.getMessage());
      }
    }
    return it_ReturnVal;
  } // getCountObject

  /**
   * Get objects out of the database using an optional SQL WHERE clause <br/>
   * <br/>
   * 
   * @param cp_strQuery
   *          String, added to the select statement
   * @param parObj
   *          objects of this kind
   * @throws ObjectNotFoundException
   * @throws DatabaseTroubleException
   * @throws Exception
   * @return number of found objects
   */
  public int getCountObjectSpeed(String parTable, String parKEY1,
      String parVAL1, String parKEY2, String parVAL2)
      throws ObjectNotFoundException, DatabaseTroubleException {
    int it_ReturnVal = 0;

    Statement sbStatement = null;

    ResultSet ct_rstRes = null;
    try {
      // Read all entries that match to the WHERE clause
      String query = "";

      query = "SELECT COUNT(*) FROM " + getDBprefix()+parTable + " WHERE " + parKEY1 + " = "
          + parVAL1 + " AND " + parKEY2 + " = " + parVAL2;
      // System. out.println(query);
      getLogger().debug("getCountObject: " + query);

      //PreparedStatement ppStatement = getConnection().prepareStatement(query);

      sbStatement = getConnection().createStatement();
      ct_rstRes = sbStatement.executeQuery(query);

      ct_rstRes.next();
      it_ReturnVal = ((java.lang.Number) ct_rstRes.getObject(1)).intValue();

    } // try
    catch (SQLException ex) {
      // ex.printStackTrace();
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
      // ex.printStackTrace();
      getLogger().error("getCountObject (DTO, String): Exception");
    } finally {
      try {
        if (ct_rstRes != null) {
          ct_rstRes.close();
        }
        if (sbStatement != null) {
          sbStatement.close();
        }
      } catch (SQLException ex) {
        getLogger().error("getCountObject: Error closing resultset/statement");
        throw new DatabaseTroubleException(ex.getMessage());
      }
    }
    return it_ReturnVal;
  } // getCountObjectSpeed

  /**
   * Retrieves all objects that match the specific 'DBT_SearchConfig' entries
   * <br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @throws Exception
   *           Runtime error occurred, may OutOfMemory, etc.
   * 
   * @param parQBEobj
   *          retrieve objects of this kind; set attributes are ignored
   * @param parSearchConfig
   *          search configuration
   * 
   * @return Vector contains all found objects <br/>
   */
  public Vector getAllObjects(DTO_Interface parQBEobj,
      DBT_SearchConfig parSearchConfig) throws ObjectNotFoundException,
      DatabaseTroubleException, Exception {

    return getObjects(parQBEobj, parSearchConfig, null, 1, Integer.MAX_VALUE);

  } // getAll..

  /**
   * Retrieves all objects that match the specific 'DBT_SearchConfig' entries
   * and sorts them using the cp_vecSortObjs entries <br/><br/>
   * 
   * @param parQBEobj
   *          retrieve objects of this kind; set attributes are ignored
   * @param parSearchConfig
   *          search configuration
   * @param cp_vecSortObjs
   *          sorting criteria
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @return Vector contains all found objects
   */
  public Vector getAllObjects(DTO_Interface parQBEobj,
      DBT_SearchConfig parSearchConfig, Vector cp_vecSortObjs)
      throws ObjectNotFoundException, DatabaseTroubleException, Exception {

    throw new Exception("Method not implemented yet");

  } // getAll..

  /**
   * Returns all objects that match a given QueryByExample object <br/><br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @throws Exception
   *           something else went wrong
   * @return Vector contains all found objects
   * @param parQBEobj
   *          find objects that match the attributes that were set
   */
  public Vector getAllObjects(DTO_Interface parQBEobj)
      throws ObjectNotFoundException, DatabaseTroubleException, Exception {

    return getAllObjects(parQBEobj, (Vector) null);
  } // getAll..

  /**
   * Returns specific number of objects that match a given QueryByExample object
   * <br/><br/>Attention:! <br/>Reading starts with number 1, not 0! <br/><br/>
   * 
   * @see #getCountObject
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @param ipStart
   *          read beginning from this entry, 1 means first entry
   * @param ipEnd
   *          read including this entry
   * @throws Exception
   *           something else went wrong
   * @return Vector contains all found objects
   * @param parQBEobj
   *          find objects that match the attributes that were set
   */
  public Vector getObjects(DTO_Interface parQBEobj, int ipStart, int ipEnd)
      throws ObjectNotFoundException, DatabaseTroubleException, Exception {

    return getObjects(parQBEobj, ipStart, ipEnd, (Vector) null);
  } // get..

  /**
   * Get all objects out of the database <br/><br/>Use 'parQBEobj' to specifiy
   * a Query By Example object <br/>Fill the Vector with instances of objects to
   * specify the sorting, if desired <br/>Use an object inside the Vector to
   * specify sorting criterias. Set an attribute to 'not null' (eg 'new
   * String()' for a character type) to tell the method that it should sort the
   * result by this criteria. <br/><br/>
   * 
   * @param parQBEobj
   *          set attributes to configure the query
   * @param parSORTobjs
   *          set attributes of the objects to <em>any</em> value to specify
   *          the sorting criteria
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws IllegalArgumentException
   *           DTO_Interface not specified
   * @throws DatabaseTroubleException
   *           problems with the database
   * @return Vector contains all found objects
   */
  public Vector getAllObjects(DTO_Interface parQBEobj, Vector parSORTobjs)
      throws ObjectNotFoundException, DatabaseTroubleException,
      IllegalArgumentException {

    String query = "";

    // Maybe the user forgot...
    if (parQBEobj == null)
      throw new IllegalArgumentException(
          "getAllObjects, parQBEobj - param not specified");

    query = getDBprefix() + parQBEobj.getTableName()
        + resolveSimpleSearchAndSorting(parQBEobj, parSORTobjs);

    // System. out.println(query);

    return getAllObjects(parQBEobj, query);
  } // getAll...

  // ########## getAll... ##########

  /**
   * Retrieve specific number of objects out of the database <br/><br/><br/>
   * Attention:! <br/>Reading starts with number 1, not 0!<br/><br/>Use
   * 'parQBEobj' to specifiy a Query By Example object <br/>Fill the Vector with
   * instances of objects to specify the sorting, if desired <br/>Use an object
   * inside the Vector to specify sorting criterias. Set an attribute to 'not
   * null' (eg 'new String()' for a character type) to tell the method that it
   * should sort the result by this criteria. <br/><br/>
   * 
   * @param parQBEobj
   *          set attributes to configure the query
   * @param parSORTobjs
   *          set attributes of the objects to <em>any</em> value to specify
   *          the sorting criteria
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws IllegalArgumentException
   *           DTO_Interface not specified
   * @param ipStart
   *          read beginning from this entry, 1 means first entry
   * @param ipEnd
   *          read including this entry
   * @throws DatabaseTroubleException
   *           problems with the database
   * @return Vector contains all found objects
   */
  public Vector getObjects(DTO_Interface parQBEobj, int ipStart, int ipEnd,
      Vector parSORTobjs) throws ObjectNotFoundException,
      DatabaseTroubleException, IllegalArgumentException {

    String query = "";

    // Maybe the user forgot...
    if (parQBEobj == null)
      throw new IllegalArgumentException(
          "getObject: parQBEobj - param not specified");

    query = getDBprefix() + parQBEobj.getTableName()
        + resolveSimpleSearchAndSorting(parQBEobj, parSORTobjs);

    return getObjects(parQBEobj, query, ipStart, ipEnd);
  } // getAll...

  // ########## getAll... ##########

  /**
   * Get objects out of the database using an optional SQL WHERE clause <br/>
   * <br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @return Vector contains all found objects
   * 
   * @param parQBEobj
   *          retrieve objects of this kind
   * @param cp_strQuery
   *          String, added to the select statement
   */
  public Vector getAllObjects(DTO_Interface parQBEobj, String cp_strQuery)
      throws ObjectNotFoundException, DatabaseTroubleException {
    Vector ct_vecReturnVal = new Vector();

    Statement sbStatement = null;
    ResultSet ct_rstRes = null;
    try {
      if (cp_strQuery == null)
        cp_strQuery = new String();
      // Read all entries that match to the WHERE clause
      String query = "SELECT DISTINCT " + getDBprefix()
          + parQBEobj.getTableName() + ".* FROM " + cp_strQuery;
      getLogger().debug("getAllObjects: " + query);

      // create statement, execute query
      // (thats what i call a "good" comment)
      sbStatement = getConnection().createStatement();
      ct_rstRes = sbStatement.executeQuery(query);
      /*
       * // determine the column's names int itColCount =
       * ct_rstRes.getMetaData().getColumnCount(); String[] cta_strColNames =
       * new String[itColCount];
       * 
       * for (int i = 1; i <= itColCount; i++) { cta_strColNames[i - 1] =
       * ct_rstRes.getMetaData().getColumnName(i); }
       */
      Iterator iteCols = parQBEobj.getColumns();
      int itColCount = parQBEobj.getColumnsSize();
      String[] cta_strColNames = new String[itColCount];
      int arrPtr = 0;
      while (iteCols.hasNext()) {
        cta_strColNames[arrPtr++] = (String) iteCols.next();
      }

      while (ct_rstRes.next()) {
        // create the object
        Generic_DTO ct_dtoNew = (Generic_DTO) parQBEobj.createObject();
        // Generic_DTO ct_dtoNew = (Generic_DTO) parQBEobj.getClass()
        // .newInstance();
        //
        // ct_dtoNew.setTableName(parQBEobj.getTableName());
        // ct_dtoNew.setColumns(parQBEobj.getColumns());

        // transfer data to the created DTO
        for (int i = 1; i <= itColCount; i++) {
          ct_dtoNew.setColumnValue(cta_strColNames[i - 1], ct_rstRes
              .getObject(cta_strColNames[i - 1]));
        }
        ct_vecReturnVal.addElement(ct_dtoNew);
      }
    } // try
    catch (SQLException ex) {
      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.mylogger.debug(mySW.getBuffer().toString());
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      // this.getLogger().error("Standard Exception" );
      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.mylogger.debug(mySW.getBuffer().toString());
      this.printStackTrace(ex);
    } finally {
      try {
        ct_rstRes.close();
      } catch (Exception ex) {
        // ignore
      }
      try {
        sbStatement.close();
      } catch (Exception ex) {
        // ignore
      }
    }
    if (ct_vecReturnVal.size() == 0) {
      throw new ObjectNotFoundException("getAllObjects");
    }
    return ct_vecReturnVal;
  } // getAllObjects

  // ########## getAllObjects ##########

  /**
   * Get a specific number of objects out of the database using an optional SQL
   * WHERE clause <br/>The Query-String can refer to as many tables as the user
   * of this class likes. This method can be used to implement a very
   * sophisticated search.
   * 
   * <br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @return Vector contains all found objects
   * 
   * @param ipStart
   *          read beginning from this entry; must be >= 1
   * @param ipEnd
   *          read including this entry
   * @param parQBEobj
   *          retrieve objects of this kind
   * @param cp_strQuery
   *          String, added to the select statement contains "<TABLE_NAME>
   *          WHERE <blah blah>" May also contain more than one <table_name>.
   *          Eg: getObjects( <obj_instance>, "<table_name>where <table_name>.<col>=
   *          1", 1, 20 );
   */
  public Vector getObjects(DTO_Interface parQBEobj, String cp_strQuery,
      int ipStart, int ipEnd) throws ObjectNotFoundException,
      DatabaseTroubleException {
    Vector ct_vecReturnVal = new Vector();

    try {
      if (cp_strQuery == null)
        cp_strQuery = new String();
      // Read all entries that match to the WHERE clause
      String query = "SELECT DISTINCT " + getDBprefix()
          + parQBEobj.getTableName() + ".* FROM " + cp_strQuery;

      // .out.println(query);
      // .out.println("ipStart: "+ ipStart );
      // .out.println("ipEnd: "+ ipEnd );

      getLogger().debug("getAllObjects: " + query);

      Statement sbStatement = getConnection().createStatement(
          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      ResultSet ct_rstRes = sbStatement.executeQuery(query);
      // determine the column's names
      int itColCount = ct_rstRes.getMetaData().getColumnCount();
      String[] cta_strColNames = new String[itColCount];

      for (int i = 1; i <= itColCount; i++) {
        cta_strColNames[i - 1] = ct_rstRes.getMetaData().getColumnName(i);
        // .out.println(cta_strColNames[i - 1]);
      }
      // move the cursor to the specific row
      if (ipStart != 1) {
        // .out.println("ipStart != 1");
        ct_rstRes.absolute(ipStart - 1);
      }
      while (ct_rstRes.next() && (ipStart <= ipEnd)) {
        // create the object
        Generic_DTO ct_dtoNew = (Generic_DTO) parQBEobj.createObject();
        // Generic_DTO ct_dtoNew = (Generic_DTO) parQBEobj.getClass()
        // .newInstance();
        // ct_dtoNew.setTableName(parQBEobj.getTableName());
        // ct_dtoNew.setColumns(parQBEobj.getColumns());

        for (int i = 1; i <= itColCount; i++) {
          ct_dtoNew.setColumnValue(cta_strColNames[i - 1], ct_rstRes
              .getObject(cta_strColNames[i - 1]));
        }
        ct_vecReturnVal.addElement(ct_dtoNew);
        // increase counter
        ipStart++;
      }
      ct_rstRes.close();
      sbStatement.close();
    } // try
    catch (SQLException ex) {
      ex.printStackTrace();
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
    }
    if (ct_vecReturnVal.size() == 0) {
      throw new ObjectNotFoundException("getObjects");
    }
    return ct_vecReturnVal;
  } // getObjects

  /**
   * This method is not implemented <br/>
   * 
   * Raises an Exception, when called <br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @throws Exception
   *           something else went wrong
   * @return Vector not used
   * 
   * @param cp_strQuery
   *          not used
   * @param ipStart
   *          not used
   * @param ipEnd
   *          not used
   */
  public Vector getObjects(String cp_strQuery, int ipStart, int ipEnd)
      throws ObjectNotFoundException, DatabaseTroubleException, Exception {

    throw new Exception("Generic_DAO; getObjects(String, int, int): "
        + "Method not implemented");

  } // getObjects

  /**
   * Read objects with restricted attributes <br/>
   * 
   * Sometimes it is not necessary to return all attributes (columns). This
   * method allows to restrict the columns that should be retrieved from the
   * database <br/>LWO -> Light Weight Object
   * 
   * @param parAttributes
   *          Set the attributes that should be retrieved The value specified
   *          for the attribute is ignored. Must not be null.
   * @param parQBEobj
   *          Specify the WHERE clause with this object Can be null.
   * @param ipStart
   *          start reading from this entry (start >= 1)
   * @param ipEnd
   *          end reading with this value
   * @param parSORTobjs
   *          order by this objects. ASC, DESC Remember to set only one
   *          attribute inside a DTO. Can be null.
   * 
   * @return Vector containing the found objects. Only a subset of attributes is
   *         set.
   * 
   * @throws ObjectNotFoundException
   *           Nothing found
   * @throws DatabaseTroubleException
   *           Database could not be accessed
   * @throws IllegalArgumentException
   *           An argument the user specified is not valid.
   * 
   * eg. --- Person { NAME, VORNAME, STRASSE, VILLAGE } --- // Only read
   * attribute NAME and VORNAME Person myPerson = new Person; myPerson.setNAME(
   * "something" ); myPerson.setVORNAME( "something" ); ... Vector result =
   * myPerson_DAO.getObjects_LWO( myPerson, null, 1, Integer.MAX_VALUE, null );
   * 
   */
  public Vector getObjects_LWO(DTO_Interface parAttributes,
      DTO_Interface parQBEobj, int ipStart, int ipEnd, Vector parSORTobjs)
      throws ObjectNotFoundException, DatabaseTroubleException,
      IllegalArgumentException {

    String query = "";

    // Maybe the user forgot...
    if (parAttributes == null)
      throw new IllegalArgumentException(
          "getObjects_LWO: parAttributes - param not specified");

    if (parQBEobj != null) {
      query = getDBprefix() + parQBEobj.getTableName()
          + this.resolveSimpleSearchAndSorting(parQBEobj, parSORTobjs);
    } else {
      query = getDBprefix() + parAttributes.getTableName();
    }

    return getObjects_LWO(parAttributes, query, ipStart, ipEnd);
  } // getObjects_LWO

  public Vector getObjects_LWO(DTO_Interface parQBEobj, String cp_strQuery,
      int ipStart, int ipEnd) throws ObjectNotFoundException,
      DatabaseTroubleException {
    Vector ct_vecReturnVal = new Vector();

    String attributes = "";
    // collect all attributes, that should be read from the EIS
    Properties ct_propProps = parQBEobj.getObjectProperties();
    String ct_strPropName = "";

    for (Enumeration e = ct_propProps.propertyNames(); e.hasMoreElements();) {
      ct_strPropName = (String) e.nextElement();
      attributes += getDBprefix() + parQBEobj.getTableName() + "."
          + ct_strPropName;
      if (e.hasMoreElements()) {
        attributes += ", ";
      }
    } // ATTRIBUTES object

    try {
      if (cp_strQuery == null)
        cp_strQuery = new String();
      // Read all entries that match to the WHERE clause
      String query = "SELECT DISTINCT " + attributes + " FROM " + cp_strQuery;

      getLogger().debug("getObjects_LWO: " + query);

      Statement sbStatement = getConnection().createStatement(
          ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      ResultSet ct_rstRes = sbStatement.executeQuery(query);
      // determine the column's names
      int itColCount = ct_rstRes.getMetaData().getColumnCount();
      String[] cta_strColNames = new String[itColCount];

      for (int i = 1; i <= itColCount; i++) {
        cta_strColNames[i - 1] = ct_rstRes.getMetaData().getColumnName(i);
      }
      // move the cursor to the specific row
      if (ipStart != 1)
        ct_rstRes.absolute(ipStart - 1);
      while (ct_rstRes.next() && (ipStart <= ipEnd)) {
        // create the object
        // Generic_DTO ct_dtoNew = (Generic_DTO) parQBEobj.getClass()
        // .newInstance();
        // ct_dtoNew.setTableName(parQBEobj.getTableName());
        // ct_dtoNew.setColumns(parQBEobj.getColumns());
        Generic_DTO ct_dtoNew = (Generic_DTO) parQBEobj.createObject();

        for (int i = 1; i <= itColCount; i++) {
          ct_dtoNew.setColumnValue(cta_strColNames[i - 1], ct_rstRes
              .getObject(cta_strColNames[i - 1]));
        }
        ct_vecReturnVal.addElement(ct_dtoNew);
        // increase counter
        ipStart++;
      }
      ct_rstRes.close();
      sbStatement.close();
    } // try
    catch (SQLException ex) {
      printSQLException(ex);
      throw new DatabaseTroubleException(ex.getMessage());
    } catch (java.lang.Exception ex) {
      this.printStackTrace(ex);
    }
    if (ct_vecReturnVal.size() == 0)
      throw new ObjectNotFoundException("getObjects_LWO");
    return ct_vecReturnVal;
  } // getObjects_LWO

  /**
   * Returns the first property of an object that is NOT NULL <br/> <br/> This
   * method questions an object about its set properties and returns the first
   * one it finds. <br/> <br/>
   * 
   * @param parObj
   *          object to be scanned
   * @return String first property value found
   */
  private String getFirstProperty(DTO_Interface parObj) {
    Properties ct_propProps = parObj.getObjectProperties();
    String ct_strPropName = "";

    for (Enumeration e = ct_propProps.propertyNames(); e.hasMoreElements();) {
      ct_strPropName = (String) e.nextElement();
      break;
    } // QueryByExample object
    return ct_strPropName;
  } // ########## getFirstProperty ##########

  /**
   * Create SQL query String according to the given query parameter <br/><br/>
   * Accepts a String containing '*' (asterisk) which means zero or more
   * characters (SQL '%'), or '=/' which means "is null", '!=/' which means "is
   * not null" <br/>Eg for the parameter "some*thing" the method will return "
   * LIKE 'some*thing'" <br/>-> the method will add the ' and the "LIKE" <br/>
   * 
   * @see #resolveDate
   * @see #resolveNumber
   * 
   * @param cp_strQuery
   * @param cp_strColName
   *          name of the database column
   * @param cp_strTablename
   *          name of the database table
   * @param cp_strComparator
   *          something like "=", "<", ">", etc.
   * 
   * @return SQL query String (can be used inside a WHERE clause), can be empty
   */
  public String resolveText(String cp_strTablename, String cp_strColName,
      String cp_strQuery, String cp_strComparator) {

    // resolve it?
    if (this.isResolveSearch() == false) {
      return " " + cp_strComparator + " " + cp_strQuery + " ";
    }

    if (cp_strComparator.equals("=")) {
      return " " + cp_strComparator + " " + "'" + cp_strQuery + "'";
    }
    if (cp_strComparator.equals(" = ")) {
      return cp_strComparator + "'" + cp_strQuery + "'";
    }
    // syntax: if the query starts with "\query\" -> use
    // prefix without changes
    if (cp_strQuery.startsWith("\\query\\")) {
      return cp_strQuery.substring("\\query\\".length());
    }

    //this.getLogger().debug(">> DB_Generic_DAO: resolveText - kein =");
    //this.getLogger().debug(
    //    ">> DB_Generic_DAO: resolveText - cp_strComparator '"
    //        + cp_strComparator + "'");

    // 03/feb/2004 ->add "in..." statement
    if (cp_strQuery.toLowerCase().startsWith("in(")) {
      return " " + cp_strQuery;
    }

    String ct_strRetValue = "";

    ct_strRetValue = cp_strQuery.replace('*', '%');
    // ct_strRetValue = "'%" +ct_strRetValue+ "%'";
    ct_strRetValue = "'" + convertToSQLFormat(ct_strRetValue) + "'";

    if (cp_strQuery.equals("=/")) {
      return " is null ";
    }
    if (cp_strQuery.equals("!=/")) {
      return " is not null ";
    }
    return " " + cp_strComparator + " " + ct_strRetValue + " ";
  } // ########## resolveText ##########

  public String resolveText(String cp_strTablename, String cp_strColName,
      String cp_strQuery) {
    return this
        .resolveText(cp_strTablename, cp_strColName, cp_strQuery, "LIKE");
  }

  /**
   * Create SQL query String according to the given query parameter <br/><br/>
   * Accepts a String containing '<co><number>', whereas '<co>' is a
   * comparison operator like '<=,<,>=,>' and '<number>' a valid number
   * <br/>Can also be '=/' which means "is null", or '!=/' which means "is not
   * null" <br/>One can also use the keywords "min" or "max" (standalone), to
   * retrieve the data with the min/max value for this column <br/>
   * 
   * @see #resolveText
   * @see #resolveDate
   * 
   * @throws IllegalArgumentException
   *           the given query string is not valid
   * @param cp_strQuery
   * @param cp_strColName
   *          name of the database column
   * @param cp_strTablename
   *          name of the database table
   * @param cp_strComparator
   *          something like "=", "<", ">", etc.
   * @return SQL query String (can be used inside a WHERE clause), can be empty
   */
  // public String resolveNumber( String cp_strQuery )
  public String resolveNumber(String cp_strTablename, String cp_strColName,
      String cp_strQuery, String cp_strComparator)
      throws IllegalArgumentException {

    // resolve it?
    if (this.isResolveSearch() == false) {
      return " " + cp_strComparator + " " + cp_strQuery + " ";
    }

    String ct_strRetValue = "";

    // syntax: if the query starts with "\query\" -> use
    // prefix without changes
    if (cp_strQuery.startsWith("\\query\\")) {
      return cp_strQuery.substring("\\query\\".length());
    }

    // <co> <number>
    if (cp_strQuery.startsWith("<=") || cp_strQuery.startsWith("<")
        || cp_strQuery.startsWith(">=") || cp_strQuery.startsWith(">")) {
      return cp_strQuery;
    }

    // 03/feb/2004 ->add "in..." statement
    if (cp_strQuery.toLowerCase().startsWith("in(")) {
      return " " + cp_strQuery;
    }

    if (cp_strQuery.equals("=/")) {
      return " is null ";
    }
    if (cp_strQuery.equals("!=/")) {
      return " is not null ";
    }
    if (cp_strQuery.equalsIgnoreCase("min")) {
      return " in (select min(" + cp_strColName + ") from " + cp_strTablename
          + ") ";
    }
    if (cp_strQuery.equalsIgnoreCase("max")) {
      return " in (select max(" + cp_strColName + ") from " + cp_strTablename
          + ") ";
    }

    // last chance -> is number
    try {
      Double ct_douNum = new Double(cp_strQuery);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(
          "resolveNumber: not a valid query string");
    }
    return " " + cp_strComparator + " " + cp_strQuery + " ";
  } // ########## resolveNumber ##########

  /**
   * Create SQL query String according to the given query parameter <br/><br/>
   * Accepts a String containing '<co><number>', whereas '<co>' is a
   * comparison operator like '<=,<,>=,>' and '<number>' a valid number
   * <br/>Can also be '=/' which means "is null", or '!=/' which means "is not
   * null" <br/>One can also use the keywords "min" or "max" (standalone), to
   * retrieve the data with the min/max value for this column <br/>
   * 
   * @see #resolveText
   * @see #resolveDate
   * 
   * @throws IllegalArgumentException
   *           the given query string is not valid
   * @param cp_strQuery
   * @param cp_strColName
   *          name of the database column
   * @param cp_strTablename
   *          name of the database table
   * @return SQL query String (can be used inside a WHERE clause), can be empty
   */
  public String resolveNumber(String cp_strTablename, String cp_strColName,
      String cp_strQuery) throws IllegalArgumentException {
    return this.resolveNumber(cp_strTablename, cp_strColName, cp_strQuery, "=");
  } // ########## resolveNumber ##########

  public String resolveDateValue(String cp_strColumnValue)
      throws IllegalArgumentException, Exception {

    String ct_strRetValue = "";

    String dateFormats[] = this.getDateFormats();

    // date relative
    if (cp_strColumnValue.startsWith("[Now-")) {

      int braceFound = cp_strColumnValue.indexOf("]");
      // getLogger().debug("found: braceFound: "+ braceFound);
      String number = cp_strColumnValue.substring(5, braceFound);
      // getLogger().debug("Number: "+ number);
      int days = -1;

      try {
        days = Integer.valueOf(number).intValue();
        days = days * -1;
        // getLogger().debug("Days: "+ days);
      } catch (NumberFormatException ex) {
        // do nothing
        throw new IllegalArgumentException("Invalid date format");
      }
      // create a valid Date
      Calendar myCal = Calendar.getInstance();

      myCal.add(Calendar.DATE, days);

      SimpleDateFormat sqlFormatter = new SimpleDateFormat(
          dateFormats[sqldateformat]);

      ct_strRetValue = sqlFormatter.format(myCal.getTime());

      return ct_strRetValue;
    }
    // <co> <number>
    if (cp_strColumnValue.startsWith("<=")
        || cp_strColumnValue.startsWith(">=")) {
      return cp_strColumnValue.substring(0, 2)
          + " "
          + generateSQLDateString(dateFormats[sqltimestampformatquery],
              cp_strColumnValue.substring(2));
    }
    if (cp_strColumnValue.startsWith("<") || cp_strColumnValue.startsWith(">")) {
      return cp_strColumnValue.substring(0, 1)
          + " "
          + generateSQLDateString(dateFormats[sqltimestampformatquery],
              cp_strColumnValue.substring(1));
    }
    // is null, is not null
    if (cp_strColumnValue.equals("=/")) {
      return " is null";
    }
    if (cp_strColumnValue.equals("!=/")) {
      return " is not null";
    }
    // contains "~"?
    if (cp_strColumnValue.indexOf('~') != -1) {
      int itFound = cp_strColumnValue.indexOf('~');
      String dateLeft = cp_strColumnValue.substring(0, itFound);
      String dateRight = cp_strColumnValue.substring(itFound + 1);
      // test, if dates are valid
      SimpleDateFormat formatter = new SimpleDateFormat(
          dateFormats[guidateformat]);
      // Parse the string back into a Date.
      ParsePosition pos = new ParsePosition(0);
      Date fromDate = formatter.parse(dateLeft, pos);

      if (fromDate == null) {
        throw new IllegalArgumentException(
            "resolveDate: 'from date', not a valid query string: "
                + cp_strColumnValue);
      }
      pos = new ParsePosition(0);
      Date toDate = formatter.parse(dateRight, pos);

      if (toDate == null) {
        throw new IllegalArgumentException(
            "resolveDate: 'to date', not a valid query string: "
                + cp_strColumnValue);
      }
      // convert DATE to SQL format
      SimpleDateFormat sqlFormatter = new SimpleDateFormat(
          dateFormats[sqldateformat]);
      String sqlFromDate = // sqlFormatter.format(fromDate);
      generateSQLDateString(dateFormats[sqltimestampformatquery], dateLeft);

      String sqlToDate = // sqlFormatter.format(toDate);
      generateSQLDateString(dateFormats[sqltimestampformatquery], dateRight);

      return " between " + sqlFromDate + " and " + sqlToDate + "";
    }

    // maybe it is a single date
    try {
      // Format the current time.
      SimpleDateFormat formatter = new SimpleDateFormat(
          dateFormats[sqltimestampformat]);
      // Parse the previous string back into a Date.
      ParsePosition pos = new ParsePosition(0);
      Date validDate = formatter.parse(cp_strColumnValue, pos);

      if (validDate == null) {
        // try: Maybe TimeStamp format
        // "yyyy-MM-dd HH:mm:ss.S"
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        pos = new ParsePosition(0);
        validDate = formatter.parse(cp_strColumnValue, pos);

      }
      if (validDate == null) {
        formatter = new SimpleDateFormat(dateFormats[guidateformat]);
        pos = new ParsePosition(0);
        validDate = formatter.parse(cp_strColumnValue, pos);

        if (validDate == null) {
          throw new IllegalArgumentException(
              "resolveDate: DATE null; not a valid query string: "
                  + cp_strColumnValue);
        }
      }
      SimpleDateFormat sqlFormatter = new SimpleDateFormat(
          dateFormats[sqldateformat]);

      ct_strRetValue = sqlFormatter.format(validDate);

      // generate the SQL string for the database
      ct_strRetValue = generateSQLDateString(
          dateFormats[sqltimestampformatquery], ct_strRetValue);

    } catch (Exception ex) {

      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.mylogger.debug(mySW.getBuffer().toString());

      throw new IllegalArgumentException(
          "resolveDate: not a valid query string: " + ex.getMessage());
    }

    return ct_strRetValue;
  } // resolveDateValue

  /**
   * Create SQL query String according to the given query parameter <br/><br/>
   * 
   * The query text can start with "<=", "<", ">=", ">" which is considered as
   * a correct SQL statement; the date given is not checked <br/>
   * 
   * Uses the date formats specified inside "dao_config.xml". Make sure that you
   * cross check "DB-Connection-Config.xml", where the database to be used is
   * specified. <br/>
   * 
   * @see #resolveText
   * @see #resolveNumber
   * 
   * @throws IllegalArgumentException
   *           the given query string is not valid
   * @param cp_strQuery
   * 
   * @param cp_strColName
   *          name of the database column
   * @param cp_strTablename
   *          name of the database table
   * @param cp_strComparator
   *          something like "=", "<", ">", etc.
   * 
   * @return SQL query String (can be used inside a WHERE clause), can be empty
   * @throws Exception
   *           something else went wrong
   */
  public String resolveDate(String cp_strTablename, String cp_strColName,
      String cp_strQuery, String cp_strComparator)
      throws IllegalArgumentException, Exception {

    // resolve it?
    // if not -> use cp_strQuery "as it is"
    // Otherwise check for "=/" (Not null), etc etc
    if (this.isResolveSearch() == false) {
      return " " + cp_strComparator + " " + cp_strQuery + " ";
    }

    String[] dateFormats = getDateFormats();

    String ct_strRetValue = "";

    SimpleDateFormat sqlFormatter = new SimpleDateFormat(
        dateFormats[sqldateformat]);

    // date relative
    if (cp_strQuery.startsWith("[Now-")) {

      int braceFound = cp_strQuery.indexOf("]");
      // getLogger().debug("found: braceFound: "+ braceFound);
      String number = cp_strQuery.substring(5, braceFound);
      // getLogger().debug("Number: "+ number);
      int days = -1;

      try {
        days = Integer.valueOf(number).intValue();
        // getLogger().debug("Days: "+ days);
      } catch (NumberFormatException ex) {
        // do nothing
        throw new IllegalArgumentException("Invalid date format");
      }
      // create a valid Date
      Calendar myCal = Calendar.getInstance();

      myCal.add(Calendar.DATE, days);

      ct_strRetValue = sqlFormatter.format(myCal.getTime());

      return ct_strRetValue;
    }
    // <co> <number>
    if (cp_strQuery.startsWith("<=") || cp_strQuery.startsWith(">=")) {
      Date validDate = this.convertStringToDate(cp_strQuery.substring(2));
      String sqlDate = sqlFormatter.format(validDate);

      return cp_strQuery.substring(0, 2)
          + " "
          + generateSQLDateString(dateFormats[sqltimestampformatquery], sqlDate);
    }
    if (cp_strQuery.startsWith("<") || cp_strQuery.startsWith(">")) {
      // this method may throw an IllegalArgumentException
      Date validDate = this.convertStringToDate(cp_strQuery.substring(1));
      String sqlDate = sqlFormatter.format(validDate);

      return cp_strQuery.substring(0, 1)
          + " "
          + generateSQLDateString(dateFormats[sqltimestampformatquery], sqlDate);
    }
    // is null, is not null
    if (cp_strQuery.equals("=/")) {
      return " is null";
    }
    if (cp_strQuery.equals("!=/")) {
      return " is not null";
    }
    // contains "~"?
    if (cp_strQuery.indexOf('~') != -1) {
      int itFound = cp_strQuery.indexOf('~');
      String dateLeft = cp_strQuery.substring(0, itFound);
      String dateRight = cp_strQuery.substring(itFound + 1);
      // test, if dates are valid
      SimpleDateFormat formatter = new SimpleDateFormat(
          dateFormats[guidateformat]);
      // Parse the string back into a Date.
      ParsePosition pos = new ParsePosition(0);
      Date fromDate = formatter.parse(dateLeft, pos);

      if (fromDate == null) {
        throw new IllegalArgumentException(
            "resolveDate: 'from date', not a valid query string: "
                + cp_strQuery);
      }
      pos = new ParsePosition(0);
      Date toDate = formatter.parse(dateRight, pos);

      if (toDate == null) {
        throw new IllegalArgumentException(
            "resolveDate: 'to date', not a valid query string: " + cp_strQuery);
      }
      // convert DATE to SQL format
      Date validDateLeft = this.convertStringToDate(dateLeft);
      String sqlDateLeft = sqlFormatter.format(validDateLeft);
      String sqlFromDate = // sqlFormatter.format(fromDate);
      generateSQLDateString(dateFormats[sqltimestampformatquery], sqlDateLeft);

      Date validDateRight = this.convertStringToDate(dateRight);
      String sqlDateRight = sqlFormatter.format(validDateRight);
      String sqlToDate = // sqlFormatter.format(toDate);
      generateSQLDateString(dateFormats[sqltimestampformatquery], sqlDateRight);

      // return " between '" + sqlFromDate + "' and '" + sqlToDate + "'";
      return " between " + sqlFromDate + " and " + sqlToDate + "";
    }

    // maybe it is a single date
    try {
      // this method may throw an IllegalArgumentException
      Date validDate = this.convertStringToDate(cp_strQuery);

      ct_strRetValue = sqlFormatter.format(validDate);

      // generate the SQL string for the database
      ct_strRetValue = generateSQLDateString(
          dateFormats[sqltimestampformatquery], ct_strRetValue);

    } catch (Exception ex) {

      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      this.mylogger.debug(mySW.getBuffer().toString());

      throw new IllegalArgumentException(
          "resolveDate: not a valid query string: " + ex.getMessage());
    }

    return " " + cp_strComparator + " " + ct_strRetValue + " ";
  } // ########## resolveDate ##########

  /**
   * Create SQL query String according to the given query parameter <br/><br/>
   * 
   * The query text can start with "<=", "<", ">=", ">" which is considered as
   * a correct SQL statement; the date given is not checked <br/>
   * 
   * @see #resolveText
   * @see DB_Generic_DAO#resolveNumber
   * 
   * @throws IllegalArgumentException
   *           the given query string is not valid
   * @param cp_strQuery
   * @param cp_strColName
   *          name of the database column
   * @param cp_strTablename
   *          name of the database table
   * 
   * @return SQL query String (can be used inside a WHERE clause), can be empty
   * @throws Exception
   *           something else went wrong
   */
  public String resolveDate(String cp_strTablename, String cp_strColName,
      String cp_strQuery) throws IllegalArgumentException, Exception {
    return this.resolveDate(cp_strTablename, cp_strColName, cp_strQuery, "=");
  }

  /**
   * Receives a database dependent format string and replaces "@" by the given
   * Date
   * 
   * @param cp_Format
   *          something like "@"
   * @param cp_Date
   *          replac "@" with this value, adds "'"
   * @return the formatted string
   */
  private String generateSQLDateString(String cp_Format, String cp_Date) {
    String result = "";

    for (int i = 0; i < cp_Format.length(); i++) {
      if (cp_Format.substring(i, i + 1).equals("@")) {
        // can we read the next character?
        // result += "'" + cp_Date + "'";
        result += cp_Date;
      } else {
        result += cp_Format.substring(i, i + 1);
      }
    }

    this.mylogger.debug("Formated sql date string: " + result);

    return result;
  } // ########## generateSQLDateString ##########

  /**
   * Returns GUI-DateFormat and SQL-DateFormat <br/><br/>The user can enter a
   * date inside the search-mask, the format of this date is specified inside
   * the "dao_config.xml" file <br/>This format is then converted to a database
   * specific format which is also specified inside the "dao_config.xml" file
   * <br/>
   * 
   * The returned format is compatible to SimpleDateFormat
   * 
   * Remark: Uses the global variable "dateFormats"
   * 
   * @return String[0] GUI DateFormat, String[1] SQL DateFormat String[2] GUI
   *         TimeStamp, String[2] SQL TimeStamp
   */
  private String[] getDateFormats() {
    if (this.dateFormats != null) {
      return this.dateFormats;
    }

    this.dateFormats = new String[5];
    this.dateFormats[guidateformat] = this.config.getGUIDateFormat();
    this.dateFormats[sqldateformat] = this.config.getSQLDateFormat();
    this.dateFormats[guitimestampformat] = this.config.getGUITimestampFormat();
    this.dateFormats[sqltimestampformat] = this.config.getSQLTimestampFormat();
    this.dateFormats[sqltimestampformatquery] = this.config
        .getSQLTimestampFormatQuery();

    return this.dateFormats;
  } // ########## getDateFormats ##########

  /**
   * Return the default database type specified inside DB-Connection-Config.xml
   * <br/><br/>Needs "DB-Connection-Config.xml", reads "dbtype" attribute
   * 
   * @throws Exception
   *           problems reading/parsing "DB-Connection-Config.xml"
   * @return database type, eg. "oracle8i", etc
   */
  private String getDatabaseType() throws Exception {
    return this.config.getDatabaseType();
  } // ########## getDatabaseType ##########

  /**
   * Creates the SQL "order by" statement for a given DTO <br/><br/>Every
   * attribute is considered a String like "ASC" or "DESC" to specify the
   * sorting <br/>If, eg. the attribute (column) "PERSON" is set to "ASC" the a
   * statement will be added "ORDER BY TABLE.PERSON ASC" <br/>
   * 
   * @throws IllegalArgumentException
   * @throws Exception
   *           something else went wrong
   * @param cp_dtoObj
   *          Object, that contains the search strings
   * @return String maybe emtpy, if the user did not specify any of the
   *         attributes, else a SQL "order by..." string
   */
  public String generateOrderBy(DTO_Interface cp_dtoObj)
      throws IllegalArgumentException, Exception {
    String ct_strRetValue = "";

    String orderBy = "";

    for (Iterator ct_iteProps = cp_dtoObj.getColumns(); ct_iteProps.hasNext();) {
      String ct_strCol = (String) ct_iteProps.next();

      if (cp_dtoObj.getColumnValue(ct_strCol) != null) {
        orderBy = cp_dtoObj.getColumnValue(ct_strCol).toString();
        if (orderBy.toUpperCase().equals("ASC")
            || orderBy.toUpperCase().equals("DESC")) {
        } else {
          throw new IllegalArgumentException("OrderBy: Column '" + ct_strCol
              + "' contains value '" + orderBy + "', must be 'ASC' or 'DESC'");
        }
        if (ct_strRetValue.length() != 0)
          ct_strRetValue += ", ";
        ct_strRetValue += getDBprefix() + cp_dtoObj.getTableName() + "."
            + ct_strCol + " " + orderBy;
      }
    }
    // if( ct_strRetValue.length() != 0 ) ct_strRetValue = " ORDER BY
    // "+ct_strRetValue;
    return ct_strRetValue;
  } // ########## generateOrderBy ##########

  /**
   * Creates the SQL "order by" statement for a given DTO <br/><br/>Every
   * attribute is considered a String like "ASC" or "DESC" to specify the
   * sorting <br/>If, eg. the attribute (column) "PERSON" is set to "ASC" the a
   * statement will be added "ORDER BY TABLE.PERSON ASC" <br/>
   * 
   * @throws IllegalArgumentException
   * @throws Exception
   *           something else went wrong
   * @param cp_vecObj
   *          Vector of objects, that contain the search strings
   * @return String maybe emtpy, if the user did not specify any of the
   *         attributes, else a SQL "order by..." string
   */
  public String generateOrderBy(Vector cp_vecObj)
      throws IllegalArgumentException, Exception {
    String ct_strRetValue = "";
    String orderBy = "";

    // entry: read from the vector
    DTO_Interface sortObj = null;

    for (Enumeration enu = cp_vecObj.elements(); enu.hasMoreElements();) {
      sortObj = (DTO_Interface) enu.nextElement();

      for (Iterator ct_iteProps = sortObj.getColumns(); ct_iteProps.hasNext();) {
        String ct_strCol = (String) ct_iteProps.next();

        if (sortObj.getColumnValue(ct_strCol) != null) {
          orderBy = sortObj.getColumnValue(ct_strCol).toString();
          if (orderBy.toUpperCase().equals("ASC")
              || orderBy.toUpperCase().equals("DESC")) { // ASC, DESC
            // specified: do
            // nothing
          } else {
            throw new IllegalArgumentException("OrderBy: Column '" + ct_strCol
                + "' contains value '" + orderBy + "', must be 'ASC' or 'DESC'");
          }
          if (ct_strRetValue.length() != 0)
            ct_strRetValue += ", ";
          ct_strRetValue += getDBprefix() + sortObj.getTableName() + "."
              + ct_strCol + " " + orderBy;
        }
      } // attributes of one object
    } // loop: for all objects (sorting ones)

    return ct_strRetValue;
  } // ########## generateOrderBy ##########

  /**
   * Convert the single quotes to SQL conform format <br/>
   * 
   * "This is Peter's dog" -> "This is Peter''s dog" "Hello ''" -> "Hello''''"
   * "3 Quotes '''" -> "3 Quotes ''''''"
   * 
   * @param cp_strInput
   * @return SQL conform conversion
   */
  public String convertToSQLFormat(String cp_strInput) {
    String result = "";

    if (cp_strInput == null)
      return null;

    for (int i = 0; i < cp_strInput.length(); i++) {
      if (cp_strInput.substring(i, i + 1).equals("'")) {
        // can we read the next character?
        result += "''";
      } else {
        result += cp_strInput.substring(i, i + 1);
      }
    }
    return result;
  } // ########## convertToSQLFormat ##########

  /**
   * Delete an object using search Configuration<br/>
   * 
   * This method can be used to send SQL queries like
   * 
   * delete * from <table> where <table>.<pkey> in ( select <table>.<pkey>
   * from <table>, <other> where <table>.fk = <other>.<pkey> and <other>.<column> =
   * 'value'; ) <br/> This would allow one to specify a query like: <br/> delete
   * all users from the database, that live in the village with the name
   * 'somewhere' Whereas "user" and "village" are linked together and "village"
   * contains a field/column "name".
   * 
   * 
   * @param parQBEobj
   *          delete entries of this type
   * @param parSearchConfig
   *          search config that describes the search for pkeys
   * 
   * @return number of deleted objects
   * 
   * @throws ObjectNotDeletedException
   *           objects could not be deleted
   * @throws Exception
   *           runtime exception, eg OutOfMemory, etc.
   */
  /*
   * 
   * 2006-09-09
   * TODO
   * enhance for more than one primary key
   public int deleteObject(DTO_Interface parQBEobj,
   DBT_SearchConfig parSearchConfig) throws ObjectNotDeletedException,
   Exception {

   Vector ct_vecIgnoreTableName = new Vector();
   ct_vecIgnoreTableName.addElement(parQBEobj.getTableName());

   String query = "";

   // [0] -> tables
   // [1] -> <WHERE> ...
   String[] whereClause = this.generateWhereClauseSearchConfig(parQBEobj,
   parSearchConfig, ct_vecIgnoreTableName);

   query = whereClause[0] + " WHERE " + whereClause[1];

   // select <table>.<pkey> from <table> where ...
   query = "SELECT " + getDBprefix() + parQBEobj.getTableName() + "."
   + parQBEobj.getPrimaryKeyColumn() + " FROM " + query;

   getLogger().debug("deleteObject, SearchCfg - Query: " + query);

   String masterQuery = "delete from " + getDBprefix()
   + parQBEobj.getTableName() + " where " + getDBprefix()
   + parQBEobj.getTableName() + "." + parQBEobj.getPrimaryKeyColumn()
   + " in (" + query + ")";

   try {
   Statement sbStatement = getConnection().createStatement();

   getLogger().debug("deleteObject: " + masterQuery);

   int itRetValue = sbStatement.executeUpdate(masterQuery);

   sbStatement.close();
   return itRetValue;
   } // try()
   catch (SQLException ex) {
   throw new ObjectNotDeletedException(ex.getMessage());
   }
   }// deleteObject
   * 
   */

  /**
   * Read objects from the database using a specific search configuration <br/>
   * 
   * @param parQBEobj
   *          Read objects of this kind
   * @param parSearchConfig
   *          Use this search configuration
   * @param cp_OrderByObj
   *          specify sorting
   * @param ipStart
   *          read beginning from this entry (starts with 1, not 0)
   * @param ipEnd
   *          read including this entry
   * 
   * @return Vector containing found elements
   * 
   * @throws ObjectNotFoundException
   *           No objects were found inside the EIS
   * @throws DatabaseTroubleException
   *           Problems accessing the EIS
   * @throws Exception
   *           Runtime error occurred, may OutOfMemory, etc.
   */
  public Vector getObjects(DTO_Interface parQBEobj,
      DBT_SearchConfig parSearchConfig, DTO_Interface cp_OrderByObj,
      int ipStart, int ipEnd) throws ObjectNotFoundException,
      DatabaseTroubleException, Exception {
    Vector retValue = new Vector();

    this.mylogger.debug("Query: getObjects");

    // generate the WHERE clause
    // all SearchCriteria objects
    int itComparator = 0;
    DTO_Interface ct_dbdObject = null;
    DTO_Interface ct_dbdObject2 = null;
    int itOperator = 0;
    DBT_SearchCriteria ct_scrSCriteria = null;

    Vector ct_vecIgnoreTableName = new Vector();

    ct_vecIgnoreTableName.addElement(parQBEobj.getTableName());

    int itFlag = 0;
    String query = "";
    boolean addOpeningBrace = false;

    for (Enumeration e = parSearchConfig.getSearchCriterias(); e
        .hasMoreElements();) {
      ct_scrSCriteria = (DBT_SearchCriteria) e.nextElement();
      if (ct_scrSCriteria.getKindOf() == 1) {
        // get search criterias
        itComparator = ct_scrSCriteria.getComparator();
        itOperator = ct_scrSCriteria.getOperator();
        ct_dbdObject = ct_scrSCriteria.getDataObject();
        ct_dbdObject2 = ct_scrSCriteria.getDataObject2();

        // create it
        if (ct_dbdObject2 != null) {
          // JOIN
          // two objects set -> implement JOIN
          if ((ct_dbdObject != null) && ((ct_dbdObject2 != null))) {
            String ct_strFirstProp = getDBprefix()
                + ct_dbdObject.getTableName() + "."
                + this.myRSC_SQL.getFirstProperty(ct_dbdObject);
            String ct_strScndProp = getDBprefix()
                + ct_dbdObject2.getTableName() + "."
                + this.myRSC_SQL.getFirstProperty(ct_dbdObject2);

            // remember the tables (duplicates are eliminated)
            if (ct_vecIgnoreTableName.contains(ct_dbdObject.getTableName()) == false) {
              ct_vecIgnoreTableName.addElement(ct_dbdObject.getTableName());
            }
            if (ct_vecIgnoreTableName.contains(ct_dbdObject2.getTableName()) == false) {
              ct_vecIgnoreTableName.addElement(ct_dbdObject2.getTableName());
            }
            // AND, OR, etc.
            if ((query.length() == 0)) {
              if (itFlag == 0) {
                if (addOpeningBrace == true) {
                  query += " ( ";
                  addOpeningBrace = false;
                } else {
                  query += " ";
                }
              } else {
                if (addOpeningBrace == true) {
                  query += " " + this.myRSC_SQL.getOperator(itOperator) + "( ";
                  addOpeningBrace = false;
                } else {
                  query += " " + this.myRSC_SQL.getOperator(itOperator);
                }
              }
            } else {
              if (addOpeningBrace == true) {
                query += " " + this.myRSC_SQL.getOperator(itOperator) + "( ";
                addOpeningBrace = false;
              } else {
                query += " " + this.myRSC_SQL.getOperator(itOperator);
              }
            }
            query += ct_strFirstProp + " "
                + this.myRSC_SQL.getComparator(itComparator) + " "
                + ct_strScndProp + " ";
            itFlag = 1;
          }
        } else {
          // static value
          String comparator = this.myRSC_SQL.getComparator(itComparator);
          String operator = this.myRSC_SQL.getOperator(itOperator);

          String[] standardWhere;

          // if( ct_dbdObject instanceof Auftrag ) {
          // standardWhere =
          // this.generateWhereClause((Auftrag) ct_dbdObject, true, comparator,
          // comparator, comparator);
          // } else {
          standardWhere = this.myRSC_SQL.resolveSearchForOneDTO(ct_dbdObject,
              itComparator);
          // }

          if ((query.length() == 0)) {
            if (itFlag == 0) {
              if (addOpeningBrace == true) {
                query += " (" + standardWhere[1];
                addOpeningBrace = false;
              } else {
                query += standardWhere[1];
              }
            } else {
              if (addOpeningBrace == true) {
                query += operator + " (" + standardWhere[1];
                addOpeningBrace = false;
              } else {
                query += operator + " " + standardWhere[1];
              }
            }
          } else {
            if (addOpeningBrace == true) {
              query += operator + " (" + standardWhere[1];
              addOpeningBrace = false;
            } else {
              query += operator + " " + standardWhere[1];
            }
          }
          if (ct_vecIgnoreTableName.contains(standardWhere[0]) == false) {
            if (standardWhere[0].length() != 0) {
              ct_vecIgnoreTableName.addElement(standardWhere[0]);
            }
          }
          if (itFlag == 0) {
            itFlag = 2;
          }
        }
      } // kindOf == 1
      else if (ct_scrSCriteria.getKindOf() == 2) {
        // kindOf == 2 // SELECT... WHERE... IN...
      } else {
        // kindOf == 3 // OPENING_BRACE, CLOSING_BRACE
        int iBrace = ct_scrSCriteria.getBrace();

        if (iBrace == DBT_SearchConfig.OPENING_BRACE) {
          addOpeningBrace = true;
        } else {
          query += ")";
        }
        itFlag = 3;
      }

    } // for... all searchCriterias
    // Create table String
    String tables = "";

    for (Enumeration e = ct_vecIgnoreTableName.elements(); e.hasMoreElements();) {
      if (tables.length() == 0) {
        tables = (String) e.nextElement();
      } else {
        tables += ", " + (String) e.nextElement();
      }
    }
    // if (itFlag == 1)
    // query = sophisticated + query;
    // else
    // query = query + sophisticated;

    query = tables + " WHERE " + query;
    if (cp_OrderByObj != null) {
      query += " ORDER BY " + this.generateOrderBy(cp_OrderByObj);
    }
    this.mylogger.debug("Query: " + query);

    return this.getObjects(query, ipStart, ipEnd);
  } // getObjects... DBT_SearchConfig

  /**
   * Returns all objects that match a given QueryByExample object <br/><br/>
   * The 'orderBy-object' contains "ASC" or "DESC" as values for the attributes.
   * They must be set using the 'setColumnValue()' <br/><br/>eg:
   * orderByObj.setColumnValue( "ID", "DESC" ); -> this will order the objects
   * descending by the ID column <br/>It is also possible to set multiple
   * sorting criterias <em>but</em> there is no guaranteed order of the
   * evaluation <br/>eg: orderByObj.setColumnValue( "ID", "DESC" );
   * orderByObj.setColumnValue( "NAME", "ASC" ); -> It is not clear, if the
   * result set will be ordered by ID descending and then by NAME ascending or
   * vice versa! <br/><br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @throws Exception
   *           something else went wrong
   * @return Vector contains all found objects
   * @param parQBEobj
   *          find objects that match the attributes that were set
   * @param parOrderBy
   *          set attributes to "ASC" or "DESC"
   * @param ipStart
   *          read beginning from this entry, must be >= 1
   * @param ipEnd
   *          read including this entry
   */
  public Vector getObjects(DTO_Interface parQBEobj, DTO_Interface parOrderBy,
      int ipStart, int ipEnd) throws ObjectNotFoundException,
      DatabaseTroubleException, Exception {
    Vector vecOrderBy = new Vector();
    vecOrderBy.addElement(parOrderBy);
    return this.getObjects(parQBEobj, ipStart, ipEnd, vecOrderBy);
  } // getObjects

  /**
   * Returns all objects that match a given QueryByExample object <br/><br/>
   * The 'orderBy-object' contains "ASC" or "DESC" as values for the attributes.
   * They must be set using the 'setColumnValue()' <br/><br/>eg:
   * orderByObj.setColumnValue( "ID", "DESC" ); -> this will order the objects
   * descending by the ID column <br/>It is also possible to set multiple
   * sorting criterias <em>but</em> there is no guaranteed order of the
   * evaluation <br/>eg: orderByObj.setColumnValue( "ID", "DESC" );
   * orderByObj.setColumnValue( "NAME", "ASC" ); -> It is not clear, if the
   * result set will be ordered by ID descending and then by NAME ascending or
   * vice versa! <br/><br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @throws Exception
   *           something else went wrong
   * @return Vector contains all found objects
   * @param parQBEobj
   *          find objects that match the attributes that were set
   * @param parOrderBy
   *          set attributes to "ASC" or "DESC"
   */
  public Vector getAllObjects(DTO_Interface parQBEobj, DTO_Interface parOrderBy)
      throws ObjectNotFoundException, DatabaseTroubleException, Exception {
    Vector vecOrderBy = new Vector();
    vecOrderBy.addElement(parOrderBy);
    return this.getAllObjects(parQBEobj, vecOrderBy);
  } // getAllObjects

  /**
   * Get objects out of the database using an optional SQL WHERE clause <br/>
   * <br/>
   * 
   * @throws ObjectNotFoundException
   *           object could not be found inside database
   * @throws DatabaseTroubleException
   *           problems with the database
   * @throws Exception
   *           something else went wrong
   * @return Vector contains all found objects
   * 
   * @param cp_strQuery
   *          String, added to the select statement
   */
  public Vector getAllObjects(String cp_strQuery)
      throws ObjectNotFoundException, DatabaseTroubleException, Exception {
    throw new Exception("Generic_DAO; getAllObjects(STRING): "
        + " Method not implemented");
  } // getAllObjects

  /**
   * Copy data from one table to another <br/>
   * 
   * @param parFrom
   *          Copy contents from this table, must be a *real* subset from
   *          "parTo". Also serves as a "Query By Example" object
   * @param parTo
   *          Insert data to this table
   * 
   * @throws SQLException
   *           Problems with the database
   */
  public void copyContents(DTO_Interface parFrom, DTO_Interface parTo)
      throws SQLException {
    this.copyContents(parFrom, parTo, null);
  }

  /**
   * Copy data from one table to another <br/>
   * 
   * @param parFrom
   *          Copy contents from this table, must be a *real* subset from
   *          "parTo". Also serves as a "Query By Example" object
   * @param parTo
   *          Insert data to this table
   * @param parAdditionalData
   *          Add this data to every row for the "TO" table can be null <br/> If
   *          set -> Additional data will be added to the left of the column eg:
   *          from: to: add: ID NAME STREET ID NAME <obj>.setSTREET( "...." );
   * 
   * @throws SQLException
   *           Problems with the database
   * 
   */
  public void copyContents(DTO_Interface parFrom, DTO_Interface parTo,
      DTO_Interface parAdditionalData) throws SQLException {
    StringBuffer query = new StringBuffer();

    // Maybe the user forgot...
    if (parFrom == null)
      throw new IllegalArgumentException(
          "copyContents, parFrom - param not specified");
    if (parTo == null)
      throw new IllegalArgumentException(
          "copyContents, parTo - param not specified");

    // insert into...
    query.append("insert into " + getDBprefix() + parTo.getTableName() + " ( ");
    //
    // attribute column_names
    //
    // get changed attributes
    StringBuffer strAddColumn = new StringBuffer();
    String strAddValue = "";
    if (parAdditionalData != null) {
      if (parAdditionalData.getChangedProperties().size() > 0) {
        HashMap ct_propProps = parAdditionalData.getChangedProperties();
        String ct_strPropName = "";
        Object ct_objPropValue = "";
        //int itCounter = 0;
        for (Iterator e = ct_propProps.keySet().iterator(); e.hasNext();) {
          ct_strPropName = (String) e.next();
          ct_objPropValue = ct_propProps.get(ct_strPropName);
          strAddColumn.append(ct_strPropName);
          strAddValue += ct_objPropValue.toString();
          if (e.hasNext()) {
            strAddColumn.append(", ");
            strAddValue += ", ";
          }
        } // for... changed attributes
        strAddColumn.append(", ");
        strAddValue += ", ";
      } // additional object attribute: values changed
    } // addition object: not null

    String toColumns = "";
    query.append(strAddColumn.toString());
    Iterator itTo = parTo.getColumns();
    while (itTo.hasNext()) {
      toColumns += (String) itTo.next();
      if (itTo.hasNext()) {
        toColumns += ", ";
      }
    }
    String fromColumns = "";
    query.append(strAddColumn.toString());
    Iterator itFrom = parFrom.getColumns();
    while (itFrom.hasNext()) {
      fromColumns += (String) itFrom.next();
      if (itFrom.hasNext()) {
        fromColumns += ", ";
      }
    }
    query.append(toColumns + ") ");
    // select...
    String selectFrom = "";
    selectFrom = "( select ";
    //
    // attribute values
    //
    selectFrom += strAddValue;
    selectFrom += fromColumns;
    selectFrom += " from " + getDBprefix() + parFrom.getTableName(); // +") ";

    query.append(selectFrom
    // + parFrom.getTableName()
        + this.resolveSimpleSearchAndSorting(parFrom, null) + ") ");

    this.getLogger().debug("query: " + query.toString());

    // connect to database and GO GO GO
    Statement sbStatement = getConnection().createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    boolean retValue = sbStatement.execute(query.toString());

    //rs.close();
    sbStatement.close();

  } // copyContents

  /**
   * Creates an SQL query string out of the object <br/><br/>The given object
   * is a 100% copy of this class, except that all types are mapped to String
   * and the value for an attribute can be a special search string, which is
   * resolved here <br/>eg <br/>Column Value BIRTHDAY 01-04-1980~01-04-2000 ->
   * where BIRTHDAY between 01-04-1980, 01-04-2000
   * 
   * @throws IllegalArgumentException
   *           one of the search criterias did not match the given grammar (see
   *           PPC-Manager "Implementierungskonzept", "4.1.5 Filtern und Suchen
   *           von Aufträgen"
   * @throws Exception
   *           something else went wrong
   * @param cp_dtoObj
   *          Object, that contains the search strings
   * @return String[] 0 -> all needed tables 1 -> "WHERE" String[1], meaning
   *         without "WHERE"
   */
  public String[] generateWhereClause(DTO_Interface cp_dtoObj)
      throws IllegalArgumentException, Exception {
    throw new NoSuchMethodException("Function not supported in GenericDAO!");
  }

  /**
   * Turn the resolving of attributes on/off for the search <br/>
   * 
   * Attributes for the column are searched for a specific syntax and the
   * content is replaced by a translated value, if the resolving is set to "on".
   * 
   * Example: ON -> "/=" is replaced by "NOT NULL" OFF -> "/=" is left untouched
   * 
   * @param parResolveSearch -
   *          true -> resolve column attributes, false - else
   * 
   */
  public void setResolveSearch(boolean parResolveSearch) {
    this.resolveSearch = parResolveSearch;
  } // setResolveSearch

  public boolean isResolveSearch() {
    return this.resolveSearch;
  }

  /**
   * Read a serialised DTO object located at fileName
   * 
   * @param fileName
   *          location of DTO Object
   * @return DTO Object
   * @throws ClassNotFoundException
   *           if class to load is not in classpath
   * @throws IOException
   *           if something went wrong while loading fileName
   */
  public static DTO_Interface readSerialisedObject(String fileName)
      throws ClassNotFoundException, IOException {
    DTO_Interface dto = null;
    try {
      FileInputStream fs = new FileInputStream(fileName);
      ObjectInputStream is = new ObjectInputStream(fs);
      dto = (DTO_Interface) is.readObject();
      is.close();
    } catch (ClassNotFoundException e) {
      throw e;
    } catch (IOException e) {
      throw e;
    }
    return dto;
  }

  /**
   * Serialise Object parDTO of type DTO_Interface to location pathName
   * 
   * @param pathName
   *          Location of serialised object
   * @param parDTO
   *          DTO to serialised
   * @throws IOException
   *           if something went wrong while loading fileName
   */
  public static void writeSerialisedObject(String pathName, DTO_Interface parDTO)
      throws IOException {
    try {
      FileOutputStream fs = new FileOutputStream(pathName);
      ObjectOutputStream os = new ObjectOutputStream(fs);
      os.writeObject(parDTO);
      os.close();
    } catch (IOException e) {
      throw e;
    }
  } // writeSerialisedObject

  /**
   * Convert a String to a Date object using pre-defined DateFormats <br/>
   * 
   * Hint: <br/>Uses the following global variables: <br(>
   * <ul>
   * <li>String dateFormats[]</li>
   * <li>int sqltimestampformat</li>
   * <li>int guitimestampformat</li>
   * <li>int guidateformat</li>
   * 
   * See dao_config.xml for the defined formats according to the given constant
   * values (int values above).
   * 
   * Also parses for "yyyy-MM-dd HH:mm:ss.S", which is the standard format of a
   * java.sql.Timestamp object called with "toString()" method
   * 
   * </ul>
   * 
   * @param cp_strQuery
   *          String containing the Date
   * @return java.util.Date
   * 
   * @throws IllegalArgumentException
   *           If the given String could not be parsed using the date formats
   *           specified above
   */
  private Date convertStringToDate(String cp_strQuery)
      throws IllegalArgumentException {
    // Format the current time.
    SimpleDateFormat formatter = new SimpleDateFormat(
        this.dateFormats[DB_Generic_DAO.sqltimestampformat]);
    // Parse the previous string back into a Date.
    ParsePosition pos = new ParsePosition(0);
    Date validDate = formatter.parse(cp_strQuery, pos);

    if (validDate == null) {
      // try: Maybe TimeStamp format
      // "yyyy-MM-dd HH:mm:ss.S"
      formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
      pos = new ParsePosition(0);
      validDate = formatter.parse(cp_strQuery, pos);
      // validDate = AccessHelper.AccessConverter(cp_strQuery);
    }
    if (validDate == null) {
      // try: Maybe TimeStamp format
      // "yyyy-MM-dd HH:mm:ss.S"
      formatter = new SimpleDateFormat(
          this.dateFormats[DB_Generic_DAO.guitimestampformat]);
      pos = new ParsePosition(0);
      validDate = formatter.parse(cp_strQuery, pos);
    }
    if (validDate == null) {
      // try: Maybe TimeStamp format
      // "yyyy-MM-dd HH:mm:ss.S"
      formatter = new SimpleDateFormat(
          this.dateFormats[DB_Generic_DAO.guidateformat]);
      pos = new ParsePosition(0);
      validDate = formatter.parse(cp_strQuery, pos);

      if (validDate == null) {
        throw new IllegalArgumentException(
            "resolveDate: DATE null; not a valid query string: " + cp_strQuery);
      }
    }
    return validDate;

  } // convertToTStamp

  private String[] generateWhereClauseSearchConfig(DTO_Interface parQBEobj,
      DBT_SearchConfig parSearchConfig, Vector cp_vecIgnoreTableName)
      throws Exception {

    // generate the WHERE clause
    // all SearchCriteria objects
    int itComparator = 0;
    DTO_Interface ct_dbdObject = null;
    DTO_Interface ct_dbdObject2 = null;
    int itOperator = 0;
    DBT_SearchCriteria ct_scrSCriteria = null;

    int itFlag = 0;
    String query = "";
    boolean addOpeningBrace = false;

    if (cp_vecIgnoreTableName == null) {
      cp_vecIgnoreTableName = new Vector();
    }

    for (Enumeration e = parSearchConfig.getSearchCriterias(); e
        .hasMoreElements();) {
      ct_scrSCriteria = (DBT_SearchCriteria) e.nextElement();
      if (ct_scrSCriteria.getKindOf() == 1) {
        // get search criterias
        itComparator = ct_scrSCriteria.getComparator();
        itOperator = ct_scrSCriteria.getOperator();
        ct_dbdObject = ct_scrSCriteria.getDataObject();
        ct_dbdObject2 = ct_scrSCriteria.getDataObject2();

        // create it
        if (ct_dbdObject2 != null) {
          //String comparator = this.myRSC_SQL.getComparator(itComparator);
          String operator = this.myRSC_SQL.getOperator(itOperator);
          // JOIN
          // two objects set -> implement JOIN
          if ((ct_dbdObject != null) && ((ct_dbdObject2 != null))) {
            String ct_strFirstProp = getDBprefix()
                + ct_dbdObject.getTableName() + "."
                + this.myRSC_SQL.getFirstProperty(ct_dbdObject);
            String ct_strScndProp = getDBprefix()
                + ct_dbdObject2.getTableName() + "."
                + this.myRSC_SQL.getFirstProperty(ct_dbdObject2);

            // remember the tables (duplicates are eliminated)
            if (cp_vecIgnoreTableName.contains(ct_dbdObject.getTableName()) == false) {
              cp_vecIgnoreTableName.addElement(ct_dbdObject.getTableName());
            }
            if (cp_vecIgnoreTableName.contains(ct_dbdObject2.getTableName()) == false) {
              cp_vecIgnoreTableName.addElement(ct_dbdObject2.getTableName());
            }
            // AND, OR, etc.
            if ((query.length() == 0)) {

              if (itFlag == 0) {
                if (addOpeningBrace == true) {
                  // query += " " + operator + "( ";
                  query += " ( ";
                  addOpeningBrace = false;
                } else {
                  // query += " " + operator;
                  query += " ";
                }
              } else {
                if (addOpeningBrace == true) {
                  query += " " + operator + "( ";
                  addOpeningBrace = false;
                } else {
                  query += " " + operator;
                }
              }
            } else {
              if (addOpeningBrace == true) {
                query += " " + operator + "( ";
                addOpeningBrace = false;
              } else {
                query += " " + operator;
              }
            }
            query += ct_strFirstProp + " "
                + this.myRSC_SQL.getComparator(itComparator) + " "
                + ct_strScndProp + " ";
            itFlag = 1;
          }
        } else {
          // static value
          String comparator = this.myRSC_SQL.getComparator(itComparator);
          String operator = this.myRSC_SQL.getOperator(itOperator);

          String[] standardWhere;
          if (ct_dbdObject.getTableName().equals(parQBEobj.getTableName())) {
            standardWhere = this.generateWhereClause(ct_dbdObject, true,
                comparator, comparator, comparator);
          } else {
            standardWhere = this.myRSC_SQL.resolveSearchForOneDTO(ct_dbdObject,
                itComparator);
          }

          if ((query.length() == 0)) {
            if (itFlag == 0) {
              if (addOpeningBrace == true) {
                query += " (" + standardWhere[1];
                addOpeningBrace = false;
              } else {
                query += standardWhere[1];
              }
            } else {
              if (addOpeningBrace == true) {
                query += operator + " (" + standardWhere[1];
                addOpeningBrace = false;
              } else {
                query += operator + " " + standardWhere[1];
              }
            }
          } else {
            if (addOpeningBrace == true) {
              query += operator + " (" + standardWhere[1];
              addOpeningBrace = false;
            } else {
              query += operator + " " + standardWhere[1];
            }
          }
          if (cp_vecIgnoreTableName.contains(standardWhere[0]) == false) {
            if (standardWhere[0].length() != 0) {
              cp_vecIgnoreTableName.addElement(standardWhere[0]);
            }
          }
          this.getLogger().debug("standardWhere[0]: " + standardWhere[0]);

          if (itFlag == 0) {
            itFlag = 2;
          }
        }
      } // kindOf == 1
      else if (ct_scrSCriteria.getKindOf() == 2) {
        // kindOf == 2 // SELECT... WHERE... IN...
      } else {
        // kindOf == 3 // OPENING_BRACE, CLOSING_BRACE
        int iBrace = ct_scrSCriteria.getBrace();

        if (iBrace == DBT_SearchConfig.OPENING_BRACE) {
          addOpeningBrace = true;
        } else {
          query += ")";
        }
        itFlag = 3;
      }

    } // for... all searchCriterias
    // Create table String
    String tables = "";

    for (Enumeration e = cp_vecIgnoreTableName.elements(); e.hasMoreElements();) {
      if (tables.length() == 0) {
        tables = (String) e.nextElement();
      } else {
        tables += ", " + (String) e.nextElement();
      }
    }

    return new String[] { tables, query };
  } // generateWhereClauseSearchConfig()

  /**
   * Method, that returns the generated WHERE clause for a given object <br/>
   * 
   * This method is just here for "dummy" purposes, see the derived DAO for an
   * implementation <br/>
   * 
   * @throws IllegalArgumentException
   *           one of the search criterias did not match the given grammar (see
   *           PPC-Manager "Implementierungskonzept", "4.1.5 Filtern und Suchen
   *           von Aufträgen"
   * @throws Exception
   *           something else went wrong
   * @param parDTOobj
   *          Object, that contains the search strings
   * @return String[] 0 -> all needed tables 1 -> "WHERE" String[1], meaning
   *         without "WHERE"
   * 
   * @param pUse
   *          Use attributes from referenced objects (attributes from objects
   *          that were set using FK_OBJ)
   * 
   * @param cp_strComparatorDATE
   *          use this to compare DATE objects
   * @param cp_strComparatorTEXT
   *          use this to compare TEXT objects
   * @param cp_strComparatorNUMBER
   *          use this to compare NUMBER objects
   */
  public String[] generateWhereClause(DTO_Interface parDTOobj, boolean pUse,
      String cp_strComparatorDATE, String cp_strComparatorTEXT,
      String cp_strComparatorNUMBER) throws IllegalArgumentException, Exception {
    throw new Exception(
        "GenericDAO: generateWhereClause -> Method not implemented");
  } // generateWhereClause

  /**
   * Return true, if the system uses a primary key creator (factory), false
   * else.
   * 
   * @return true - system uses primary key creator factory, else primary key is
   *         left untouched
   */
  public boolean usePrimaryKeyCreator() {
    return this.bCreatePrimaryKey;
  }

  /**
   * Configure the system to use a primary key creator, or leave the primary key
   * value untouched<br/>
   * 
   * @param parCreatePrimaryKey
   *          true - use primary key factory, false - leave value untouched
   */
  public void setCreatePrimaryKey(boolean parCreatePrimaryKey) {
    this.bCreatePrimaryKey = parCreatePrimaryKey;
  }

  /**
   * Tribute to all SQL purists -> send a SQL update String directly to the
   * database<br/> <br/>
   * 
   * @param parUpdString
   *          delete, create table, etc.
   */
  public int sendSQLupdate(String parUpdString) throws SQLException {
    Statement sbStatement = getConnection().createStatement();

    getLogger().debug("sendSQLupdate: " + parUpdString);

    int itRetValue = sbStatement.executeUpdate(parUpdString);

    sbStatement.close();
    return itRetValue;
  }// sendSQLupdate

} // Generic_DAO.java
// ########## Generic_DAO.java ##########
