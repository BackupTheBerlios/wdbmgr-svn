package com.gele.tools.wow.wdbearmanager.exception;





/**
 * Wjile trying to access the WBD file, an error occurred<br/> 
 * <br/>
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: WDBMgr_ErrorWritingCSVFile.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/exception/WDBMgr_ErrorWritingCSVFile.java $
 * $Rev: 195 $
 */

public class WDBMgr_ErrorWritingCSVFile extends WDBMgr_Exception{

  /**
	 * 
	 */
	private static final long serialVersionUID = -2760088928800657059L;

public WDBMgr_ErrorWritingCSVFile() {
    super();
  }

  public WDBMgr_ErrorWritingCSVFile(String msg) {
    super(msg);
  }

  public WDBMgr_ErrorWritingCSVFile(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: WDBMgr_ErrorReadingWDBFile
