/*
 * Created on 01.04.2005
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: JEPyperlinkListener.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/JEPyperlinkListener.java $
 * $Rev: 195 $
 */
package com.gele.tools.wow.wdbearmanager.gui.helper;

import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class JEPyperlinkListener implements HyperlinkListener {
  
  private JEditorPane htmlPane = null;
  
  public JEPyperlinkListener(JEditorPane parJEPane ) {
    this.htmlPane = parJEPane;
  }

  public void hyperlinkUpdate(HyperlinkEvent event) {
    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      try {
        this.htmlPane.setPage(event.getURL());
      } catch(IOException ioe) {
        // Some warning to user
      }
    }
  }

}
