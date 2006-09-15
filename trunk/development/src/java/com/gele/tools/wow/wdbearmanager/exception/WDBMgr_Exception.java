package com.gele.tools.wow.wdbearmanager.exception;

import com.gele.base.exception.NestingException;




/**
 * Base Exception for all Exception that are defined for
 * the WoWWDBManager application<br/>
 * <br/>
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDBMgr_Exception.java 201 2006-04-10 18:50:37Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/exception/WDBMgr_Exception.java $
 * $Rev: 201 $
 *
 */

public class WDBMgr_Exception extends NestingException{

  /**
	 * 
	 */
	private static final long serialVersionUID = -1798213564309239762L;

public WDBMgr_Exception() {
    super();
  }

  public WDBMgr_Exception(String msg) {
    super(msg);
  }

  public WDBMgr_Exception(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
  
  public WDBMgr_Exception(Throwable nestedException) {
    super(nestedException);
  }

} // class: WDBMgr_Exception
