package com.gele.tools.wow.wdbearmanager.exception;


/**
 * An error occurred while trying to read the config file<br/>
 * <br/>
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDBMgr_MissingConfigParameterException.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/exception/WDBMgr_MissingConfigParameterException.java $
 * $Rev: 195 $
 */

public class WDBMgr_MissingConfigParameterException extends WDBMgr_Exception{

  /**
	 * 
	 */
	private static final long serialVersionUID = -6437035031943830900L;

public WDBMgr_MissingConfigParameterException() {
    super();
  }

  public WDBMgr_MissingConfigParameterException(String msg) {
    super(msg);
  }

  public WDBMgr_MissingConfigParameterException(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: WDBMgr_ErrorReadingConfigFile
