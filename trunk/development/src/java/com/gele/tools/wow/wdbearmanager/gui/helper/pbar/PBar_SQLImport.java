/*
 * Created on 08.04.2005
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: PBar_SQLImport.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/pbar/PBar_SQLImport.java $
 * $Rev: 195 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.gui.helper.pbar;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_MissingConfigParameterException;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;

/**
 * Import WDB data to the database<br/>
 * 
 *
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: PBar_SQLImport.java 195 2005-10-30 16:44:25Z gleibrock $
 */
public class PBar_SQLImport implements WDBMgr_Task_I {
  // Logging with Log4J
  Logger myLogger = Logger.getLogger(PBar_SQLImport.class);

  private Collection colDTOs = null;

  private int currPos = 0;

  private WDBearManager_I myWoWWDBManager_API = null;

  private boolean updateFlag = false;

  private ObjectsWritten retOW = new ObjectsWritten();
  
  private Object objNotify = null;
  
  private Throwable myEx = null;

  public PBar_SQLImport(WDBearManager_I parWoWWDBManager_API, Object parNotify) {
    this.myWoWWDBManager_API = parWoWWDBManager_API;
    this.objNotify = parNotify;
  }

  public void setObjects(Collection parCollection, boolean parUpdate) {
    this.colDTOs = parCollection;
    this.updateFlag = parUpdate;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getLengthOfTask()
   */
  public int getLengthOfTask() {
    return this.colDTOs.size();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getMessage()
   */
  public String getMessage() {
    return null;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getCurrent()
   */
  public int getCurrent() {
    return this.currPos;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#isDone()
   */
  public boolean isDone() {
    if (this.colDTOs == null) {
      return true;
    }

    if (this.currPos == this.colDTOs.size()) {
      return true;
    }
    return false;
  }

  public ObjectsWritten getStatistics() {
    return this.retOW;
  }
  
  private ObjectsWritten retVal = null;
  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#go()
   */
  public void go() throws WDBMgr_MissingConfigParameterException,
      WDBMgr_Exception {
    this.currPos = 0;
    Iterator itrDTOs = this.colDTOs.iterator();
    this.retVal = null;

    //this.myLogger.info("sqlimport -> go");
    this.retOW = new ObjectsWritten();
    while (itrDTOs.hasNext()) {
      this.currPos++;

      DTO_Interface loopDTO = (DTO_Interface) itrDTOs.next();

      try {
        this.retVal = this.myWoWWDBManager_API.insertOrUpdateToSQLDB(loopDTO,
            this.updateFlag);
        //      	Thread.sleep(0);
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      this.retOW.setNumErrorInsert(this.retOW.getNumErrorInsert()
          + this.retVal.getNumErrorInsert());
      this.retOW.setNumErrorUpdate(this.retOW.getNumErrorUpdate()
          + this.retVal.getNumErrorUpdate());
      this.retOW
          .setNumInsert(this.retOW.getNumInsert() + this.retVal.getNumInsert());
      this.retOW.setNumSkipped(this.retOW.getNumSkipped()
          + this.retVal.getNumSkipped());
      this.retOW
          .setNumUpdate(this.retOW.getNumUpdate() + this.retVal.getNumUpdate());
    }
    //this.myLogger.info("sqlimport -> go // end");

  }// go
  
  
  public Object getResult() {
    return this.retOW;
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getException()
   */
  public Throwable getException() {
    return this.myEx;
  }
  

}// PBar_SQLImport.java