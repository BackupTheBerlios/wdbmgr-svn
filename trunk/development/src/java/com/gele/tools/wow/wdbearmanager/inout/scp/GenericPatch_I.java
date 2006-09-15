/*
 * Created on 22.03.2005
 *
 */
package com.gele.tools.wow.wdbearmanager.inout.scp;

import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_IOException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoDataAvailableException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_UnknownWDBFile;
import com.gele.tools.wow.wdbearmanager.inout.SCPWritten;

/**
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: GenericPatch_I.java 205 2006-08-16 14:49:30Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-08-16 16:49:30 +0200 (Mi, 16 Aug 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 205 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/scp/GenericPatch_I.java $
 * $Rev: 205 $
 */
public interface GenericPatch_I {
  /**
   * Merge database with existing creatures.scp file
   * 
   * @param parConfig config extracted from "patchSCP.xml"
   * 
   * @param parQuestsSCP name of the scp file
   * parUseUTF8 write UTF-8 letters
   * 
   * @param parLocale enUS, enGB, deDE - specifies which locale information to be used
   * ALL -> ignore locale (leads to non predictable behaviour!!)
   * 
   * @return number of entries inside database
   */
  public abstract SCPWritten merge(WDBearManager_I parAPI, String parQuestsSCP, PatchConfig parConfig,
      boolean parUseUTF8, boolean parVerbose, String parLocale) throws WDBMgr_UnknownWDBFile,
      WDBMgr_NoDataAvailableException, WDBMgr_IOException, WDBMgr_Exception;

  public abstract String patch_info(SCPWritten mySCPW, String parFilename);

  public abstract String patch_info_simple(SCPWritten mySCPW, String parFilename);
}