/*
 * Created on 15.03.2005
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: Python_Panel.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/Python_Panel.java $
 * $Rev: 195 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;

import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_API;
import com.gele.tools.wow.wdbearmanager.gui.helper.JEPyperlinkListener;
import com.gele.utils.ReadPropertiesFile;

/**
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 195 $
 * 
 * Panel to manage scripts from users<br/>
 * 
 *
 */
public class Python_Panel extends JPanel implements ActionListener {
  /**
   * 
   */
  private static final long serialVersionUID = 4901966827149552513L;

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(Python_Panel.class);

  JLabel statusField = null;

  private File scriptDirectory = new File("");

  private String SCRIPT_DIR = "scriptDir";

  private String SCRIPT = "script";

  private String PARAM = "param";

  private String VIEW_SOURCE = "View Source";

  private static String VIEW_HELP = "?";

  private String propFILE = "module_script.properties";

  Vector vecScripts = new Vector();

  HashMap hmaScript2Param = new HashMap();

  HashMap hmaScript2ParamTF = new HashMap();

  JPanel page_startPanel = null;

  JTextField scriptTextField = null;

  // API for the scripts
  WDBearManager_I myWoWWDBManager_API;

  JPanel buttonPanel;

  // Safes ressources, one INTER for the whole application
  PythonInterpreter interp = null;

  Border buttonBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

  TitledBorder buttonTitled = BorderFactory.createTitledBorder(
      this.buttonBorder, "Available scripts (check your 'scripts' folder)");

  public Python_Panel(WDBearManager_I parWoWWDBManager_API ) {
    
    this.myWoWWDBManager_API = parWoWWDBManager_API;

    this.setLayout(new BorderLayout());
    this.page_startPanel = new JPanel();
    this.page_startPanel.setLayout(new BorderLayout());

    this.generateScriptsGUI();

    this.add(this.page_startPanel, BorderLayout.PAGE_START);

    // Status Field
    this.statusField = new JLabel("Script Status: OK");
    this.add(this.statusField, BorderLayout.PAGE_END);
  }

  private void setStatus(String parStatus) {
    this.statusField.setText(parStatus);
  }

  public void actionPerformed(ActionEvent arg0) {
    JButton source = (JButton) (arg0.getSource());

    if (source.getText().equals(Python_Panel.VIEW_HELP)) {
      JFrame myFrame = new JFrame("/scripts/help/" + source.getName() + ".html");
      Font sourceFont = new Font("monospaced", Font.PLAIN, this.getFont()
          .getSize());
      //myFrame.setFont(   );
      URL urlHTML = null;
      try {
        JEditorPane htmlPane = new JEditorPane();
        //htmlPane.setFont(sourceFont);
        // .out.println("/scripts/"+source.getName()+".py");
        File scriptFile = new File("scripts/help/" + source.getName() + ".html");
        urlHTML = scriptFile.toURL();//this.getClass().getResource("/scripts/"+source.getName()+".py");
        //          .out.println( urlHTML );
        //          .out.println( urlHTML.toExternalForm() );
        htmlPane.setPage(urlHTML);
        htmlPane.setEditable(false);
        JEPyperlinkListener myJEPHL = new JEPyperlinkListener(htmlPane);
        htmlPane.addHyperlinkListener(myJEPHL);
        myFrame.getContentPane().add(new JScrollPane(htmlPane));
        myFrame.pack();
        myFrame.setSize(640, 480);
        myFrame.show();
      } catch (IOException ioe) {
        ioe.printStackTrace();
        this.myLogger.error( "Error displaying " + urlHTML);
      }
      //this.generateScriptsGUI();
    } else if (source.getText().equals(this.VIEW_SOURCE)) {
      JFrame myFrame = new JFrame("/scripts/" + source.getName() + ".py");
      Font sourceFont = new Font("monospaced", Font.PLAIN, this.getFont()
          .getSize());
      //myFrame.setFont(   );
      URL urlHTML = null;
      try {
        JEditorPane editComp = null;

        editComp = new JEditorPane();
        //editComp.setEditorKit( new NumberedEditorKit() );
        editComp.setFont(sourceFont);
        // .out.println("/scripts/"+source.getName()+".py");
        File scriptFile = new File("scripts/" + source.getName() + ".py");
        urlHTML = scriptFile.toURL();//this.getClass().getResource("/scripts/"+source.getName()+".py");
        //        .out.println( urlHTML );
        //        .out.println( urlHTML.toExternalForm() );
        editComp.setPage(urlHTML);
        editComp.setEditable(true);
        JButton tmpButton = new JButton("Save");
        tmpButton.setName("scripts/" + source.getName() + ".py");
        EditorPanelActionListener myEA = new EditorPanelActionListener(editComp);
        tmpButton.addActionListener(myEA);
        myFrame.getContentPane().setLayout(new BorderLayout());
        myFrame.getContentPane().add(tmpButton, BorderLayout.PAGE_START);
        myFrame.getContentPane().add(new JScrollPane(editComp),
            BorderLayout.CENTER);
        myFrame.pack();
        myFrame.setSize(640, 480);
        myFrame.show();
      } catch (IOException ioe) {
        ioe.printStackTrace();
        this.myLogger.error( "Error displaying " + urlHTML);
      }

    } else {
      // Execute Script
      // .out.println("source.getText(): "+source.getText());
      if (this.interp == null) {
        this.interp = new PythonInterpreter();
      }

      if (this.myWoWWDBManager_API == null) {
        this.myWoWWDBManager_API = new WDBearManager_API();
      }
      JTextField myJTF = (JTextField) this.hmaScript2ParamTF.get(source
          .getText());
      if (myJTF == null) {
        this.myLogger.error( "fatal error, no tf");
        System.exit(0);
      }
      String param = myJTF.getText();
      this.hmaScript2Param.put(source.getText(), param);
      if (param.length() != 0) {
        String[] result = param.split(" ");
        for (int i = 0; i < result.length; i++) {
          // Split by "="
          String[] splitEQ = result[i].split("=");
          if (splitEQ.length != 2) {
            String msg = "Error in parameter specification\n" + "Invalid: '"
                + result[i] + "'\n" + "Only param=value allowed";
            JOptionPane.showMessageDialog(this, msg, "Error "
                + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
            return;
          }
          this.interp.set(splitEQ[0], splitEQ[1]);
        }
      }

      this.interp.set("wdbmgrapi", this.myWoWWDBManager_API);
      this.interp.execfile("scripts/" + source.getText() + ".py");

      // write properties file
      Properties myProps = new Properties();
      myProps.setProperty(this.PARAM, (String) this.hmaScript2Param.get(source
          .getText()));
      try {
        FileOutputStream myOS = new FileOutputStream("scripts/"
            + source.getText() + ".properties");
        myProps.store(myOS, "User parameters for the Jython script");
        myOS.close();
      } catch (Exception ex) {
        // do nothing
      }

    }// Execute PY script

    /*

     else if (source.getText().equals(Python_Panel.BUTTON_PATCH_ITEMS)) {
     JOptionPane.showMessageDialog(this, "Not implemented for the GUI, yet \n"
     + "Please use the command line option", "Error"
     + WDBManager.VERSION_INFO, JOptionPane.WARNING_MESSAGE);
     return;
     } else {

     }
     */
  }//actionPerformed

  /**
   * Scan folder "scripts" for files:
   * <ul>
   * <li> <name>.py - Script </li>
   * <li> <name>.properties - GUI settings </li>
   * <li> <name>.html|<name>.rtf - Help file</li>
   * </ul>
   *
   */
  private void generateScriptsGUI() {
    if (this.buttonPanel != null) {
      this.page_startPanel.remove(this.buttonPanel);
    }

    this.buttonPanel = new JPanel();

    this.buttonPanel.setBorder(this.buttonTitled);
    this.buttonPanel.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.anchor = GridBagConstraints.WEST;

    String scriptName = "";
    JButton jbuDummy = null;

    int i = 0;
    String paramScript = "";
    File scriptDir = new File("scripts");
    // .out.println(scriptDir.getAbsolutePath());
    String[] contents = scriptDir.list();
    // datastructure
    // Hashmap: <name> 2 Vector
    // Vector: contains <name>.py
    //                  <name>.properties
    //                  <name>.html
    HashMap name2Vector = new HashMap();
    if (contents != null) {
      // a valid script has:
      // <name>.py
      // <name>.properties
      // <name>.html

      for (int k = 0; k < contents.length; k++) {
        // contains "."?
        if (contents[k].lastIndexOf(".") == -1) {
          continue;
        }
        String noSuffix = contents[k]
            .substring(0, contents[k].lastIndexOf("."));
        Vector fnames = (Vector) name2Vector.get(noSuffix);
        if (fnames == null) {
          fnames = new Vector();
        }
        fnames.add(contents[k]);
        name2Vector.put(noSuffix, fnames);
        // .out.println(contents[k]);
      }
    }

    // Validate -> remove all entries that do not contain 2 files
    Iterator itKeys = name2Vector.keySet().iterator();
    Vector vecFnames = null;
    String strFName = "";
    Vector vecRemoveThese = new Vector();
    while (itKeys.hasNext()) {
      strFName = (String) itKeys.next();
      vecFnames = (Vector) name2Vector.get(strFName);
      if (vecFnames.size() < 2) {
        this.myLogger.error( "Python Module: " + strFName);
        this.myLogger.error( "MUST have .py, .properties");
        vecRemoveThese.add(strFName);
      }
    }
    // Remove
    for (Enumeration enuScr = vecRemoveThese.elements(); enuScr
        .hasMoreElements();) {
      name2Vector.remove(enuScr.nextElement());
    }
    // .out.println("Number of modules: " + name2Vector.size());

    itKeys = name2Vector.keySet().iterator();

    Properties scriptProps = null;
    while (itKeys.hasNext()) {

      scriptName = (String) itKeys.next();

      try {
        scriptProps = ReadPropertiesFile.readProperties("scripts/" + scriptName
            + ".properties");
        if (scriptProps.get(this.PARAM) != null) {
          this.hmaScript2Param.put(scriptName, scriptProps.get(this.PARAM));
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      JButton jbuScript = new JButton(scriptName);
      jbuScript.addActionListener(this);
      this.buttonPanel.add(jbuScript);
      JTextField parJTF = new JTextField("", 35);
      //      c.gridwidth = 4;
      //      this.buttonPanel.add(jbuDummy, c);
      parJTF.setText((String) this.hmaScript2Param.get(scriptName));
      this.hmaScript2ParamTF.put(scriptName, parJTF);
      this.buttonPanel.add(parJTF);
      jbuDummy = new JButton(Python_Panel.VIEW_HELP);
      jbuDummy.setName(scriptName);
      jbuDummy.addActionListener(this);
      //this.buttonPanel.add(jbuDummy, c);
      this.buttonPanel.add(jbuDummy);
      jbuDummy = new JButton(this.VIEW_SOURCE);
      jbuDummy.setName(scriptName);
      jbuDummy.addActionListener(this);
      this.buttonPanel.add(jbuDummy, c);
      i++;
    }

    this.page_startPanel.add(this.buttonPanel, BorderLayout.PAGE_START);
    // write properties stuff

    this.add(this.page_startPanel, BorderLayout.PAGE_START);

    //    // write properties file
    //    try {
    //      FileOutputStream myOS = new FileOutputStream(this.propFILE);
    //      this.moduleProps.store(myOS, "Config file for the Python module");
    //      myOS.close();
    //    } catch (Exception ex) {
    //      // do nothing
    //    }

  }

  public class EditorPanelActionListener implements
      java.awt.event.ActionListener {
    JEditorPane jePane = null;

    EditorPanelActionListener(JEditorPane parJEPane) {
      this.jePane = parJEPane;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      JButton saveButton = (JButton) e.getSource();
      //      .out.println(jePane.getText());
      //      .out.println(saveButton.getName());
      File outFile = new File(saveButton.getName());
      try {
        FileOutputStream myOS = new FileOutputStream(outFile);
        myOS.write(this.jePane.getText().getBytes());
        myOS.close();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error writing .py file", "Error "
            + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
      }

    }
  }// inner class

}// Python_Panel
