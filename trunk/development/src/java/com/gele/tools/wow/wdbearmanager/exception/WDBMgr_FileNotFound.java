package com.gele.tools.wow.wdbearmanager.exception;



/**
 * An error occurred while trying to read the config file<br/>
 * <br/>
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDBMgr_FileNotFound.java 201 2006-04-10 18:50:37Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-04-10 20:50:37 +0200 (Mo, 10 Apr 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 201 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/exception/WDBMgr_FileNotFound.java $
 * $Rev: 201 $
 */

public class WDBMgr_FileNotFound extends WDBMgr_Exception{

  /**
	 * 
	 */
	private static final long serialVersionUID = -1588075419496038288L;

public WDBMgr_FileNotFound() {
    super();
  }

  public WDBMgr_FileNotFound(String msg) {
    super(msg);
  }

  public WDBMgr_FileNotFound(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
  
  public WDBMgr_FileNotFound(Throwable nestedException) {
    super(nestedException);
  }

} // class: WDBMgr_ErrorReadingConfigFile
