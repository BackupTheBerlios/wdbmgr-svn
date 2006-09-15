/*
 * CreateDateTime: 22.03.2005, 13:48:37
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDBMgr_Caller_I.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/pbar/WDBMgr_Caller_I.java $
 * $Rev: 195 $
 */
package com.gele.tools.wow.wdbearmanager.gui.helper.pbar;


/**
 * 
 * Every method that calls the progress bar to show the
 * state of a special task must implement this interface.
 * 
 * It offers a method for the progress bar to tell the caller, that
 * the task is finished now and sets a return value (Object).
 * 
 * The return value is of type object, since the return value
 * depends heavilily on the task.
 * 
 * 
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 195 $
 */
public interface WDBMgr_Caller_I {

/**
 * 
 * @param parObj Return value, depends on the routine
 * @param parMillies number of milli seconds, the routine took time
 */
  public void taskFinished( Object parObj, long parMillies );
  public void taskError( Throwable parEx );
  

}// WDBMgr_Caller_I.java
