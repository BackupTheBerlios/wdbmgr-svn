/*
 * Created on 08.04.2005
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: HelperThread.java 216 2006-09-10 16:55:52Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/pbar/HelperThread.java $
 * $Rev: 216 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.gui.helper.pbar;

import org.apache.log4j.Logger;

import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;

/**
 * 
 *
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: HelperThread.java 216 2006-09-10 16:55:52Z gleibrock $
 */
public class HelperThread extends Thread implements WDBMgr_Task_I {
  // Logging with Log4J
  Logger myLogger = Logger.getLogger(HelperThread.class);

  protected WDBMgr_Task_I myWDBMgr_Task_I;

  private Exception myEx = null;
  
  

  public HelperThread(WDBMgr_Task_I parTask) {
    this.myWDBMgr_Task_I = parTask;
  }

  public void run() {
    //this.myLogger.info("helper thread - run");
    try {
      this.myWDBMgr_Task_I.go();
    } catch (Exception ex) {
      System.err.println("HelperThread - Exception inside 'run'");
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
}