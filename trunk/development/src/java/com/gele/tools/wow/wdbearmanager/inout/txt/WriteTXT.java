/*
 * Created on 14.03.2005
 * 
 * 
 * Write TXT file in "KEY = VALUE", like a INI, properties file.
 * The column is used as key, the column's value as value.<br/>
 * <br/>
 * 
 * 
 * Write the contents of the WDB file as text using the description
 * from the XML file (stored inside the DTOs)
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WriteTXT.java 195 2005-10-30 16:44:25Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2005-10-30 17:44:25 +0100 (So, 30 Okt 2005) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 195 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/txt/WriteTXT.java $
 * $Rev: 195 $
 *
 */
package com.gele.tools.wow.wdbearmanager.inout.txt;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorWritingTXTFile;

/**
 * 
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: WriteTXT.java 195 2005-10-30 16:44:25Z gleibrock $
 *
 */
public class WriteTXT {
  // Logging with Log4J
  static Logger myLogger = Logger.getLogger(WriteTXT.class);
  /**
   * Use the info in the DTOs and write a TXT file<br/>
   * <br>/
   * All existing files are overwritten without warning!
   * 
   * @param parOutputDir write here, name is taken from the WDB with ".txt"
   * @param parItems Collection containing the DTO instances
   * @return message for the user, can be ignored
   */
  public static String writeTXT(File parOutputDir, Collection parItems)
      throws WDBMgr_ErrorWritingTXTFile {
    // Depending on the choosen file (WDB) export the values

    // Determine name of export file
    Iterator itWDBS = parItems.iterator();
    DTO_Interface myDTO = null;
    if (itWDBS.hasNext()) {
      myDTO = (DTO_Interface) itWDBS.next();
    }
    String table = myDTO.getTableName();

    File txtFile = new File(parOutputDir, table + ".txt");
    itWDBS = parItems.iterator();
    try {
      FileWriter myFW = new FileWriter(txtFile);
      myFW.write(" WDB-File: " + table);
      myFW.write(System.getProperty("line.separator"));
      myFW.write("Created using WDBManager by kizura");
      myFW.write(System.getProperty("line.separator"));
      myFW.write(WDBearManager.VERSION_INFO);
      myFW.write(System.getProperty("line.separator"));
      myFW.write(System.getProperty("line.separator"));

      while (itWDBS.hasNext()) {
        myDTO = (DTO_Interface) itWDBS.next();
        Iterator itCols = myDTO.getColumns();
        while (itCols.hasNext()) {
          String colName = (String) itCols.next();
          myFW.write(colName + " = ");
          int type = myDTO.getColumnType(colName);
          switch (type) {
          case java.sql.Types.VARCHAR:
            myFW.write("\"" + myDTO.getColumnValue(colName) + "\"");
            break;
          case java.sql.Types.SMALLINT:
          case java.sql.Types.TINYINT:
          case java.sql.Types.INTEGER:
            myFW.write(myDTO.getColumnValue(colName).toString());
            break;
          case java.sql.Types.FLOAT:
            myFW.write((String) myDTO.getColumnValue(colName));
            break;
          case java.sql.Types.NUMERIC:
            // hex value
            myFW.write((String) myDTO.getColumnValue(colName));
            break;
          default:
            myLogger.info("WriteTXT: Unknown Type for Column: '" + colName
                + "' - should never happen");
            break;
          }// switch

          if (itCols.hasNext()) {
            myFW.write(System.getProperty("line.separator"));
          }
        }// loop... cols
        myFW.write(System.getProperty("line.separator"));
        myFW.write(System.getProperty("line.separator"));
      }// while... DTOs
      myFW.close();

      return "TXT Export successfull\n" + "File '" + txtFile.getAbsolutePath()
          + "' created";

    } catch (Exception ex) {
      //ex.printStackTrace();
      String msg = "Error creating the TXT file\n" + "Could not create file '"
          + txtFile.getAbsolutePath() + "' ";
      throw new WDBMgr_ErrorWritingTXTFile(msg, ex);
    }

  }// writeTXT
}// WriteTXT.java
