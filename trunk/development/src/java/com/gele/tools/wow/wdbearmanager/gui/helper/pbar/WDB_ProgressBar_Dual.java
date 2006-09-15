package com.gele.tools.wow.wdbearmanager.gui.helper.pbar;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;

/*
 * ProgressBarDemo.java is a 1.4 application that requires these files:
 *   LongTask.java
 *   SwingWorker.java
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDB_ProgressBar_Dual.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/helper/pbar/WDB_ProgressBar_Dual.java $
 * $Rev: 195 $
 */
public class WDB_ProgressBar_Dual extends Thread {
  // Logging with Log4J
  Logger myLogger = Logger.getLogger(WDB_ProgressBar_Dual.class);

  public final static int ONE_SECOND = 1000;

  private JProgressBar progressBar;

  private JProgressBar progressBar2;

  private Timer timer;

  private JTextArea taskOutput;

  //private String newline = "\n";

  private WDBMgr_Task_Dual_I myTask = null;

  private JPanel myPanel = new JPanel();

  private String winTitle = "";

  private JFrame frame = null;

  private WDBMgr_Caller_I myCaller = null;
  
  
  private Date startDate = null;

  public WDB_ProgressBar_Dual(WDBMgr_Caller_I parCaller,
      WDBMgr_Task_Dual_I parTask, String parTitle) {
    this.myPanel.setLayout(new BorderLayout());

    this.myCaller = parCaller;
    this.myTask = parTask;
    this.winTitle = parTitle;

    createGUI();

  }// WDB_ProgressBar.java

  public void createGUI() {
    //Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);

    //Create and set up the window.
    this.frame = new JFrame("WDB Manager progress...");
    this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    //Create and set up the content pane.
    JComponent newContentPane = this.myPanel;
    newContentPane.setOpaque(true); //content panes must be opaque
    this.frame.setContentPane(newContentPane);

    this.progressBar2 = new JProgressBar(0, this.myTask.getLenghtOfOuterLoop());
    this.progressBar2.setValue(0);
    this.progressBar2.setStringPainted(true);

    this.progressBar = new JProgressBar(0, this.myTask.getLengthOfTask());
    this.progressBar.setValue(0);
    this.progressBar.setStringPainted(true);

    this.taskOutput = new JTextArea(5, 20);
    this.taskOutput.setMargin(new Insets(5, 5, 5, 5));
    this.taskOutput.setEditable(false);
    this.taskOutput.setCursor(null); //inherit the panel's cursor
    //see bug 4851758

    JPanel panel = new JPanel();
    panel.add(this.progressBar2);
    panel.add(this.progressBar);

    this.myPanel.add(panel, BorderLayout.PAGE_START);
    this.myPanel.add(new JScrollPane(this.taskOutput), BorderLayout.CENTER);
    this.myPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    //Display the window.
    this.frame.setTitle(this.winTitle);
    this.frame.pack();
    this.frame.setVisible(true);

  }// createGUI

  public void go() throws WDBMgr_Exception {
    this.myLogger.info("PBar -> go");
    this.myPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    this.myLogger.info("Timer -> go");

    this.myLogger.info("MyTask -> go");

    createGUI();

    //this.start();
    //SwingUtilities.invokeLater(new ProgBarHelper(progressBar, myTask, taskOutput) );
    this.myLogger.info("nach start");
  }

  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  private HelperThread_Dual myHT = null;

  public void run() {
    this.myHT = new HelperThread_Dual(this.myTask);
    this.myHT.start();
    this.startDate = new Date();

    //Create a timer.
    this.timer = new Timer(ONE_SECOND, new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        //myLogger.info("Timer: Frage WDB-Task nach Status");
        WDB_ProgressBar_Dual.this.progressBar2
            .setValue(WDB_ProgressBar_Dual.this.myHT.getCurrentOfOuterLoop());
        WDB_ProgressBar_Dual.this.progressBar
            .setValue(WDB_ProgressBar_Dual.this.myHT.getCurrent());
        String s = WDB_ProgressBar_Dual.this.myHT.getMessage();
        if (s != null) {
          WDB_ProgressBar_Dual.this.taskOutput.setText(s);
          /*
           append(s + WDB_ProgressBar.this.newline);
           WDB_ProgressBar.this.taskOutput.setCaretPosition(WDB_ProgressBar.this.taskOutput.getDocument().getLength());
           */
        }
        if (WDB_ProgressBar_Dual.this.myHT.isDone()) {
          //Toolkit.getDefaultToolkit().beep();
          WDB_ProgressBar_Dual.this.timer.stop();
          WDB_ProgressBar_Dual.this.myPanel.setCursor(null); //turn off the wait cursor
          //progressBar.setValue(progressBar.getMinimum());
          WDB_ProgressBar_Dual.this.frame
              .setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
          WDB_ProgressBar_Dual.this.myCaller
              .taskFinished(WDB_ProgressBar_Dual.this.myHT.getResult()
                  , new Date().getTime()
                  - WDB_ProgressBar_Dual.this.startDate.getTime());
          WDB_ProgressBar_Dual.this.frame.setVisible(false);
        }
        if (WDB_ProgressBar_Dual.this.myHT.getException() != null) {
          WDB_ProgressBar_Dual.this.timer.stop();
          WDB_ProgressBar_Dual.this.myHT.getMessage();
          WDB_ProgressBar_Dual.this.frame.setVisible(false);
          WDB_ProgressBar_Dual.this.myCaller
              .taskError(WDB_ProgressBar_Dual.this.myHT.getException());
        } else {
        }
      }
    });
    this.timer.start();
    /*
     createGUI();
     this.timer.start();
     try {
     while (myTask.isDone() == false) {
     try {
     Thread.sleep(1000);
     } catch (Exception ex) {
     }

     this.myLogger.info("progbar: run");
     this.frame.repaint();
     progressBar.setValue(myTask.getCurrent());
     String s = myTask.getMessage();
     if (s != null) {
     taskOutput.append(s + newline);
     taskOutput.setCaretPosition(taskOutput.getDocument().getLength());
     }
     if (myTask.isDone()) {
     //Toolkit.getDefaultToolkit().beep();
     timer.stop();
     myPanel.setCursor(null); //turn off the wait cursor
     //progressBar.setValue(progressBar.getMinimum());
     }
     }
     this.frame.hide();


     //this.myTask.go();
     //      synchronized(objNotifyMe) {
     //        this.myLogger.info("wake up call");
     //	    	this.objNotifyMe.notify();
     //      }
     } catch (Exception ex) {
     ex.printStackTrace();
     this.myLogger.info("error");
     }
     */
  }// run

}//WDB_ProgressBar
