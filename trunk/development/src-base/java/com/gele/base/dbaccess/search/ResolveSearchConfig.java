/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: ResolveSearchConfig.java 205 2006-08-16 14:49:30Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/search/ResolveSearchConfig.java $
 * $Rev: 205 $
 */

package com.gele.base.dbaccess.search;

// standard packages
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
 * 
 * @version $Id: ResolveSearchConfig.java 205 2006-08-16 14:49:30Z gleibrock $
 */

public interface ResolveSearchConfig {

  /**
   * Creates the SQL query string out of the given objects<br/>
   * <br/>
   * Returns
   *  "<tablename1, tablename2, ...> WHERE <generated_out_of_DBT_SearchConfig><br/>
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
    Vector cp_vecSortObjs);

  /**
   * Returns the textual representation of the operator<br/>
   * <br/>
   *
   * @see DBT_SearchConfig
   *
   * @param ipOperator  identifier for the operator
   * @return String     textual representation (AND, OR, etc)
   */
  public String getOperator(int ipOperator);

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
    int ip_Comparator);

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
  public String getFirstProperty(DTO_Interface parObj);

  /**
   * Returns the textual representation of the comparator<br/>
   * <br/>
   * @param ipComparator identifier for the comparator
   * @return String      textual representation ('=', '<', '>', 'LIKE', etc)
   */
  public String getComparator(int ipComparator);

} // ########## ResolveSearchConfig ##########
