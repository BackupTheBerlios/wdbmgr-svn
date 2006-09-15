/*
 * Created on 14.03.2005
 *
 */
package com.gele.tools.wow.wdbearmanager.inout.csv;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.Iterator;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorWritingCSVFile;

/**
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WriteCSV.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/csv/WriteCSV.java $
 * $Rev: 195 $
 */
public class WriteCSV {
  /**
   * Use the info in the DTOs and write a CSV file<br/>
   * <br>/
   * All existing files are overwritten without warning!
   * 
   * @param parOutputDir write here, name is taken from the WDB with ".csv"
   * @param parItems Collection containing the DTO instances
   * @return message for the user, can be ignored
   */
  public static String writeCSV(File parOutputDir, Collection parItems) 
  throws WDBMgr_ErrorWritingCSVFile
  {
    // Depending on the choosen file (WDB) export the values

    // Determine name of export file
    Iterator itWDBS = parItems.iterator();
    DTO_Interface myDTO = null;
    if (itWDBS.hasNext()) {
      myDTO = (DTO_Interface) itWDBS.next();
    }
    String table = myDTO.getTableName();

    File csvFile = new File(parOutputDir, table + ".csv");
    itWDBS = parItems.iterator();
    try {
      FileWriter myFW = new FileWriter(csvFile);
      while (itWDBS.hasNext()) {
        myDTO = (DTO_Interface) itWDBS.next();
        Iterator itCols = myDTO.getColumns();
        while (itCols.hasNext()) {
          String colName = (String) itCols.next();
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
          }// switch

          if (itCols.hasNext()) {
            myFW.write(",");
          }
        }// loop... cols
        myFW.write(System.getProperty("line.separator"));
      }// while... DTOs
      myFW.close();

      return "CSV Export successfull\n"
          + "File '" + csvFile.getAbsolutePath() + "' created";
          
    } catch (Exception ex) {
ex.printStackTrace();
      String msg = 
        "Error creating the CSV file\n" + "Could not create file '"
        + csvFile.getAbsolutePath() + "' ";
      throw new WDBMgr_ErrorWritingCSVFile( msg, ex);
    }

  }// writeCSV
}// WriteCSV.java
