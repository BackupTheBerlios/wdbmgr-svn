/*
 * Created on 13.04.2005
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: DBConfig_I.java 183 2005-10-20 18:37:27Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2005-04-07 19:52:19 +0200 (Do, 07 Apr 2005) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 159 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/DB_Generic_DAO.java $
 * $Rev: 159 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.inout.wdb;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoXMLConfigFileForThisWDBException;
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
 * @author    $Author:$ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id:$
 */
public class PB_WDB_ImportRecursive implements WDBMgr_Task_I {

  private int lengthOfTask = 0;

  private int current = 0;

  private StringBuffer strMessage = new StringBuffer();

  private Throwable myEx = null;

  private boolean done = false;

  private ObjectsWritten objWritten = new ObjectsWritten();

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(PB_WDB_ImportRecursive.class);

  private File startDir = null;

  private Object[] myFiles = null;

  private WDBearManager_I myAPI;

  private boolean doUpdate = false;

  /**
   * Import one or more WDB files into the database<br/>
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
  public PB_WDB_ImportRecursive(WDBearManager_I parAPI, File parStartDir,
      boolean parUpdate) {

    this.startDir = parStartDir;
    this.myAPI = parAPI;
    this.doUpdate = parUpdate;

    // Create list of all files to be processed
    Collection colFiles = new Vector();
    this.createListOfFiles(colFiles, this.startDir);
    Object[] testObj = colFiles.toArray();
    myFiles = colFiles.toArray();

    //    for (int i = 0; i < myFiles.length; i++) {
    //      .out.println(myFiles[i]);
    //    }

    // Calculate length of the files
    this.lengthOfTask = myFiles.length * 2;

  }// PB_WDB_ImportRecursive

  private void createListOfFiles(Collection parColFiles, File parStartDir) {
    File[] filList = parStartDir.listFiles();

    for (int i = 0; i < filList.length; i++) {
      if (filList[i].isDirectory()) {
        createListOfFiles(parColFiles, filList[i]);
      } else {
        parColFiles.add(filList[i]);
      }
    }// for... files

  }// createListOfFiles

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

      String tabName = ((File) this.myFiles[j]).getName();
      tabName = tabName.substring(0, tabName.length() - ".wdb".length());

      this.strMessage.append("Processing '" + tabName + "' " + (j + 1) + "/"
          + (this.myFiles.length + 1) + "\n");

      // Import
      this.strMessage.append("Reading WDB file\n");
      Collection wdbItems = null;
      try {
        File impFile = (File) this.myFiles[j];
        String fPath = (impFile).getAbsolutePath();
        // ZIP support
        /*
        if (fPath.endsWith(".zip")) {
          try {
            // Open the ZIP file
            ZipFile zf = new ZipFile(fPath);

            // Enumerate each entry
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
              // Get the entry name
              String zipEntryName = ((ZipEntry) entries.nextElement())
                  .getName();
System.out.println(zipEntryName);
            }
          } catch (IOException e) {
            // do nothing
          }
        } else
*/
        {
          wdbItems = this.myAPI.readWDBfile(fPath);
        }
        this.current++;

        this.strMessage.append("Insert into DB ( " + wdbItems.size()
            + " objects)\n");
        if (wdbItems.size() != 0) {
          ObjectsWritten tmpOW = this.myAPI.insertOrUpdateToSQLDB(wdbItems,
              this.doUpdate);
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
          // move file to folder "imported"
          String startPath = startDir.getAbsolutePath();
          // Get directory of the file
          String dirOfCurrFile = impFile.getParent().substring(startPath.length());
          File importedDir2 = new File( startDir, "_imported"+dirOfCurrFile );
          importedDir2.mkdirs();
          impFile.renameTo(new File(importedDir2, impFile.getName()));
        }
        this.current++;
      } catch (WDBMgr_NoXMLConfigFileForThisWDBException ex) {
        this.myLogger.info("No supported WDB file: "
            + ((File) this.myFiles[j]).getAbsolutePath());
      } catch (Throwable thr) {
        this.myLogger.info("Runtime Exception: "
            + ((File) this.myFiles[j]).getAbsolutePath());
      }
      //this.current += counter;
      //.out.println("current: "+ this.current);

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