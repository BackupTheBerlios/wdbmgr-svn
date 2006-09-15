/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: ResolveSearchConfig_SQL.java 205 2006-08-16 14:49:30Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/search/ResolveSearchConfig_SQL.java $
 * $Rev: 205 $
 */

package com.gele.base.dbaccess.search;

// standard packages
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.gele.base.dbaccess.DTO_Interface;

/**
 * <p>Title: Resolve the SearchConfiguration to specify JOIN, IN queries
 *           using DTOs.</p>
 *
 * <p>Description: Resolve search configuration to SQL
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Gerhard Leibrock, Class taken from the dbTool product</p>
 * @author <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 * @version $Id: ResolveSearchConfig_SQL.java 205 2006-08-16 14:49:30Z gleibrock $
 */

public class ResolveSearchConfig_SQL implements ResolveSearchConfig {

  public ResolveSearchConfig_SQL() {
  }

  /**
   * Creates the SQL query string out of the given objects<br/>
   * <br/>
   * Returns
   *  "<tablename1, tablename2, ...> , <generated_out_of_DBT_SearchConfig><br/>
   * <br/>
   *
   * @param cp_strTableName name of the SQL database table
   * @param parSCobj search configuration object
   * @param cp_vecSortObjs sort by
   * @return SQL string[0] -> used tables
   *             string[1] WHERE clause without "WHERE" statement
   */
  public String[] resolveSearchAndSorting(
    String cp_strTableName,
    DBT_SearchConfig parSCobj,
    Vector cp_vecSortObjs) {
    // empty -> set it anyway
    if (parSCobj == null)
      parSCobj = new DBT_SearchConfig();

    String retValue[] = new String[2];

    retValue[0] = ""; // used tables
    retValue[1] = ""; // generated WHERE clause (without "WHERE")

    String sophisticated = "";

    // all SearchCriteria objects
    int itComparator = 0;
    DTO_Interface ct_dbdObject = null;
    DTO_Interface ct_dbdObject2 = null;
    int itOperator = 0;
    DBT_SearchCriteria ct_scrSCriteria = null;

    // SearchCriterias: START
    // itFlag = 1 -> sophisticated : found this first
    // itFalg = 2 -> query         : else
    int itFlag = 0;
    // keep track of the tables that are required for the query
    Hashtable ct_htaTables = new Hashtable();

    ct_htaTables.put(cp_strTableName, "");
    for (Enumeration e = parSCobj.getSearchCriterias(); e.hasMoreElements();) {
      ct_scrSCriteria = (DBT_SearchCriteria) e.nextElement();
      if (ct_scrSCriteria.getKindOf() == 1) {
        // get search criterias
        itComparator = ct_scrSCriteria.getComparator();
        itOperator = ct_scrSCriteria.getOperator();
        ct_dbdObject = ct_scrSCriteria.getDataObject();
        ct_dbdObject2 = ct_scrSCriteria.getDataObject2();

        // two objects set -> implement JOIN
        if ((ct_dbdObject != null) && ((ct_dbdObject2 != null))) {
          String ct_strFirstProp =
            ct_dbdObject.getTableName() + "." + getFirstProperty(ct_dbdObject);
          String ct_strScndProp =
            ct_dbdObject2.getTableName()
              + "."
              + getFirstProperty(ct_dbdObject2);

          // remember the tables (duplicates are eliminated automatically by Hashtable)
          ct_htaTables.put(ct_dbdObject.getTableName(), "");
          ct_htaTables.put(ct_dbdObject2.getTableName(), "");
          // AND, OR, etc.
          if (itFlag == 0)
            itFlag = 1;
          else
            sophisticated += getOperator(itOperator);
          sophisticated += ct_strFirstProp
            + " "
            + getComparator(itComparator)
            + " "
            + ct_strScndProp;
          continue;
        }

        if (ct_dbdObject.getObjectProperties().size() > 0) {
          // if( query.length() == 0 ) query += " WHERE ";

          Properties ct_propProps = ct_dbdObject.getObjectProperties();
          String ct_strPropName = "";
          Object ct_objPropValue = "";

          for (Enumeration ee = ct_propProps.propertyNames();
            ee.hasMoreElements();
            ) {
            ct_strPropName = (String) ee.nextElement();
            ct_objPropValue = ct_propProps.get(ct_strPropName);
            if ((ct_objPropValue instanceof Float)
              || (ct_objPropValue instanceof Double)
              || (ct_objPropValue instanceof Integer)
              || (ct_objPropValue instanceof Long)
              || (ct_objPropValue instanceof java.math.BigDecimal)
              || (ct_objPropValue instanceof java.math.BigInteger)
              || (ct_objPropValue instanceof Boolean)) {
              // AND, OR, etc.
              if (itFlag == 0)
                itFlag = 2;
              else
                retValue[1] += this.getOperator(itOperator);
              // <, =, >, <=, >=, LIKE, etc
              retValue[1] += " "
                + this.getNumericComparatorString(
                  ct_dbdObject.getTableName(),
                  ct_strPropName,
                  ct_objPropValue,
                  itComparator);
            } else { // use QUOTEs
              if (itFlag == 0)
                itFlag = 2;
              else
                retValue[1] += this.getOperator(itOperator);
              // <, =, >, <=, >=, LIKE, etc
              retValue[1] += " "
                + this.getTextualComparatorString(
                  ct_dbdObject.getTableName(),
                  ct_strPropName,
                  ct_objPropValue,
                  itComparator);
            }
            ct_htaTables.put(ct_dbdObject.getTableName(), "");
          } // QueryByExample object
        }
      } // kindOf == 1
      else { // kindOf == 2
        // Specify "SELECT ... IN (..)" query
      }
    } // SearchCriterias: END
    // construct the TABLEs
    String ct_strTableNames = "";

    for (Enumeration enu = ct_htaTables.keys(); enu.hasMoreElements();) {
      if (ct_strTableNames.length() != 0)
        ct_strTableNames += ", ";
      ct_strTableNames += (String) enu.nextElement();
    }

    // if( sophisticated.length() == 0 ) innerJoin = " "+ cp_strTableName +" ";
    if (itFlag == 1)
      retValue[1] = sophisticated + retValue[1];
    else
      retValue[1] = retValue[1] + sophisticated;
    // if (itFlag == 1)
    // query = ct_strTableNames + " WHERE " + sophisticated + query;
    // else
    // query = ct_strTableNames + " WHERE " + query + sophisticated;

    // Sort: START
    if (cp_vecSortObjs != null) {
      retValue[1] += " ORDER BY ";
      boolean addComma = false;

      for (Enumeration enu = cp_vecSortObjs.elements();
        enu.hasMoreElements();
        ) {
        DTO_Interface parSORTobj = (DTO_Interface) enu.nextElement();

        Properties ct_propProps = parSORTobj.getObjectProperties();
        String ct_strPropName = "";
        Object ct_objPropValue = "";

        for (Enumeration e = ct_propProps.propertyNames();
          e.hasMoreElements();
          ) {
          ct_strPropName = (String) e.nextElement();
          // now we have all relevant items; the value is irrelevant
          // (the value is only relevant for the QBE object)
          // add "," (comma)
          if (addComma == false)
            addComma = true;
          else
            retValue[1] += ", ";
          retValue[1] += parSORTobj.getTableName() + "." + ct_strPropName;
        } // QueryByExample object
      }
    }
    // Sort: END

    return retValue;
  } // ########## resolveSearchAndSorting ##########

  /**
   * Generate a WHERE clause for a single DTO compared to a static value<br/>
   * <br/>
   * Returns (without WHERE)
   * a String array:
   *
   * String [0] -> used table
   * String [1} -> TABLE.COLUMN = VALUE
   *
   * ipComparator:<br/>
   * See DBT_SearchConfig
   *
   *   public static final int COMPARATOR_EQUAL = 0;
   *   public static final int COMPARATOR_LESS = 1;
   *   public static final int COMPARATOR_GREATER = 2;
   *   public static final int COMPARATOR_LIKE = 3;
   *   public static final int COMPARATOR_LESSTHAN = 4;
   *   public static final int COMPARATOR_GREATERTHAN = 5;
   *   public static final int COMPARATOR_NOTEQUAL = 6;
   *
   * @param cp_dtoObj Use this DTO for comparison
   * @param ip_Comparator Specify the comparator, @see DBT_SearchConfig
   * @return String[] containing used table(s) and query string
   */
  public String[] resolveSearchForOneDTO(
    DTO_Interface cp_dtoObj,
    int ip_Comparator) {

    String retValue[] = new String[2];

    retValue[0] = ""; // used tables
    retValue[1] = ""; // generated WHERE clause (without "WHERE")

    String sophisticated = "";

    // SearchCriterias: START
    // keep track of the tables that are required for the query
    Hashtable ct_htaTables = new Hashtable();

    if (cp_dtoObj.getObjectProperties().size() > 0) {
      // if( query.length() == 0 ) query += " WHERE ";

      Properties ct_propProps = cp_dtoObj.getObjectProperties();
      String ct_strPropName = "";
      Object ct_objPropValue = "";

      for (Enumeration ee = ct_propProps.propertyNames();
        ee.hasMoreElements();
        ) {
        ct_strPropName = (String) ee.nextElement();
        ct_objPropValue = ct_propProps.get(ct_strPropName);
        if ((ct_objPropValue instanceof Float)
          || (ct_objPropValue instanceof Double)
          || (ct_objPropValue instanceof Integer)
          || (ct_objPropValue instanceof Long)
          || (ct_objPropValue instanceof java.math.BigDecimal)
          || (ct_objPropValue instanceof java.math.BigInteger)
          || (ct_objPropValue instanceof Boolean)) {
          // AND, OR, etc.
          // <, =, >, <=, >=, LIKE, etc
          retValue[1] += " "
            + this.getNumericComparatorString(
              cp_dtoObj.getTableName(),
              ct_strPropName,
              ct_objPropValue,
              ip_Comparator);
        } else { // use QUOTEs
          // <, =, >, <=, >=, LIKE, etc
          retValue[1] += " "
            + this.getTextualComparatorString(
              cp_dtoObj.getTableName(),
              ct_strPropName,
              ct_objPropValue,
              ip_Comparator);
        }
        ct_htaTables.put(cp_dtoObj.getTableName(), "");
      } // QueryByExample object
    }
    // construct the TABLEs

    for (Enumeration enu = ct_htaTables.keys(); enu.hasMoreElements();) {
      if (retValue[0].length() != 0)
        retValue[0] += ", ";
      retValue[0] += (String) enu.nextElement();
    }

    return retValue;
  } // ########## resolveSearchForOneDTO ##########

  /**
   * Returns the first property of an object that got found<br/>
   *
   * Returns the column found that contains a value<br/>
   * eg:
   *
   * Personal myPersonal = new Personal();
   * myPersonal.setName( "Mueller" );
   * ...
   * String retValue = getFirstProperty( myPersonal );
   * ...
   * retValue -> "Name"
   *
   * <br/>
   * This method questions an object about its set properties and returns
   * the first one it finds.<br/>
   * <br/>
   * @param parObj   object to be scanned
   * @return String  first property value found
   */
  public String getFirstProperty(DTO_Interface parObj) {
    Properties ct_propProps = parObj.getObjectProperties();
    String ct_strPropName = "";

    for (Enumeration e = ct_propProps.propertyNames(); e.hasMoreElements();) {
      ct_strPropName = (String) e.nextElement();
      break;
    } // QueryByExample object
    return ct_strPropName;
  } // ########## getFirstProperty ##########

  /**
   * Returns the textual representation of the operator<br/>
   * <br/>
   *
   * @see DBT_SearchConfig
   *
   * @param ipOperator  identifier for the operator
   * @return String     textual representation (AND, OR, etc)
   */
  public String getOperator(int ipOperator) {
    String ct_strRetValue = "";

    switch (ipOperator) {
      case DBT_SearchConfig.OPERATOR_AND :
        ct_strRetValue = " AND ";
        break;

      case DBT_SearchConfig.OPERATOR_OR :
        ct_strRetValue = " OR ";
        break;

      default :
        ct_strRetValue = " AND ";
        break;
    }

    return ct_strRetValue;
  } // getOperator

  // ########## getOperator ##########

  /**
   * Returns the textual representation of the comparator<br/>
   * <br/>
   * @param ipComparator identifier for the comparator
   * @return String      textual representation ('=', '<', '>', 'LIKE', etc)
   */
  public String getComparator(int ipComparator) {
    String ct_strRetValue = "";

    switch (ipComparator) {
      case DBT_SearchConfig.COMPARATOR_NOTEQUAL :
        ct_strRetValue = " <> ";
        break;

      case DBT_SearchConfig.COMPARATOR_EQUAL :
        ct_strRetValue = " = ";
        break;

      case DBT_SearchConfig.COMPARATOR_LESS :
        ct_strRetValue = " < ";
        break;

      case DBT_SearchConfig.COMPARATOR_GREATER :
        ct_strRetValue = " > ";
        break;

      case DBT_SearchConfig.COMPARATOR_LESSTHAN :
        ct_strRetValue = " <= ";
        break;

      case DBT_SearchConfig.COMPARATOR_GREATERTHAN :
        ct_strRetValue = " >= ";
        break;

      case DBT_SearchConfig.COMPARATOR_LIKE :
        ct_strRetValue = " LIKE ";
        break;

      default :
        ct_strRetValue = " = ";
        break;
    }
    return ct_strRetValue;
  } // getComparator

  // ########## getComparator ##########

  /**
   * Returns a SQL string for comparison of a column with a specific value<br/>
   * <br/>
   * Only use this method for NUMERIC types<br/>
   *
   * @return String  SQL string
   * @param cp_strTableName
   * @param cp_strPropname
   * @param cp_objPropValue
   * @param ipComparator
   */
  private String getNumericComparatorString(
    String cp_strTableName,
    String cp_strPropname,
    Object cp_objPropValue,
    int ipComparator) {
    String ct_strRetValue = "";

    switch (ipComparator) {
      case DBT_SearchConfig.COMPARATOR_NOTEQUAL :
        ct_strRetValue =
          cp_strTableName + "." + cp_strPropname + " <> " + cp_objPropValue;
        break;

      case DBT_SearchConfig.COMPARATOR_EQUAL :
        ct_strRetValue =
          cp_strTableName + "." + cp_strPropname + " = " + cp_objPropValue;
        break;

      case DBT_SearchConfig.COMPARATOR_LESS :
        ct_strRetValue =
          cp_strTableName + "." + cp_strPropname + " < " + cp_objPropValue;
        break;

      case DBT_SearchConfig.COMPARATOR_GREATER :
        ct_strRetValue =
          cp_strTableName + "." + cp_strPropname + " > " + cp_objPropValue;
        break;

      case DBT_SearchConfig.COMPARATOR_LESSTHAN :
        ct_strRetValue =
          cp_strTableName + "." + cp_strPropname + " <= " + cp_objPropValue;
        break;

      case DBT_SearchConfig.COMPARATOR_GREATERTHAN :
        ct_strRetValue =
          cp_strTableName + "." + cp_strPropname + " >= " + cp_objPropValue;
        break;

      default :
        ct_strRetValue =
          cp_strTableName + "." + cp_strPropname + " = " + cp_objPropValue;
        break;
    }
    return ct_strRetValue;
  } // getNumericComparatorString

  // ########## getNumericComparatorString ##########

  private String getTextualComparatorString(
    String cp_strTableName,
    String cp_strPropname,
    Object cp_objPropValue,
    int ipComparator) {
    String ct_strRetValue = "";

    switch (ipComparator) {
      case DBT_SearchConfig.COMPARATOR_NOTEQUAL :
        ct_strRetValue += cp_strTableName
          + "."
          + cp_strPropname
          + " <> '"
          + cp_objPropValue
          + "'";
        break;

      case DBT_SearchConfig.COMPARATOR_EQUAL :
        ct_strRetValue += cp_strTableName
          + "."
          + cp_strPropname
          + " = '"
          + cp_objPropValue
          + "'";
        break;

      case DBT_SearchConfig.COMPARATOR_LIKE :
        ct_strRetValue += cp_strTableName
          + "."
          + cp_strPropname
          + " LIKE '"
          + cp_objPropValue
          + "'";
        break;

      default :
        ct_strRetValue += cp_strTableName
          + "."
          + cp_strPropname
          + " = '"
          + cp_objPropValue
          + "'";
        break;

        /*
         case DBT_SearchConfig.COMPARATOR_LESSTHAN:
         ct_strRetValue += cp_strTableName+"."+cp_strPropname +" <= '"+cp_objPropValue+"'";
         break;
         case DBT_SearchConfig.COMPARATOR_GREATERTHAN:
         ct_strRetValue += cp_strTableName+"."+cp_strPropname +" >= '"+cp_objPropValue+"'";
         break;
         case DBT_SearchConfig.COMPARATOR_LESS:
         ct_strRetValue += cp_strTableName+"."+cp_strPropname +" < '"+cp_objPropValue+"'";
         break;
         case DBT_SearchConfig.COMPARATOR_GREATER:
         ct_strRetValue += cp_strTableName+"."+cp_strPropname +" > '"+cp_objPropValue+"'";
         break;
         */
    }
    return ct_strRetValue;
  } // getTextualComparatorString

  // ########## getTextualComparatorString ##########

}
