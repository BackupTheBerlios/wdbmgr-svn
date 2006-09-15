/*
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: DBConfig_I.java 205 2006-08-16 14:49:30Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/DBConfig_I.java $
 * $Rev: 205 $
 */

package com.gele.base.dbaccess;

import java.sql.Connection;

/**
 * This interface describes the configuration for a DAO<br/>
 * <br/>
 * DAO - Data Access Object<br/>
 * A DAO is a interface, that encapsulates all access to a storage (eg.
 * SQL database, filesystem, ERP, etc etc).<br/>
 * The user of a DAO just uses the given methods and doesn't have
 * to cope with SQL statement, file sytem handels, sockets, etc.<br/>
 * <br/>
 * The access to a SQL based data container is maintained using this
 * interface.<br/> 
 *
 *
 * @author <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 */
public interface DBConfig_I {

  /**
   * Dateformat the user sends<br/>
   * If the DAO receives a DTO that has a column of type DATE/TIMESTAMP
   * then it looks up this format to "know" the syntax.
   * 
   * @return Java Dateformat
   */
  public String getGUIDateFormat();
  
/**
 * This is how the SQL database wants a date to be<br/>
 * This depends on the datasource one is using.<br/>
 * And it only applies, if one uses a String coded Date,
 * recommended is *always* to use a PreparedStatement and a
 * setDate()<br/>
 * 
 * @return format of the SQL date
 */
  public String getSQLDateFormat();
  
  /**
   * User set a Timestamp (as String) inside a DTO<br/>
   * 
   * @return Java Dateformat 
   */
  
  public String getGUITimestampFormat();
  
/**
 * This is how the database "wants" a Timestamp in String representation
 * 
 * @return sql timestamp format, *very* database and country dependend...
 */
  public String getSQLTimestampFormat();
  
  public String getSQLTimestampFormatQuery();
  
/**
 * java.sql.Connection
 * 
 * For each instance of this class -> only one connection
 * 
 * This means every call to "getConnection" will return the *same*
 * Connection object.
 * 
 * This saves massively ressources, but has some other severe drawbacks!
 * 
 * @return instance of java.sql.Connection
 */
  public Connection getConnection();
  
  public String getDatabaseType();
  
/**
 * Class that creates a unique primary key<br/>
 * 
 * Needed, since almost every database has its own mechanism
 * to provide a unique numeric value (Autonum, Sequence...)
 * 
 * @return fully qualified classname of the primary key creator
 */
  public String getPKCClass();
  
}//DBConfig_I
