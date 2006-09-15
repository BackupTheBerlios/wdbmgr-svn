/*
 * Created on 04 Apr 2005
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
package com.gele.tools.wow.wdbearmanager.test;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.python.util.PythonInterpreter;

import com.gele.tools.wow.wdbearmanager.api.WDBearManager_API;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.plugin.WDBearPlugin;

/**
 * @author <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 * 
 * Enhance the WDB Manager using plugins<br/>
 * 
 * 1.  Write a class that is derived from JPanel
 * 2.  Edit myPlugin_plugin.properties
 * 2.1 name=Name of my plugin
 * 2.3 class=com.gele.myplugin.HelloWorld
 * 2.4 image=images/myplugin.gif
 * 3.  Create a jar with your class(es)
 * 4.  copy *.jar, *.properties, *.gif to "plugin" folder
 * 5.  start WDBManager with "-gui"
 *
 */
public class HelloWorldPlugin extends WDBearPlugin {

  /**
   * 
   */
  private static final long serialVersionUID = -5996647171993044504L;

  JLabel statusField = null;

  JPanel page_startPanel = null;

  // Safes ressources, one INTER for the whole application
  PythonInterpreter interp = null;

  Border buttonBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

  TitledBorder buttonTitled = BorderFactory.createTitledBorder(
      this.buttonBorder, "Place your plugin text here");
  
  WDBearManager_I myAPI = null;

  public HelloWorldPlugin() {
  }

  private void setStatus(String parStatus) {
    this.statusField.setText(parStatus);
  }
  
/**
 * Method from WDBearPlugin
 * @param parAPI
 */
  public void runPlugin( WDBearManager_I parAPI ) {
    this.myAPI = parAPI;
    

    this.setLayout(new BorderLayout());
    this.page_startPanel = new JPanel();
    this.page_startPanel.setLayout(new BorderLayout());

    JPanel buttonPanel = new JPanel();

    buttonPanel.setBorder(this.buttonTitled);
    buttonPanel.setLayout(new GridBagLayout());

    JLabel myLabel = new JLabel("Hello World - Nice, you found me - wdbmanager.de.gg");
    buttonPanel.add(myLabel);

    this.page_startPanel.add(buttonPanel, BorderLayout.PAGE_START);

    this.add(this.page_startPanel, BorderLayout.PAGE_START);

    // Status Field
    this.statusField = new JLabel("Plugin Status: OK");
    this.add(this.statusField, BorderLayout.PAGE_END);

  }

}// HelloWorldPanel
