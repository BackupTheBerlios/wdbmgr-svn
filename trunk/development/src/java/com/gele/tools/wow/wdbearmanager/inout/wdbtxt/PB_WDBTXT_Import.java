/*
 * Created on 13.04.2005
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: PB_WDBTXT_Import.java 218 2006-09-14 23:45:59Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/wdbtxt/PB_WDBTXT_Import.java $
 * $Rev: 218 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.inout.wdbtxt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Types;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_FileNotFound;
import com.gele.tools.wow.wdbearmanager.gui.helper.pbar.WDBMgr_Task_I;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;

/**
 * Import WDBTXT files to the database<br/>
 * 
 * The user can export the database and send it to another
 * user, who can import it. Or just to make a backup of all data. 
 * 
 * 
 * Format is:
 *
 * <tabname>.WDBTXT
 * 
 *
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: PB_WDBTXT_Import.java 218 2006-09-14 23:45:59Z gleibrock $
 */
public class PB_WDBTXT_Import implements WDBMgr_Task_I {

  private int lengthOfTask = 0;

  private int current = 0;

  private StringBuffer strMessage = new StringBuffer();

  private Throwable myEx = null;
  
  private boolean done = false;

  private ObjectsWritten objWritten = new ObjectsWritten();

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(PB_WDBTXT_Import.class);

  private File[] myFiles = null;

  private WDBearManager_I myAPI;
  
  private boolean doUpdate = false;

  /**
   * Import one or more WDBTXT files into the database<br/>
   * 
   * Only accepts files, with a name, that is equal to the name
   * of the original WDB file:
   * 
   * questcache.wdb <-> questcache.wdbtxt
   * 
   * @param parAPI
   * @param parFile Import this file
   * @return
   * @throws WDBMgr_Exception
   */
  public PB_WDBTXT_Import(WDBearManager_I parAPI, File[] parFiles, boolean parUpdate ) {

    this.myFiles = parFiles;
    this.myAPI = parAPI;
    this.doUpdate = parUpdate;

    // Calculate length of the files
    FileReader myFR = null;
    int filLength = 0;
    for (int i = 0; i < parFiles.length; i++) {
      filLength += parFiles[i].length();
    }
    this.lengthOfTask = filLength;

  }// importWDBTXT

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Task_I#getLengthOfTask()
   */
  public int getLengthOfTask() {
    return this.lengthOfTask;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Task_I#getMessage()
   */
  public String getMessage() {
    return this.strMessage.toString();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Task_I#getCurrent()
   */
  public int getCurrent() {
    return this.current;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Task_I#isDone()
   */
  public boolean isDone() {
    return this.done;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Task_I#go()
   */
  public void go() throws WDBMgr_Exception {

    for (int j = 0; j < this.myFiles.length; j++) {

      String tabName = this.myFiles[j].getName();
      tabName = tabName.substring(0, tabName.length() - ".wdbtxt".length());

      // Import
      DTO_Interface objDTO = this.myAPI.createDTOfromTable(tabName);
      Iterator itCols = objDTO.getColumns();
      int iColNum = objDTO.getColumnsSize();
      String[] arrColumns = new String[iColNum];
      int counter = 0;
      String strColName = "";
      while (itCols.hasNext()) {
        arrColumns[counter] = (String) itCols.next();
        counter++;
      }

//      RandomAccessFile burIn = null;
      BufferedReader burIn = null;
      try {
        burIn = new BufferedReader(new InputStreamReader(
            new FileInputStream(this.myFiles[j]),
            "UTF8"));
//        burIn = new RandomAccessFile(this.myFiles[j], "r");
      } catch (FileNotFoundException ex) {
        throw new WDBMgr_FileNotFound("Could not read: "
            + this.myFiles[j].getAbsolutePath(), ex);
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      DTO_Interface dtoLoop = null;
      String readLine = "";

      String strValue = "";
      Vector vecWriteDTOs = new Vector();
      try {
        counter = 0;
        String pKey = "";
        int savCurrent = this.current;
         
        this.strMessage.append( "Reading file for '"+tabName+"'\n");

        String[] pKeys = objDTO.getPrimaryKeyColumns();
        while ((readLine = burIn.readLine()) != null) {
          this.current = this.current + savCurrent +(int) readLine.length();
          counter++;

          dtoLoop = objDTO.createObject();
          if (readLine.startsWith("[" + tabName)) {
            pKey = (readLine.substring(("[" + tabName).length(), readLine
                .length() - 1)).trim();
            dtoLoop.setColumnValue(pKeys[0], new Integer(pKey));

            // Start with 1, since 0 is primary key
            for (int i = 1; i < iColNum; i++) {
              readLine = burIn.readLine();
              if (readLine == null) {
                System.err.println("Premature end of file");
                System.exit(0);
              }
              // check for valid line
              // (Line must start with the name of the column)
              while (readLine.startsWith(arrColumns[i] + "=") == false) {
                String errMsg = "ID: "+ pKey +"\n";
                errMsg += readLine +"\n";
                errMsg += "MARKER: " + tabName+"\n";
                errMsg += "MARKERID: " + strValue+"\n";
                errMsg += "Wrong Format: Expected " + arrColumns[i]+ "=";
                
                this.myLogger.warn("Importing from WDBTXT");
                this.myLogger.warn(errMsg);
                
                System.err.println(errMsg);
                System.err.println("Skipping this WDBTXT value");

                // Move to next column from the database
                // -> 
                if( i < (iColNum-1)  ) {
                  i++;
                } else {
                  System.err.println( "Version mismatch - Could not import the WDBTXT" );
                  System.err.println( "Exiting - Did not find a matching column");
                  System.exit(0);
                }
              }
              // ok, set the attribute
              //.out.println(readLine);
              strValue = (readLine.substring((arrColumns[i] + "=").length(),
                  readLine.length())).trim();
              if( (dtoLoop.getColumnType(arrColumns[i]) == Types.CHAR) ||
                  (dtoLoop.getColumnType(arrColumns[i]) == Types.VARCHAR) ) {
                strValue = (readLine.substring((arrColumns[i] + "=").length(),
                    readLine.length())).trim();
                dtoLoop.setColumnValue(arrColumns[i], strValue);
              }else
              {
                if( strValue.length() == 0 ) {
                  strValue = null;
                }
                dtoLoop.setColumnValue(arrColumns[i], strValue);
              }
              // store it
            }
            vecWriteDTOs.addElement(dtoLoop);

          } else {
            // ignore
          }
        }// while... readLine
      } catch (IOException ex) {
        StringWriter mySW = new StringWriter();
        PrintWriter myPW = new PrintWriter(mySW);
        ex.printStackTrace(myPW);
        myPW.close();
        this.myLogger.error(mySW.getBuffer().toString());
        this.myEx = new WDBMgr_Exception(ex.getMessage());
        return;
        //throw new WDBMgr_IOException("Error accessing file: "+ parFile.getAbsolutePath(), ex); 
      }
      //.out.println("Anzahl gelesen aus WDBTXT: " + vecWriteDTOs.size());

      this.strMessage.append( "Insert into DB\n");
      ObjectsWritten tmpOW = this.myAPI.insertOrUpdateToSQLDB(vecWriteDTOs,
          this.doUpdate);
      //this.current += counter;
      //.out.println("current: "+ this.current);

      this.objWritten.setNumErrorInsert(this.objWritten.getNumErrorInsert()
          + tmpOW.getNumErrorInsert());
      this.objWritten.setNumErrorUpdate(this.objWritten.getNumErrorUpdate()
          + tmpOW.getNumErrorUpdate());
      this.objWritten.setNumInsert(this.objWritten.getNumInsert()
          + tmpOW.getNumInsert());
      this.objWritten.setNumSkipped(this.objWritten.getNumSkipped()
          + tmpOW.getNumSkipped());
      this.objWritten.setNumUpdate(this.objWritten.getNumUpdate()
          + tmpOW.getNumUpdate());

      //    .out.println("error insert: " + objWritten.getNumErrorInsert());
      //    .out.println("      insert: " + objWritten.getNumInsert());
      //    .out.println("skipped     : " + objWritten.getNumSkipped());
      //    .out.println("update      : " + objWritten.getNumUpdate());
    }// for... all files
    this.done = true;

  }// go

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Task_I#getResult()
   */
  public Object getResult() {
    return this.objWritten;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Task_I#getException()
   */
  public Throwable getException() {
    return this.myEx;
  }
}