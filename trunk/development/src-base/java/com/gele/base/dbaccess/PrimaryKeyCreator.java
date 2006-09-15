/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: PrimaryKeyCreator.java 193 2005-10-30 16:22:47Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2005-10-30 17:22:47 +0100 (So, 30 Okt 2005) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 193 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/PrimaryKeyCreator.java $
 * $Rev: 193 $
 */

package com.gele.base.dbaccess;

/**
 * This interface defines the method that should be implemented for
 * a class that provides a new primary key for an object, that should be
 * written to the database for the first time ("insert").<br/>
 * The method returns "long" and it is up to the user of a class that
 * needs the functionality to cast it to the type really needed as primary
 * key<br/>
 *
 */

// standard packages
import java.sql.Connection;
import java.sql.SQLException;

public interface PrimaryKeyCreator {
  /**
   * Create a primary key<br/>
   *
   * @return long primary key
   * @param cp_dobDTO create a primary key for this object
   * @throws SQLException problems accessing the database
   */
  public long createPrimaryKey(DTO_Interface cp_dobDTO) throws SQLException;
  //########## createPrimaryKey ##########

  /**
   * Set the connection to access the database<br/>
   * @param cp_conDbConn JDBC database connection
   */
  public void setConnection(Connection cp_conDbConn);
  //########## setConnection ##########
  /**
  * Create a primary key using Oracle sequences<br/>
  * <br/>
  * Assumes an Oracle database and sequence tables for each table
  * inside the database<br/>
  * <br/>
  * Warning: Do not forget to call "setConnection"!
  *
  * @return long primary key
  * @param pTable retrieve a primary key for this table
  * @throws SQLException problems with the database occurred
  */
  public long createPrimaryKey(String pTable) throws SQLException;
} // PrimaryKeyCreator
//########## PrimaryKeyCreator ##########
