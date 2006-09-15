/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: DBT_SearchCriteria.java 205 2006-08-16 14:49:30Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/search/DBT_SearchCriteria.java $
 * $Rev: 205 $
 */

package com.gele.base.dbaccess.search;

/**
 * Source basierend auf den Klassen von dbTool, Gerhard Leibrock<br/>
 *
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 *
 * @version $Revision: 205 $
 */

// standard packages
import java.util.Vector;

import com.gele.base.dbaccess.DTO_Interface;

public class DBT_SearchCriteria {
  // attributes to specify a query
  private int iComparator;
  private DTO_Interface c_dbdObject1;
  private DTO_Interface c_dbdObject2;
  private int iOperator;
  // kind of query
  private int iKindOfQuery;
  /* Possible values:
   * 1 -> compare object attribute to another objects attribute or static value
   * 2 -> use IN query
   * 3 -> add Opening, Closing Brace
   */
  private int iBrace;

  private Vector vecAttributes; // kindOf == 2

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
   * @param cp_dbdObj     DBT_DataObject, object with attributes set
   * @param ipComparator  int, comparator for the search
   * @param ipOperator    int, operator; see DBT_SearchConfig for details
   */
  public DBT_SearchCriteria(
    DTO_Interface cp_dbdObj,
    int ipComparator,
    int ipOperator) {
    this.c_dbdObject1 = cp_dbdObj;
    this.c_dbdObject2 = null;
    this.iComparator = ipComparator;
    this.iOperator = ipOperator;
    this.iKindOfQuery = 1;
  } //########## constructor ##########

  /**
   * Add a search criteria<br/>
   * <br/>
   * Add an object property, comparator and operator to configurate the search<br/>
   * <br/>
   *
   * @param cp_dbdObj1    DBT_DataObject, object with attributes set, represents 'source' table
   * @param cp_dbdObj2    DBT_DataObject, object with attributes set, represents other table
   * @param ipComparator  int, comparator for the search
   * @param ipOperator    int, operator; see DBT_SearchConfig for details
   */
  public DBT_SearchCriteria(
    DTO_Interface cp_dbdObj1,
    DTO_Interface cp_dbdObj2,
    int ipComparator,
    int ipOperator) {
    this.c_dbdObject1 = cp_dbdObj1;
    this.c_dbdObject2 = cp_dbdObj2;
    this.iComparator = ipComparator;
    this.iOperator = ipOperator;
    this.iKindOfQuery = 1;
  } //########## constructor ##########

  /**
   * Add a search criteria<br/>
   * <br/>
   * Add an object property, comparator and operator to configurate the search<br/>
   * <br/>
   *
   * select * from TABLE where TABLE.ID in (1,2);<br/>
   *
   * @param cp_dbdObj1 compare the SET attribute of this object with the
   *        other attributes (Specifies TABLE.xx)
   * @param cp_vecValues use this values (object.toString() method is used
   *        to retrieve the value).
   * @param ipComparator see DBT_SearchConfig for valid values
   * @param ipOperator AND; OR, etc.
   *
   */
  public DBT_SearchCriteria(
    DTO_Interface cp_dbdObj1,
    Vector cp_vecValues,
    int ipComparator,
    int ipOperator) {
    this.c_dbdObject1 = cp_dbdObj1;
    this.vecAttributes = cp_vecValues;
    this.iComparator = ipComparator;
    this.iOperator = ipOperator;
    this.iKindOfQuery = 2;
  } //########## constructor ##########

  public DBT_SearchCriteria(int ipBrace) {
    this.iBrace = ipBrace;
    this.iKindOfQuery = 3;
  } //########## constructor ##########

  /**
   * Returns the object containing the attribute for the search<br/>
   *
   * @return returns the used DBT_DataObject instance
   */
  public DTO_Interface getDataObject() {
    return this.c_dbdObject1;
  } //########## getDataObject ##########

  /**
   * Returns the object containing the attribute for the search<br/>
   *
   * @return returns the 2nd used DBT_DataObject instance
   */
  public DTO_Interface getDataObject2() {
    return this.c_dbdObject2;
  } //########## getDataObject2 ##########

  /**
   * @return comparator id
   */
  public int getComparator() {
    return this.iComparator;
  } //########## getComparator ##########

  /**
   * @return operator id
   */
  public int getOperator() {
    return this.iOperator;
  } //########## getOperator ##########

  /**
   * 1 -> compare object's attribute to another object's attribute or static value
   * 2 -> specify IN query
   *
   * @return kind of query
   */
  public int getKindOf() {
    return this.iKindOfQuery;
  } //########## getKindOf ##########

  /**
   * Return status of brace<br/>
   *
   * @return OPENING_BRACE, CLOSING_BRACE
   */
  public int getBrace() {
    return this.iBrace;
  }

} // DBT_SearchCriteria
// ########## DBT_SearchCriteria ##########
