/*
 * Created on 07.04.2005
 * 
 * This program questions the database:
 * 
 * select wdb_params.wdb_key from wdb_params where 
 * 				wdb_params.wdb_param == "DBVERSION"
 * -> Looks in directory "db-update" if update scripts are available
 * -> Executes them
 * -> If nothing available -> will return
 * -> else start from beginning
 * 
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: DBUpdater.java 216 2006-09-10 16:55:52Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/helper/DBUpdater.java $
 * $Rev: 216 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.helper;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoDataAvailableException;
import com.gele.utils.ReadPropertiesFile;

/**
 * This class updates the DB schema according to new versions<br/>
 * 
 * Please refer to folder "db-update" for the scripts<br/>
 *
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: DBUpdater.java 216 2006-09-10 16:55:52Z gleibrock $
 */
public class DBUpdater {

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(DBUpdater.class);

  // Save ressources -> only one interp
  PythonInterpreter interp = null;

  public void checkForUpdate(WDBearManager_I myAPI) {

    //
    // Check database version
    //

    // Update, till no script is found
    while (true) {
      try {
        DTO_Interface appDTO = myAPI.createDTOfromTable("wdb_params");
        appDTO.setColumnValue("wdb_param", "DBVERSION");

        DTO_Interface versionDTO = null;
        try {
          Collection retVal = myAPI.getAllObjects(appDTO);
          versionDTO = (DTO_Interface) retVal.iterator().next();
        } catch (WDBMgr_NoDataAvailableException ex) {
          // nothing found
        }
        if (versionDTO == null) {
          // no version info available
          // This means PRE-8 or earlier!
          this.updateDB("pre-8", myAPI);
          // -> sets dbversion to 0.9
        } else {
          this.myLogger.debug("Current DB version: "
              + (String) versionDTO.getColumnValue("wdb_value"));
          if (this.updateDB((String) versionDTO.getColumnValue("wdb_value"),
              myAPI) == false) {
            // no update script found
            this.myLogger.debug("DB scheme is up to date, no update script");
            break;
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }// checkForUpdate

  private boolean updateDB(String parVersion, WDBearManager_I myAPI) {
    try {
      Properties dbProps = null;
      String filName = "db-update/" + parVersion + "_dbpatch.properties";
      try {
        dbProps = ReadPropertiesFile.readProperties(filName);
      } catch (Exception ex) {
        return false;
        //
        //        System. err.println("Fatal error: Could not open DB update scripts: "
        //            + filName);
        //        System.exit(0);
      }
      this.myLogger.info("**");
      this.myLogger.info("IMPORTANT: Updating database");
      this.myLogger.info("**");
      // got the properties
      String sqlString = "";
      String msgString = "";
      for (int i = 1; i < 100; i++) {
        sqlString = dbProps.getProperty("sql" + i);
        msgString = dbProps.getProperty("msg" + i);
        if (sqlString != null) {
          if (msgString != null) {
            this.myLogger.info(msgString);
            StringBuffer sbuSTMT = new StringBuffer();
            // Call to API
            if (sqlString.startsWith("@")) {
              if (sqlString.startsWith("@scr:") == true) {
                String strAPIcall = sqlString.substring("@scr:".length(),
                    sqlString.length());
                //.out.println(strAPIcall);
                if (this.interp == null) {
                  this.interp = new PythonInterpreter();
                }
                this.interp.set("wdbmgrapi", myAPI);
                this.interp.exec(strAPIcall);
                continue;
              } else if (sqlString.startsWith("@file:") == true) {
                String strFileCall = sqlString.substring("@file:".length(),
                    sqlString.length());
                strFileCall = strFileCall.trim();
                //.out.println(strFileCall);
                if (this.interp == null) {
                  this.interp = new PythonInterpreter();
                }
                this.interp.set("wdbmgrapi", myAPI);
                this.interp.execfile("db-update/"+strFileCall);
                continue;
              } else {
                this.myLogger.error("Syntax error in SQL update script: "
                    + filName);
                this.myLogger.error("Line: " + sqlString);
                this.myLogger.error("Line must start with '@scr:' or '@file:'");
                System.exit(0);
              }

              //							myAPI
            } else {
              // replace {table}
              String[] splt = sqlString.split(" ");
              for (int k = 0; k < splt.length; k++) {
                //this.myLogger.info(splt[k]);

                // User specified "{TABNAME}
                // -> replace with the correct prefix
                if (splt[k].startsWith("{")) {
                  if (splt[k].endsWith("}") == false) {
                    this.myLogger.error("Syntax error in SQL update script: "
                        + filName);
                    this.myLogger.error("'" + "sql" + i
                        + "': Missing '}' for '" + splt[k] + "'");
                    System.exit(0);
                  }
                  String tabName = splt[k].substring(1, splt[k].length() - 1);
                  //this.myLogger.info(tabName);
                  tabName = myAPI.getDBPrefix() + tabName;
                  //this.myLogger.info(tabName);
                  sbuSTMT.append(tabName);
                  sbuSTMT.append(" ");
                } else {
                  sbuSTMT.append(splt[k]);
                  sbuSTMT.append(" ");
                }

              }// for...parsing string
            }
            // Send string to database
            //this.myLogger.info("SQL-String: " + sbuSTMT.toString());
            Connection myConn = myAPI.getConnection();
            Statement sbStatement = myConn.createStatement();
            int itRetValue = sbStatement.executeUpdate(sbuSTMT.toString());
            sbStatement.close();
          } else {
            this.myLogger.error("Found property '" + "sql" + i
                + "', but no msg key");
            System.exit(0);
          }
        } else {
          // No script
          break;
        }
      }
    } catch (Exception ex) {
      this.myLogger.error("Fatal Error: ");
      this.myLogger.error("Exception occurred while trying to patch database");
      ex.printStackTrace();
      System.exit(0);
    }
    // update script found
    return true;
  }//updateDB}// DBUpdater
}// DBUpdater
