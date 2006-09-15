/*
 * Created on 08.04.2005
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: HelperThread_Dual.java 197 2005-10-30 18:16:31Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2005-10-30 19:16:31 +0100 (So, 30 Okt 2005) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 197 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/pbar/HelperThread_Dual.java $
 * $Rev: 197 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.gui.helper.pbar;

import org.apache.log4j.Logger;

import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;

/**
 * 
 *
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: HelperThread_Dual.java 197 2005-10-30 18:16:31Z gleibrock $
 */
public class HelperThread_Dual extends Thread implements WDBMgr_Task_Dual_I {
  
  // Logging with Log4J
  Logger myLogger = Logger.getLogger(HelperThread.class);

  protected WDBMgr_Task_Dual_I myWDBMgr_Task_I;

  private Exception myEx = null;
  
  

  public HelperThread_Dual(WDBMgr_Task_Dual_I parTask) {
    this.myWDBMgr_Task_I = parTask;
  }

  public void run() {
    //this.myLogger.info("helper thread - run");
    try {
      this.myWDBMgr_Task_I.go();
    } catch (Exception ex) {
      this.myEx = ex;
      ex.printStackTrace();
    }
  }// run

  /**
   * Maybe an Exception was thrown -> this method returns it. 
   * 
   * @return
   */
  public Throwable getException() {
    return  this.myWDBMgr_Task_I.getException();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getLengthOfTask()
   */
  public int getLengthOfTask() {
    return this.myWDBMgr_Task_I.getLengthOfTask();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getMessage()
   */
  public String getMessage() {
    return this.myWDBMgr_Task_I.getMessage();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#getCurrent()
   */
  public int getCurrent() {
    return this.myWDBMgr_Task_I.getCurrent();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#isDone()
   */
  public boolean isDone() {
    return this.myWDBMgr_Task_I.isDone();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.WDBMgr_Task_I#go()
   */
  public void go() throws WDBMgr_Exception {
  }

  public Object getResult() {
    return this.myWDBMgr_Task_I.getResult();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Dual_Task_I#getLenghtOfOuterLoop()
   */
  public int getLenghtOfOuterLoop() {
    return this.myWDBMgr_Task_I.getLenghtOfOuterLoop();
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Dual_Task_I#getCurrentOfOuterLoop()
   */
  public int getCurrentOfOuterLoop() {
    return this.myWDBMgr_Task_I.getCurrentOfOuterLoop();
  }
}