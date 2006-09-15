/*
 * Created on 13.04.2005
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: PB_WDBTXT_Export.java 218 2006-09-14 23:45:59Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-15 01:45:59 +0200 (Fr, 15 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 218 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/wdbtxt/PB_WDBTXT_Export.java $
 * $Rev: 218 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.inout.wdbtxt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.gui.helper.pbar.WDBMgr_Task_Dual_I;
import com.gele.tools.wow.wdbearmanager.helper.WDB_Helper;

/**
 * Export the whole database to WDBTXT files<br/>
 * 
 * The user selected a directory and this module will export
 * the whole database to WDBTXT format inside this directory.<br/>
 * 
 * Format is:
 *
 * <tabname>.WDBTXT
 * 
 *
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: PB_WDBTXT_Export.java 218 2006-09-14 23:45:59Z gleibrock $
 */
public class PB_WDBTXT_Export
    implements WDBMgr_Task_Dual_I {

  public static final String COMPAT_PREFIX = "# Compatibility version: ";
  
  private StringBuffer strMessage = new StringBuffer();

  private int lengthOfTask = 0;

  private int current = 0;

  private int outerLengthOfTask = 0;

  private int outerCurrent = 0;

  private boolean done = false;

  private Throwable myEx = null;

  // parameters
  private WDBearManager_I myAPI;

  private File myOutDir = null;
  
  // only entries that have this locale (eg: "deDE", "enUS", etc)
  // if the value is ALL -> ignore locale
  private String wdbLocale = "";

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(PB_WDBTXT_Export.class);

  public PB_WDBTXT_Export(WDBearManager_I parAPI, File parOutDir, String parLocale)
      throws WDBMgr_Exception {
    this.myAPI = parAPI;
    this.myOutDir = parOutDir;
    this.wdbLocale = parLocale;

    // Get list of all possible tables
    // (each WDB table has an XML config file)
    FilenameFilter filter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".xml");
      }
    };
    String[] arrWDBfiles = new File(WDBearManager.WDBEAR_CONFIG).list(filter);
    String tabName = "";
    // calculate MAX length of task (sum of all entries inside DB)
    for (int k = 0; k < arrWDBfiles.length; k++) {
      // 4 = ".xml".length
      tabName = arrWDBfiles[k].substring(0, arrWDBfiles[k].length() - 4);
      //this.strMessage.append(tabName + ": " + k + " / " + arrWDBfiles.length+"\n");
      DTO_Interface qbeDTO = this.myAPI.createDTOfromTable(tabName);
      if( this.wdbLocale.equals(WDB_Helper.INF_LOCALE_ALL) == false ) {
        qbeDTO.setColumnValue( "inf_locale", this.wdbLocale );
      }
      this.lengthOfTask += this.myAPI.getCountOf(qbeDTO);
    }
    this.outerLengthOfTask = arrWDBfiles.length;

  }// exportToWDBTXT

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getLengthOfTask()
   */
  public int getLengthOfTask() {
    return this.lengthOfTask;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getMessage()
   */
  public String getMessage() {
    return this.strMessage.toString();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getCurrent()
   */
  public int getCurrent() {
    return this.current;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#isDone()
   */
  public boolean isDone() {
    return this.done;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#go()
   */
  public void go()
      throws WDBMgr_Exception {
    // Export
    StringBuffer retValue = new StringBuffer();
    try {
      // Get list of all possible tables
      // (each WDB table has an XML config file)
      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.endsWith(".xml");
        }
      };
      String[] arrWDBfiles = new File(WDBearManager.WDBEAR_CONFIG).list(filter);
      String tabName = "";

      for (int k = 0; k < arrWDBfiles.length; k++) {
        this.outerCurrent = k;
        // 4 = ".xml".length
        tabName = arrWDBfiles[k].substring(0, arrWDBfiles[k].length() - 4);
        this.strMessage.append(tabName + ": " + (k + 1) + " / " + (arrWDBfiles.length + 1) + "\n");

        String newLine = System.getProperty("line.separator");
        DTO_Interface objDTO = this.myAPI.createDTOfromTable(tabName);
        Iterator itCols = objDTO.getColumns();
        int iColNum = objDTO.getColumnsSize();
        String[] arrColumns = new String[iColNum];
        int counter = 0;
        //String strColName = "";
        while (itCols.hasNext()) {
          arrColumns[counter] = (String) itCols.next();
          counter++;
        }

        DTO_Interface qbeDTO = this.myAPI.createDTOfromTable(tabName);
        if( this.wdbLocale.equals("ALL") == false ) {
          qbeDTO.setColumnValue( "inf_locale", this.wdbLocale );
        }
        int numOf = this.myAPI.getCountOf(qbeDTO);
        if (numOf == 0) {
          retValue.append(tabName + ": No objects inside the database\n");
          continue;
        }
        retValue.append(tabName + ": " + this.lengthOfTask + "\n");
        Collection colObjs = this.myAPI.getAllObjects(qbeDTO);

        //.out.println("Exporting to WDBTXT -> " + colObjs.size());
        Iterator itDTOs = colObjs.iterator();

        BufferedWriter bwrOut = new BufferedWriter(new FileWriter(new File(this.myOutDir, tabName
            + ".wdbtxt")));
        DTO_Interface dtoLoop = null;

        bwrOut.write("# " + new Date().toString() + newLine);
        bwrOut.write("# Done with " + WDBearManager.VERSION_INFO + newLine);
        bwrOut.write(COMPAT_PREFIX + WDBearManager.COMPATIBILITY_INFO + newLine);
        bwrOut.write("# gl, hf" + newLine);
        bwrOut.write(newLine);

        while (itDTOs.hasNext()) {
          this.current++;
          dtoLoop = (DTO_Interface) itDTOs.next();
          bwrOut.write("[" + tabName + " " + dtoLoop.getPrimaryKeyValues()[0].toString() + "]"
              + newLine);
          // Start with 1, since 0 is primary key
          //StringBuffer colContents = new StringBuffer();
          String colValue = "";
          Object objValue = null;
          for (int i = 1; i < iColNum; i++) {
            objValue = dtoLoop.getColumnValue(arrColumns[i]);
            if( objValue != null ) {
                colValue = objValue.toString();
            } else {
              colValue = "";
            }

            int indexBSlash = 0;
            if ((indexBSlash = colValue.indexOf("\n")) != -1) {
              // can be found more than just once...
              if (colValue.indexOf("\r", indexBSlash) != -1) {
                //                .out.println(tabName);
                //                .out.println("windows freak -> \\n\\r"+ colValue);
                colValue = colValue.replaceAll("\r\n", "\\$B");
                //                .out.println(colValue);
                //                colContents.append( colValue.substring(0, indexBSlash-1));
                //                colContents.append( "$B");
                //                colContents.append( colValue.substring(indexBSlash));
                //                .out.println("WF: "+ colContents.toString());
              } else {
                colValue = colValue.replaceAll("\n", "\\$B");
              }
              bwrOut.write(arrColumns[i] + "=" + colValue + newLine);
            } else {
              if( (dtoLoop.getColumnType(arrColumns[i]) == Types.CHAR) ||
                  (dtoLoop.getColumnType(arrColumns[i]) == Types.VARCHAR) ) {
                byte[] bytText = colValue.getBytes("UTF8");
                bwrOut.write(arrColumns[i] + "=" );
                bwrOut.write( new String(bytText) );
                bwrOut.write( newLine );
              }else {
                bwrOut.write(arrColumns[i] + "=" + colValue + newLine);
              }
            }
          }
          bwrOut.write(newLine);
        }// while... DTOs
        bwrOut.close();
        this.strMessage.append(tabName + ": " + numOf + "\n");
      }
    } catch (Exception ex) {
      StringWriter mySW = new StringWriter();
      PrintWriter myPW = new PrintWriter(mySW);
      ex.printStackTrace(myPW);
      myPW.close();
      this.myLogger.error(mySW.getBuffer().toString());
      this.myEx = new WDBMgr_Exception(ex.getMessage());
      return;
    }
    this.done = true;
  }// go

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getResult()
   */
  public Object getResult() {
    return new Object[] {new Long(this.lengthOfTask),
        "Files written to: " + this.myOutDir.getAbsolutePath()};
    //    "Export of objects successfull\n" + "Exported objects : "
    //        + this.lengthOfTask + "\n" + "Files written to: "
    //        + this.myOutDir.getAbsolutePath();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getException()
   */
  public Throwable getException() {
    //.out.println("TXT: getException");
    return this.myEx;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Dual_Task_I#getLenghtOfOuterLoop()
   */
  public int getLenghtOfOuterLoop() {
    return this.outerLengthOfTask;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Dual_Task_I#getCurrentOfOuterLoop()
   */
  public int getCurrentOfOuterLoop() {
    return this.outerCurrent;
  }
  
  public static void writeWDBTXT( BufferedWriter parBWr, DTO_Interface parDTO ) throws Exception {
    String newLine = System.getProperty("line.separator");

    parBWr.write("[" + parDTO.getTableName() + " " + parDTO.getPrimaryKeyValues()[0].toString() + "]"
        + newLine);
    // Start with 1, since 0 is primary key
    //StringBuffer colContents = new StringBuffer();
    String colValue = "";
    Object objValue = null;
    int iColNum = parDTO.getColumnsSize();
    String[] arrColumns = new String[iColNum];
    int counter = 0;
    Iterator itCols = parDTO.getColumns();
    while (itCols.hasNext()) {
      arrColumns[counter] = (String) itCols.next();
      counter++;
    }

    for (int i = 1; i < iColNum; i++) {
      objValue = parDTO.getColumnValue(arrColumns[i]);
      if( objValue != null ) {
          colValue = objValue.toString();
      } else {
        colValue = "";
      }

      int indexBSlash = 0;
      if ((indexBSlash = colValue.indexOf("\n")) != -1) {
        // can be found more than just once...
        if (colValue.indexOf("\r", indexBSlash) != -1) {
          //                .out.println(tabName);
          //                .out.println("windows freak -> \\n\\r"+ colValue);
          colValue = colValue.replaceAll("\r\n", "\\$B");
          //                .out.println(colValue);
          //                colContents.append( colValue.substring(0, indexBSlash-1));
          //                colContents.append( "$B");
          //                colContents.append( colValue.substring(indexBSlash));
          //                .out.println("WF: "+ colContents.toString());
        } else {
          colValue = colValue.replaceAll("\n", "\\$B");
        }
        parBWr.write(arrColumns[i] + "=" + colValue + newLine);
      } else {
        if( (parDTO.getColumnType(arrColumns[i]) == Types.CHAR) ||
            (parDTO.getColumnType(arrColumns[i]) == Types.VARCHAR) ) {
          byte[] bytText = colValue.getBytes("UTF8");
          parBWr.write(arrColumns[i] + "=" );
          parBWr.write( new String(bytText) );
          parBWr.write( newLine );
        }else {
          parBWr.write(arrColumns[i] + "=" + colValue + newLine);
        }
      }
    }
    parBWr.write(newLine);
  }// writeWDBTXT


}// PB_WDBTXT_Export
