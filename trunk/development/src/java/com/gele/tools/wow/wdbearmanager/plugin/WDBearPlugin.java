package com.gele.tools.wow.wdbearmanager.plugin;

import javax.swing.JPanel;

import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;

/*
 * Created on 16. Aug 2006
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/WDBearManager.java $
 * $Rev: 205 $
 * 
 * Changes:
 * First release
 *  
 * 
 */

/**
 * All plugins must be derived from this class<br/>
 * 
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 205 $
 *
 */

public abstract class WDBearPlugin extends JPanel {

  abstract public void runPlugin(WDBearManager_I parAPI);
}// WDBearPlugin
