package com.gele.tools.wow.wdbearmanager.exception;

/**
 * The XML specs for a WDB contained wrong types<br/> <br/>
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
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
 */

public class WDBMgr_ErrorInsideXMLConfigFile extends WDBMgr_Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -3518628375726760240L;

  public WDBMgr_ErrorInsideXMLConfigFile() {
    super();
  }

  public WDBMgr_ErrorInsideXMLConfigFile(String msg) {
    super(msg);
  }

  public WDBMgr_ErrorInsideXMLConfigFile(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: WDBMgr_ErrorInsideXMLConfigFile
