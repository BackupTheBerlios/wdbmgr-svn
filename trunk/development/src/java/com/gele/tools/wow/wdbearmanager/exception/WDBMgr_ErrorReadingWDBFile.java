package com.gele.tools.wow.wdbearmanager.exception;

/**
 * While trying to access the WBD file, an error occurred<br/> 
 * <br/>
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: WDBMgr_ErrorReadingWDBFile.java 201 2006-04-10 18:50:37Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/exception/WDBMgr_ErrorReadingWDBFile.java $
 * $Rev: 201 $
 */

public class WDBMgr_ErrorReadingWDBFile
    extends WDBMgr_Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 157190073796705796L;

  public WDBMgr_ErrorReadingWDBFile() {
    super();
  }

  public WDBMgr_ErrorReadingWDBFile(String msg) {
    super(msg);
  }

  public WDBMgr_ErrorReadingWDBFile(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }

  public WDBMgr_ErrorReadingWDBFile(Throwable nestedException) {
    super(nestedException);
  }

} // class: WDBMgr_ErrorReadingWDBFile
