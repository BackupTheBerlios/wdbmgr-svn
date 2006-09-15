/*
 * CreateDateTime: 22.03.2005, 13:48:37
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDBMgr_Task_Dual_I.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/pbar/WDBMgr_Task_Dual_I.java $
 * $Rev: 195 $
 */
package com.gele.tools.wow.wdbearmanager.gui.helper.pbar;


/**
 * Interface for all classes, that are used in combination with
 * the ProgressBar<br/>
 * 
 * This interface allows a hierarchy, meaning not only to display the
 * overall progress, but also which steps are finished.<br/>
 * 
 * eg:
 * 
 * for( all_tables )
 *   read_contents
 *   for( content ) 
 *     do...
 * 
 * It would be nice, if one has 2 progress bars:
 * One for the outer loop and one for the inner loop
 * 
 * This interface allows this.   
 * 
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 195 $
 */
public interface WDBMgr_Task_Dual_I extends WDBMgr_Task_I {
  
  public int getLenghtOfOuterLoop();
  public int getCurrentOfOuterLoop();
  
}// WDBMgr_Task_I
