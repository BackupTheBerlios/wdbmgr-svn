/*
 * Created on 15.03.2005
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: User_Python_Panel.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/User_Python_Panel.java $
 * $Rev: 195 $
*/
package com.gele.tools.wow.wdbearmanager.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;

import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_API;
import com.gele.tools.wow.wdbearmanager.helper.ExtensionFileFilter;
import com.gele.utils.ReadPropertiesFile;

/**
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 195 $
 * 
 * Panel to manage scripts from users<br/>
 * 
 *
 */
public class User_Python_Panel extends JPanel implements ActionListener {
  /**
   * 
   */
  private static final long serialVersionUID = 6766615135233529972L;

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(Python_Panel.class);

  JLabel statusField = null;

  private static String BUTTON_PATCH_QUESTS = "patch quests";

  private static String BUTTON_PATCH_ITEMS = "patch items";

  private static String BUTTON_SEARCH_SCRIPT = "Search for Script";

  private String BUTTON_ADD_SCRIPT = "Add";

  private File scriptDirectory = new File("");

  private String SCRIPT_DIR = "scriptDir";

  private String SCRIPT = "script";
  private String PARAM = "param";

  private String REMOVE_FROM_LIST = "Remove from List";

  Properties moduleProps = null;

  private String propFILE = "module_script.properties";

  Vector vecScripts = new Vector();
  HashMap hmaScript2Param = new HashMap();
  HashMap hmaScript2ParamTF = new HashMap();

  JPanel page_startPanel = null;

  JTextField scriptTextField = null;
  
  // API for the scripts
  WDBearManager_I myWoWWDBManager_I;

  JPanel buttonPanel;
  
  // Safes ressources, one INTER for the whole application
  PythonInterpreter interp = null;


  Border buttonBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

  TitledBorder buttonTitled = BorderFactory.createTitledBorder(
      this.buttonBorder, "Available scripts");

  public User_Python_Panel(WDBearManager_I parWoWWDBManager_API ) {

    this.myWoWWDBManager_I = parWoWWDBManager_API;
    
    try {
      this.moduleProps = ReadPropertiesFile.readProperties(this.propFILE);
      if (this.moduleProps.getProperty(this.SCRIPT_DIR) != null) {
        this.scriptDirectory = new File(this.moduleProps
            .getProperty(this.SCRIPT_DIR));
      }
      // max 20 scripts
      String strPython = "";
      for (int i = 0; i < 20; i++) {
        if ((strPython = this.moduleProps.getProperty(this.SCRIPT + i)) != null) {
          this.vecScripts.add(strPython);
          this.hmaScript2Param.put(strPython, this.moduleProps.getProperty(this.PARAM + i));
        }
      }
    } catch (Exception ex) {
      // do nothing
      this.moduleProps = new Properties();
    }

    this.setLayout(new BorderLayout());
    this.page_startPanel = new JPanel();
    this.page_startPanel.setLayout(new BorderLayout());

    this.generateScriptsGUI();

    Border loweredEtched = BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED);
    TitledBorder titled = BorderFactory.createTitledBorder(loweredEtched,
        "Add Jython Script");
    JPanel addPython = new JPanel();
    addPython.setBorder(titled);
    addPython.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;

    JLabel jbLabel = new JLabel("Script: ");
    addPython.add(jbLabel);
    this.scriptTextField = new JTextField("", 20);
    addPython.add(this.scriptTextField);
    JButton jbuDummy = new JButton(BUTTON_SEARCH_SCRIPT);
    jbuDummy.addActionListener(this);
    addPython.add(jbuDummy);
    jbuDummy = new JButton(this.BUTTON_ADD_SCRIPT);
    jbuDummy.addActionListener(this);
    addPython.add(jbuDummy, c);

    this.page_startPanel.add(addPython, BorderLayout.PAGE_END);

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

    if (source.getText().equals(User_Python_Panel.BUTTON_SEARCH_SCRIPT)) {
      JFileChooser chooser = new JFileChooser(this.scriptDirectory);
      ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("py");
      filter.setDescription("Search for Python files");
      chooser.setFileFilter(filter);

      int returnVal = chooser.showOpenDialog(this);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        this.vecScripts.add(chooser.getSelectedFile().getAbsolutePath());
        this.scriptTextField.setText(chooser.getSelectedFile()
            .getAbsolutePath());
        this.generateScriptsGUI();

        this.scriptDirectory = chooser.getSelectedFile();
        this.moduleProps.put(this.SCRIPT_DIR, chooser.getSelectedFile()
            .getAbsolutePath());
        try {
          FileOutputStream myOS = new FileOutputStream(this.propFILE);
          this.moduleProps.store(myOS, "Config file for the Python module");
          myOS.close();
        } catch (Exception ex) {
          // do nothing
        }
      }
      return;
    } else if (source.getText().equals(this.REMOVE_FROM_LIST)) {
      this.vecScripts.remove(source.getName());
      this.generateScriptsGUI();
    } else if (source.getText().equals(this.BUTTON_ADD_SCRIPT)) {
      this.vecScripts.add(this.scriptTextField.getText());
      this.generateScriptsGUI();
    }else {
      // Execute Script
      //System. out.println("source.getText(): "+source.getText());
      if( this.interp == null ) {
        this.interp = new PythonInterpreter();
      }

      if( this.myWoWWDBManager_I == null ) {
        this.myWoWWDBManager_I = new WDBearManager_API();
      }
      JTextField myJTF = (JTextField)this.hmaScript2ParamTF.get(source.getText() );
      if( myJTF == null ) {
        this.myLogger.error( "fatal error, no tf");
        System.exit(0);
      }
      String param = myJTF.getText();
      this.hmaScript2Param.put( source.getText(), param );
      if( param.length() != 0 ) {
	      String[] result = param.split(" ");
	      for( int i=0; i<result.length; i++ ) {
	        // Split by "="
	        String[] splitEQ = result[i].split("=");
	        if( splitEQ.length != 2 ) {
	          String msg = "Error in parameter specification\n"
	            + "Invalid: '"+result[i]+"'\n"
	            + "Only param=value allowed";
	          JOptionPane.showMessageDialog(this, msg, "Error "
	              + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
	          return;
	        }
          this.interp.set(splitEQ[0], splitEQ[1]);
	      }
      }

      this.interp.set("wdbmgrapi", this.myWoWWDBManager_I);
      this.interp.execfile(source.getText());
      
      generateScriptsGUI();

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

  private void generateScriptsGUI() {
    if (this.buttonPanel != null) {
      this.page_startPanel.remove(this.buttonPanel);
    }
    // cleanup props
    for (int i = 0; i < 20; i++) {
      this.moduleProps.remove(this.SCRIPT + i);
      this.moduleProps.remove(this.PARAM+ i);
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
    for (Enumeration enuScr = this.vecScripts.elements(); enuScr
        .hasMoreElements();) {

      scriptName = (String) enuScr.nextElement();
      this.moduleProps.setProperty(this.SCRIPT + i, scriptName);
      if( (paramScript=(String)this.hmaScript2Param.get(scriptName))!=null) {
      	this.moduleProps.setProperty(this.PARAM + i, paramScript);
      }

      JButton jbuScript = new JButton(scriptName);
      jbuScript.addActionListener(this);
      this.buttonPanel.add(jbuScript);
      JTextField parJTF = new JTextField("", 20);
      parJTF.setText((String)this.hmaScript2Param.get(scriptName));
      this.hmaScript2ParamTF.put(scriptName, parJTF);
      this.buttonPanel.add(parJTF);
      jbuDummy = new JButton(this.REMOVE_FROM_LIST);
      jbuDummy.setName(scriptName);
      jbuDummy.addActionListener(this);
      this.buttonPanel.add(jbuDummy, c);
      i++;
    }

    this.page_startPanel.add(this.buttonPanel, BorderLayout.PAGE_START);
    // write properties stuff

    this.add(this.page_startPanel, BorderLayout.PAGE_START);

    // write properties file
    try {
      FileOutputStream myOS = new FileOutputStream(this.propFILE);
      this.moduleProps.store(myOS, "Config file for the Python module");
      myOS.close();
    } catch (Exception ex) {
      // do nothing
    }

  }
}// Python_Panel
