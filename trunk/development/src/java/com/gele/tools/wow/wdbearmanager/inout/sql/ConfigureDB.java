/*
 * Created on 01. Nov 2004
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: ConfigureDB.java 216 2006-09-10 16:55:52Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/sql/ConfigureDB.java $
 * $Rev: 216 $
 *
 */
package com.gele.tools.wow.wdbearmanager.inout.sql;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.DBConfig_I;

public class ConfigureDB implements DBConfig_I {

  // JDBC settings
  private String jdbcurl = "";

  private String jdbcdriver = "";

  private String username = "";

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(ConfigureDB.class);

  /**
   * @return Returns the jdbcdriver.
   */
  public String getJdbcdriver() {
    return this.jdbcdriver;
  }

  /**
   * @param jdbcdriver The jdbcdriver to set.
   */
  public void setJdbcdriver(String jdbcdriver) {
    this.jdbcdriver = jdbcdriver;
  }

  /**
   * @return Returns the password.
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * @param password The password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return Returns the username.
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * @param username The username to set.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  private String password = "";

  /* (non-Javadoc)
   */
  public String getGUIDateFormat() {
    return "dd.MM.yyyy";
  }

  /* (non-Javadoc)
   */
  public String getSQLDateFormat() {
    return "dd.MM.yyyy HH:mm:ss";
  }

  /* (non-Javadoc)
   */
  public String getGUITimestampFormat() {
    return "dd.MM.yyyy HH:mm:ss";
  }

  /* (non-Javadoc)
   */
  public String getSQLTimestampFormat() {
    return "dd.MM.yyyy HH:mm:ss.S";
  }

  /* (non-Javadoc)
   */
  public String getSQLTimestampFormatQuery() {
    return "to_date( '@', 'dd.mm.yyyy HH24:MI:ss')";
  }

  private Connection myConnection = null;

  /* 
   * Keep one connection for this class<br/>
   * <br/>
   */
  public Connection getConnection() {

    try {
      Class.forName(this.jdbcdriver).newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
      return null;
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return null;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }

    // Already connected to the database?
    if (this.myConnection == null) {
      try {
        this.myConnection = DriverManager.getConnection(this.jdbcurl,
            this.username, this.password);
        // Write some information to the LOG-file
        // helps debugging
        try {
          DatabaseMetaData myDBMeta = this.myConnection.getMetaData();
          myLogger.info("Database information:");
          myLogger.info("DatabaseProductName: "
              + myDBMeta.getDatabaseProductName());
          myLogger.info("DatabaseProductVersion: "
              + myDBMeta.getDatabaseProductVersion());
          myLogger.info("DriverMajorVersion: "
              + String.valueOf(myDBMeta.getDriverMajorVersion()));
          myLogger.info("DriverMinorVersion: "
              + String.valueOf(myDBMeta.getDriverMinorVersion()));
          myLogger.info("DriverName: " + myDBMeta.getDriverName());
          myLogger.info("DriverVersion: " + myDBMeta.getDriverVersion());
          myLogger.info("DB URL: " + myDBMeta.getURL());

        } catch (SQLException ex) {
          this.myLogger
              .warn("Tried to call 'getMetaData()' on the connection -> failed");
        }
      } catch (SQLException e1) {
        // Write stacktrace to LOG
        // Very important for debugging
        StringWriter mySW = new StringWriter();
        PrintWriter printWriter = new PrintWriter(mySW);
        e1.printStackTrace(printWriter);
        this.myLogger.error("Error establishing db connection:");
        this.myLogger.error(mySW.getBuffer().toString());
        //e1.printStackTrace();
        return null;
      }
    }

    return this.myConnection;
  }// getConnection

  /* (non-Javadoc)
   */
  public String getDatabaseType() {
    return null;
  }

  /* (non-Javadoc)
   */
  public String getPKCClass() {
    return "com.gele.base.dbaccess.Oracle_PrimaryKeyCreator";
  }

  /**
   * @return Returns the jdbcurl.
   */
  public String getJdbcurl() {
    return this.jdbcurl;
  }

  /**
   * @param jdbcurl The jdbcurl to set.
   */
  public void setJdbcurl(String jdbcurl) {
    this.jdbcurl = jdbcurl;
  }
}
