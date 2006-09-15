/*
 * CreateDateTime: 22.03.2005, 13:48:37
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDBMgr_Task_I.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/pbar/WDBMgr_Task_I.java $
 * $Rev: 195 $
 */
package com.gele.tools.wow.wdbearmanager.gui.helper.pbar;

import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;

/**
 * Interface for all classes, that are used in combination with
 * the ProgressBar<br/>
 * 
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 195 $
 */
public interface WDBMgr_Task_I {
  
  
/**
 * Return the number of objects to be processed
 * 
 * @return 100, 200 whatever number of objects the task is working with
 * 
 */
  public int getLengthOfTask();
  
  public String getMessage();
  
  public int getCurrent();
  
/**
 * Task is done?
 * 
 * @return true - yes
 */
  public boolean isDone();
  
/**
 * Start method
 *
 */
  public void go() throws WDBMgr_Exception ;
  
  /**
   * Return value of the task, may be any type
   *
   */
  public Object getResult();

/**
 * Maybe the Task has thrown an Excption
 * @return
 */
  public Throwable getException();

}// WDBMgr_Task_I
