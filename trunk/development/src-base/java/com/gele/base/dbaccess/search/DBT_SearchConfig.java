/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: DBT_SearchConfig.java 205 2006-08-16 14:49:30Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/search/DBT_SearchConfig.java $
 * $Rev: 205 $
 */

package com.gele.base.dbaccess.search;

/**
 * Zusammenstellen von Suchkriterien f�r komplexe Suchanfragen.<br/>
 *
 * Moeglichkeit der Konfiguration der Suche:
 *
 * 1. COLUMN = staticValue
 * 2. TABLE1.COLUMN1 = TABLE2.COLUMN2
 * 3. KlammerAuf
 * 4. KlammerZu
 *
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 *
 * @version $Revision: 205 $
 */

// standard java packages
import java.util.Enumeration;
import java.util.Vector;

import com.gele.base.dbaccess.DTO_Interface;

public class DBT_SearchConfig {

  // supported operators
  public static final int OPERATOR_AND = 0;
  public static final int OPERATOR_OR = 1;

  // supported comparators
  public static final int COMPARATOR_EQUAL = 0;
  public static final int COMPARATOR_LESS = 1;
  public static final int COMPARATOR_GREATER = 2;
  public static final int COMPARATOR_LIKE = 3;
  public static final int COMPARATOR_LESSTHAN = 4;
  public static final int COMPARATOR_GREATERTHAN = 5;
  public static final int COMPARATOR_NOTEQUAL = 6;

  // Opening/Closing Brace
  public static final int OPENING_BRACE = 0;
  public static final int CLOSING_BRACE = 1;

  // vector containing all SearchCriteria objects
  private Vector c_vecSearchCriterias = new Vector();

  /**
   * Add a search criteria<br/>
   * <br/>
   * Add an object property, comparator and operator to configurate the search<br/>
   * <br/>
   * eg<br/>
   * Person myPerson = new Person();                                   <br/>
   * Person.setCountry( "united" );                                    <br/>
   * DBT_SearchConfig mySC = new DBT_SearchConfig();                   <br/>
   * mySC.addSearchItem( myPerson, DBT_SearchConfig.COMPARATOR_LIKE ); <br/>
   * DBAccess myDBA = new DBAccess();                                  <br/>
   * Vector result = myDBA.getAllPerson( myPerson, mySC );             <br/>
   * <br/>
   * This will return all 'Person' entries found inside the database where the 'country'
   * attribute is 'LIKE' united ("United States of America", "United Kingdom", etc.)
   *
   * Possible values for the ipComparator parameter:<br/>
   * <br/>
   * DBT_SearchConfig.COMPARATOR_EQUAL                 0<br/>
   * DBT_SearchConfig.COMPARATOR_LESS                  1<br/>
   * DBT_SearchConfig.COMPARATOR_GREATER               2<br/>
   * DBT_SearchConfig.COMPARATOR_LIKE                  3<br/>
   * DBT_SearchConfig.COMPARATOR_LESSTHAN              4<br/>
   * DBT_SearchConfig.COMPARATOR_GREATERTHAN           5<br/>
   * DBT_SearchConfig.COMPARATOR_NOTEQUAL              7<br/>
   * <br/>
   *
   *
   * @param cp_dbdObj     DBT_DataObject, object with attributes set
   * @param ipComparator  int, comparator for the search
   */
  public void addSearchCriteria(DTO_Interface cp_dbdObj, int ipComparator) {
    this.c_vecSearchCriterias.addElement(
      new DBT_SearchCriteria(
        cp_dbdObj,
        ipComparator,
        DBT_SearchConfig.OPERATOR_AND));
  } // ########## addSearchCriteria ##########

  /**
   * Compare to a static value<br/>
   * <br/>
   *
   * Warning:<br/>
   * If you do specify a class that does not directly belong to the
   * used DAO (eg. you specified object of type "Person", but you use
   * a "Personal_DAO" to retrieve data), the process to generate the
   * query String will use a specific "generateWhereClause" algorithm,
   * which will not resolve any SQL specific characters (eg '*' -> '%'),
   * or take care for any date-conversion, etc!<br/>
   *
   * Example:
   * <quote>
   * Personal_DAO = myPersonal_DAO();
   * ...
   * DBT_SearchConfig myDBTSC = new DBT_SearchConfig();
   *
   * Person myPerson = new Person();
   * myPerson.setName( "Muster%" ); // USE '%', NOT '*'
   *
   * myDBTSC.addSearchCriteria(
   *   myPerson,
   *   (DTO_Interface)null,
   *   DBT_SearchConfig.COMPARATOR_LIKE
   * );
   *
   * Vector retValue = myPersonal_DAO.getObjects( myPerson, myDBTSC, (Vector)null, 1, 1 );
   *
   * </quote>
   *
   * Possible values for the ipComparator parameter:<br/>
   * <br/>
   * DBT_SearchConfig.COMPARATOR_EQUAL                 0<br/>
   * DBT_SearchConfig.COMPARATOR_LESS                  1<br/>
   * DBT_SearchConfig.COMPARATOR_GREATER               2<br/>
   * DBT_SearchConfig.COMPARATOR_LIKE                  3<br/>
   * DBT_SearchConfig.COMPARATOR_LESSTHAN              4<br/>
   * DBT_SearchConfig.COMPARATOR_GREATERTHAN           5<br/>
   * DBT_SearchConfig.COMPARATOR_NOTEQUAL              7<br/>
   * <br/>
   *
   * Possible values for the ipOperator parameter:<br/>
   * <br/>
   * DBT_SearchConfig.OPERATOR_AND       0<br/>
   * DBT_SearchConfig.OPERATOR_OR        1<br/>
   * <br/>
   *
   * @param cp_dbdObj read attribute from this object
   * @param ipComparator  use this comparator
   * @param itOperator    use this operator
   */
  public void addSearchCriteria(
    DTO_Interface cp_dbdObj,
    int ipComparator,
    int itOperator) {
    this.c_vecSearchCriterias.addElement(
      new DBT_SearchCriteria(cp_dbdObj, ipComparator, itOperator));
  } // ########## addSearchCriteria ##########

  /**
   * JOIN two tables<br/>
   *
   * Possible values for the ipComparator parameter:<br/>
   * <br/>
   * DBT_SearchConfig.COMPARATOR_EQUAL                 0<br/>
   * DBT_SearchConfig.COMPARATOR_LESS                  1<br/>
   * DBT_SearchConfig.COMPARATOR_GREATER               2<br/>
   * DBT_SearchConfig.COMPARATOR_LIKE                  3<br/>
   * DBT_SearchConfig.COMPARATOR_LESSTHAN              4<br/>
   * DBT_SearchConfig.COMPARATOR_GREATERTHAN           5<br/>
   * DBT_SearchConfig.COMPARATOR_NOTEQUAL              7<br/>
   * <br/>
   *
   * Uses AND if other attributes were specified<br/>
   *
   * @param cp_dbdObj1 compare the SET attribute of this object with the
   *        other object's attribute
   * @param cp_dbdObj2 compare the SET attribute of this object with the
   *        other object's attribute
   * @param ipComparator see DBT_SearchConfig for valid values
   */
  public void addSearchCriteria(
    DTO_Interface cp_dbdObj1,
    DTO_Interface cp_dbdObj2,
    int ipComparator) {
    this.c_vecSearchCriterias.addElement(
      new DBT_SearchCriteria(
        cp_dbdObj1,
        cp_dbdObj2,
        ipComparator,
        DBT_SearchConfig.OPERATOR_AND));
  } // ########## addSearchCriteria ##########

  /**
   * JOIN two tables<br/>
   *
   * @param cp_dbdObj1 compare the SET attribute of this object with the
   *        other object's attribute
   * @param cp_dbdObj2 compare the SET attribute of this object with the
   *        other object's attribute
   * @param ipComparator see DBT_SearchConfig for valid values
   * @param itOperator AND; OR, etc.
   */
  public void addSearchCriteria(
    DTO_Interface cp_dbdObj1,
    DTO_Interface cp_dbdObj2,
    int ipComparator,
    int itOperator) {
    this.c_vecSearchCriterias.addElement(
      new DBT_SearchCriteria(cp_dbdObj1, cp_dbdObj2, ipComparator, itOperator));
  } // ########## addSearchCriteria ##########

  /**
   * Specify "IN" query<br/>
   *
   * Possible values for the ipComparator parameter:<br/>
   * <br/>
   * DBT_SearchConfig.COMPARATOR_EQUAL                 0<br/>
   * DBT_SearchConfig.COMPARATOR_LESS                  1<br/>
   * DBT_SearchConfig.COMPARATOR_GREATER               2<br/>
   * DBT_SearchConfig.COMPARATOR_LIKE                  3<br/>
   * DBT_SearchConfig.COMPARATOR_LESSTHAN              4<br/>
   * DBT_SearchConfig.COMPARATOR_GREATERTHAN           5<br/>
   * DBT_SearchConfig.COMPARATOR_NOTEQUAL              7<br/>
   * <br/>
   *
   * Eg:<br/>
   * select * from TABLE where TABLE.ID in (1,2);<br/>
   *
   * @param cp_dbdObj1 compare the SET attribute of this object with the
   *        other attributes (Specifies TABLE.xx)
   * @param cp_vecValues use this values (object.toString() method is used
   *        to retrieve the value).
   * @param ipComparator see DBT_SearchConfig for valid values
   */
  public void addSearchCriteria(
    DTO_Interface cp_dbdObj1,
    Vector cp_vecValues,
    int ipComparator) {
    this.c_vecSearchCriterias.addElement(
      new DBT_SearchCriteria(
        cp_dbdObj1,
        cp_vecValues,
        ipComparator,
        DBT_SearchConfig.OPERATOR_AND));
  } // ########## addSearchCriteria ##########

  /**
   * Specify "IN" query<br/>
   *
   * Possible values for the ipComparator parameter:<br/>
   * <br/>
   * DBT_SearchConfig.COMPARATOR_EQUAL                 0<br/>
   * DBT_SearchConfig.COMPARATOR_LESS                  1<br/>
   * DBT_SearchConfig.COMPARATOR_GREATER               2<br/>
   * DBT_SearchConfig.COMPARATOR_LIKE                  3<br/>
   * DBT_SearchConfig.COMPARATOR_LESSTHAN              4<br/>
   * DBT_SearchConfig.COMPARATOR_GREATERTHAN           5<br/>
   * DBT_SearchConfig.COMPARATOR_NOTEQUAL              7<br/>
   * <br/>
   *
   * Possible values for the ipOperator parameter:<br/>
   * <br/>
   * DBT_SearchConfig.OPERATOR_AND       0<br/>
   * DBT_SearchConfig.OPERATOR_OR        1<br/>
   * <br/>
   *
   * Eg:<br/>
   * select * from TABLE where TABLE.ID in (1,2);<br/>
   *
   * @param cp_dbdObj1 compare the SET attribute of this object with the
   *        other attributes (Specifies TABLE.xx)
   * @param cp_vecValues use this values (object.toString() method is used
   *        to retrieve the value).
   * @param ipComparator see DBT_SearchConfig for valid values
   * @param ipOperator AND; OR, etc.
   */
  public void addSearchCriteria(
    DTO_Interface cp_dbdObj1,
    Vector cp_vecValues,
    int ipComparator,
    int ipOperator) {
    this.c_vecSearchCriterias.addElement(
      new DBT_SearchCriteria(
        cp_dbdObj1,
        cp_vecValues,
        ipComparator,
        ipOperator));
  } // ########## addSearchCriteria ##########

  /**
   * Add opening, closing brace<br/>
   *
   * @param ipBrace OPENING_BRACE, CLOSING_BRACE
   */
  public void addSearchCriteria(int ipBrace) {
    this.c_vecSearchCriterias.addElement(new DBT_SearchCriteria(ipBrace));
  } // ########## addSearchCriteria ##########

  /**
   * Returns all registered SearchCriteria instances<br/>
   * <br/>
   * @return Enumeration  all SearchCriteria instances
   */
  public Enumeration getSearchCriterias() {
    return this.c_vecSearchCriterias.elements();
  } // ########## getSearchCriterias ##########

} // DBT_SearchConfig
// ########## DBT_SearchConfig ##########
