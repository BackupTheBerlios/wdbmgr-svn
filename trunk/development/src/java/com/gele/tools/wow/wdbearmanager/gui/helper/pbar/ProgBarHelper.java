/*
 * Created on 08.04.2005
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: ProgBarHelper.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/pbar/ProgBarHelper.java $
 * $Rev: 195 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.gui.helper.pbar;

import java.awt.Toolkit;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;


/**
 * 
 *
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: ProgBarHelper.java 195 2005-10-30 16:44:25Z gleibrock $
 */
public class ProgBarHelper extends Thread {
  // Logging with Log4J
  Logger myLogger = Logger.getLogger(ProgBarHelper.class);

  private WDBMgr_Task_I myTask;
  private JProgressBar progressbar;
  private JTextArea taskOutput;

  public ProgBarHelper(JProgressBar progressBar, WDBMgr_Task_I parWDBMgr_Task_I,
      JTextArea taskOutput) {
    this.progressbar = progressBar;
    this.myTask = parWDBMgr_Task_I;
    this.taskOutput = taskOutput;
  }
  public void run() {
    this.myLogger.info("ProgBarHelper: Frage WDB-Task nach Status");
    this.progressbar.setValue(this.myTask.getCurrent());
    String s = this.myTask.getMessage();
    if (s != null) {
      this.taskOutput.append(s + "\n");
      this.taskOutput.setCaretPosition(this.taskOutput.getDocument().getLength());
    }
    if (this.myTask.isDone()) {
      Toolkit.getDefaultToolkit().beep();
      this.progressbar.setValue(this.progressbar.getMinimum());
    } else {
      try {
      	Thread.sleep(1000);
      }catch( Exception ex ) {
        //
      }
      SwingUtilities.invokeLater(this);
  	}

  }
}
