/*
 * Created on 15.03.2005
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: SQL_Panel.java 218 2006-09-14 23:45:59Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-15 01:45:59 +0200 (Fr, 15 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 218 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/SQL_Panel.java $
 * $Rev: 218 $
 */
package com.gele.tools.wow.wdbearmanager.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.sql.DatabaseMetaData;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.base.dbaccess.WDB_DTO;
import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCP;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchSCPItem;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_DatabaseException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_IOException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoDataAvailableException;
import com.gele.tools.wow.wdbearmanager.gui.helper.pbar.WDBMgr_Caller_I;
import com.gele.tools.wow.wdbearmanager.gui.helper.pbar.WDB_ProgressBar;
import com.gele.tools.wow.wdbearmanager.helper.ExtensionFileFilter;
import com.gele.tools.wow.wdbearmanager.helper.WDB_Helper;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;
import com.gele.tools.wow.wdbearmanager.inout.SCPWritten;
import com.gele.tools.wow.wdbearmanager.inout.scp.GenericPatchSCP;
import com.gele.tools.wow.wdbearmanager.inout.wdb.PB_WDB_Import;
import com.gele.tools.wow.wdbearmanager.inout.wdb.PB_WDB_ImportRecursive;
import com.gele.tools.wow.wdbearmanager.inout.wdbtxt.PB_WDBTXT_Export;
import com.gele.tools.wow.wdbearmanager.inout.wdbtxt.PB_WDBTXT_Import;
import com.gele.utils.ReadPropertiesFile;

/**
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: SQL_Panel.java 218 2006-09-14 23:45:59Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-15 01:45:59 +0200 (Fr, 15 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 218 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/SQL_Panel.java $
 * $Rev: 218 $
 */
public class SQL_Panel extends JPanel implements ActionListener,
    WDBMgr_Caller_I {
  /**
   * 
   */
  private static final long serialVersionUID = -441604723901216561L;

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(SQL_Panel.class);

  JLabel statusField = null;

  private HashMap scp2Patch = new HashMap();

  private static String BUTTON_REFRESH = "refresh";

  // Special features: START
  private static String BUTTON_IMPORT_RECURSIVELY_WDB_INSERT = "recursively import multiple WDB, no update";

  private static String BUTTON_IMPORT_RECURSIVELY_WDB_UPDATE = "recursively import multiple WDB, with update";

  private static String BUTTON_IMPORT_MULTI_WDB_INSERT = "import multiple WDB, no update";

  private static String BUTTON_IMPORT_MULTI_WDB_UPDATE = "import multiple WDB, with update";

  // import, export
  private static String BUTTON_EXPORT_DATABASE_TABLE = "export database to WDBTXT files";

  private static String BUTTON_IMPORT_DATABASE_INSERT = "import multiple WDBTXT files, no update";

  private static String BUTTON_IMPORT_DATABASE_UPD = "import multiple WDBTXT files, with update";

  // Special features: END

  // Properties: START

  private File scpDirectory = new File("");

  private String SCP_DIR = "scpDir";

  private File wdbDirectory = new File("");

  private String WDB_DIR = "wdbDir";

  // Directory for the "import/export as WDBTXT" dialog
  File wdbtxtDirectory = new File("");

  private String WDBTXT_DIR = "wdbtxtDir";

  // Properties: END

  private static String BUTTON_UPDATE = "write UTF-8";

  private JRadioButton updButton = new JRadioButton(BUTTON_UPDATE);

  // User can choose a language for the REFRESH
  // - refresh shows the number of entries inside the database
  private JComboBox wdbLocale = null;

  // User can choose a language for the PATCH
  private JComboBox patchLocale = null;

  // number of entries inside tables
  //  private JLabel labNumCreatureCache = new JLabel("0");
  //
  //  private JLabel labNumGameobjectCache = new JLabel("0");
  //
  //  private JLabel labNumItemCache = new JLabel("0");
  //
  //  private JLabel labNumItemNameCache = new JLabel("0");
  //
  //  private JLabel labNumItemtextChaxhe = new JLabel("0");
  //
  //  private JLabel labNumNpcCache = new JLabel("0");
  //
  //  private JLabel labNumPagetextCache = new JLabel("0");
  //
  //  private JLabel labNumQuestcache = new JLabel("0");

  Properties moduleProps = null;

  private String propFILE = "module_sql.properties";

  // global database access
  //SQLManager mySQLMgr = null;

  WDBearManager_I myWoWWDBManager_API = null;

  // HashMap: Needed for db statistics
  // KEY:    name of the database table
  // VALUE:  instance of JLabel
  private HashMap hmpTable2JLabel = new HashMap();

  public SQL_Panel(WDBearManager_I parAPI) throws WDBMgr_Exception {

    this.myWoWWDBManager_API = parAPI;

    PatchSCP myPSCP = null;
    try {
      myPSCP = WDB_Helper.readXML4SCP(WDB_Helper.PATCH_XML);
      //System. out.println("Anzahl PatchSCP: "+ myArr.length);
    } catch (Exception ex) {
      ex.printStackTrace();
      this.myLogger.error("Fatal error: Could not open: '"
          + WDB_Helper.PATCH_XML + "'");
      this.myLogger.error(ex.getMessage());
      System.exit(0);
    }

    // Access database

    try {
      this.moduleProps = ReadPropertiesFile.readProperties(this.propFILE);
      if (this.moduleProps.getProperty(this.SCP_DIR) != null) {
        this.scpDirectory = new File(this.moduleProps.getProperty(this.SCP_DIR));
      }
      if (this.moduleProps.getProperty(this.WDB_DIR) != null) {
        this.wdbDirectory = new File(this.moduleProps.getProperty(this.WDB_DIR));
      }
      if (this.moduleProps.getProperty(this.WDBTXT_DIR) != null) {
        this.wdbtxtDirectory = new File(this.moduleProps
            .getProperty(this.WDBTXT_DIR));
      }
    } catch (Exception ex) {
      // do nothing
      this.moduleProps = new Properties();
    }

    this.setLayout(new BorderLayout());
    JPanel buttonPanel = new JPanel();

    JPanel page_startPanel = new JPanel();
    page_startPanel.setLayout(new BorderLayout());

    // Status Field
    this.statusField = new JLabel("SQL Status: OK");
    this.add(this.statusField, BorderLayout.PAGE_END);

    Object[] possibilities = this.readInf_Locale();

    // Build patch from patchSCP.xml
    JLabel jbLabel = new JLabel("Select locale: ");
    buttonPanel.add(jbLabel);
    patchLocale = new JComboBox(possibilities);
    patchLocale.setToolTipText("Select a locale of the PATCH process");
    buttonPanel.add(patchLocale);

    JButton btnPatchSCP;
    PatchSCPItem[] myArr = myPSCP.getPatchSCPItem();
    for (int i = 0; i < myArr.length; i++) {
      this.scp2Patch.put(myArr[i].getPatchConfig().getName(), myArr[i]
          .getPatchConfig());
      btnPatchSCP = new JButton("Patch " + myArr[i].getPatchConfig().getName());
      btnPatchSCP.addActionListener(this);
      btnPatchSCP.setToolTipText("Patch '"
          + myArr[i].getPatchConfig().getName() + "' with database content");
      buttonPanel.add(btnPatchSCP);
    }
    this.updButton.setToolTipText("Do not use UTF-8 for WadEMU");
    buttonPanel.add(this.updButton);

    page_startPanel.add(buttonPanel, BorderLayout.PAGE_START);

    // Status info about the database
    Border borDBStatus = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    TitledBorder titDBStatus = BorderFactory.createTitledBorder(borDBStatus,
        "DB Status");
    JPanel dbStatus = new JPanel();
    dbStatus.setBorder(titDBStatus);
    dbStatus.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;

    jbLabel = null;
    // Select locale

    jbLabel = new JLabel("Select locale: ");
    dbStatus.add(jbLabel);
    wdbLocale = new JComboBox(possibilities);
    wdbLocale.setToolTipText("Select a locale of the WDB entries");
    dbStatus.add(wdbLocale, c);

    // Status:
    // For all files, that are inside the WDBearManager.WDBEAR_CONFIG folder
    File wdbDir = new File(WDBearManager.WDBEAR_CONFIG);
    String strConfFiles[] = wdbDir.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return (name.endsWith(".xml"));
      }
    });
    for (int i = 0; i < strConfFiles.length; i++) {
      WDB_DTO myDTO = WDB_Helper.createDTOfromXML(null, strConfFiles[i]);
      jbLabel = new JLabel(myDTO.getTableName() + ": ");
      dbStatus.add(jbLabel);
      jbLabel = new JLabel("0");
      dbStatus.add(jbLabel, c);
      hmpTable2JLabel.put(myDTO.getTableName(), jbLabel);
    }

    //    JLabel jbLabel = new JLabel("CreatureCache: ");
    //    dbStatus.add(jbLabel);
    //    dbStatus.add(this.labNumCreatureCache, c);
    //
    //    jbLabel = new JLabel("GameobjectCache: ");
    //    dbStatus.add(jbLabel);
    //    dbStatus.add(this.labNumGameobjectCache, c);
    //
    //    jbLabel = new JLabel("ItemCache: ");
    //    dbStatus.add(jbLabel);
    //    dbStatus.add(this.labNumItemCache, c);
    //
    //    jbLabel = new JLabel("ItemNameCache: ");
    //    dbStatus.add(jbLabel);
    //    dbStatus.add(this.labNumItemNameCache, c);
    //
    //    jbLabel = new JLabel("ItemtextCaxhe: ");
    //    dbStatus.add(jbLabel);
    //    dbStatus.add(this.labNumItemtextChaxhe, c);
    //
    //    jbLabel = new JLabel("NpcCache: ");
    //    dbStatus.add(jbLabel);
    //    dbStatus.add(this.labNumNpcCache, c);
    //
    //    jbLabel = new JLabel("PagetextCache: ");
    //    dbStatus.add(jbLabel);
    //    dbStatus.add(this.labNumPagetextCache, c);
    //
    //    jbLabel = new JLabel("QuestCache: ");
    //    dbStatus.add(jbLabel);
    //    dbStatus.add(this.labNumQuestcache, c);

    JButton jbButton = new JButton(SQL_Panel.BUTTON_REFRESH);
    jbButton.addActionListener(this);
    dbStatus.add(jbButton);

    page_startPanel.add(dbStatus, BorderLayout.CENTER);

    // dbInfo
    DatabaseMetaData myDBMD = null;
    try {
      myDBMD = this.myWoWWDBManager_API.getDatabaseMetaData();
    } catch (WDBMgr_DatabaseException ex) {
      // do nothing
    } catch (WDBMgr_Exception ex) {
      this.myLogger
          .error("Error accessing database - Please check if the database is available");
      this.myLogger.error("Program exiting...");
      this.myLogger.error(WDBearManager.VERSION_INFO);
      System.exit(0);
    }

    // DB status
    try {
      Border loweredEtched = BorderFactory
          .createEtchedBorder(EtchedBorder.LOWERED);
      TitledBorder titled = BorderFactory.createTitledBorder(loweredEtched,
          "DB Info");
      JPanel dbInfo = new JPanel();
      dbInfo.setBorder(titled);
      dbInfo.setLayout(new GridBagLayout());
      jbLabel = new JLabel("Database: ");
      dbInfo.add(jbLabel);
      dbInfo.add(new JLabel(myDBMD.getDatabaseProductName()), c);

      jbLabel = new JLabel("DB Version: ");
      dbInfo.add(jbLabel);
      dbInfo.add(new JLabel(myDBMD.getDatabaseProductVersion()), c);

      jbLabel = new JLabel("DB Driver: ");
      dbInfo.add(jbLabel);
      dbInfo.add(new JLabel(myDBMD.getDriverName()), c);

      jbLabel = new JLabel("DB Driver Version: ");
      dbInfo.add(jbLabel);
      dbInfo.add(new JLabel(myDBMD.getDriverVersion()), c);
      page_startPanel.add(dbInfo, BorderLayout.PAGE_END);
    } catch (Exception ex) {

    }
    this.add(page_startPanel, BorderLayout.PAGE_START);

    Border loweredEtched = BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED);
    TitledBorder titled = BorderFactory.createTitledBorder(loweredEtched,
        "Special features");
    JPanel specFeatures = new JPanel(new GridBagLayout());
    c = new GridBagConstraints();
    //c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = GridBagConstraints.REMAINDER;

    specFeatures.setBorder(titled);
    JButton myButton = new JButton(
        SQL_Panel.BUTTON_IMPORT_RECURSIVELY_WDB_INSERT);
    myButton.addActionListener(this);
    myButton
        .setToolTipText("Scans a directory recursively and inserts all found WDB files");
    specFeatures.add(myButton);
    myButton = new JButton(SQL_Panel.BUTTON_IMPORT_RECURSIVELY_WDB_UPDATE);
    myButton
        .setToolTipText("Scans a directory recursively and inserts/updates all found WDB files");
    myButton.addActionListener(this);
    specFeatures.add(myButton, c);

    myButton = new JButton(SQL_Panel.BUTTON_IMPORT_MULTI_WDB_INSERT);
    myButton.addActionListener(this);
    specFeatures.add(myButton);
    myButton = new JButton(SQL_Panel.BUTTON_IMPORT_MULTI_WDB_UPDATE);
    myButton.addActionListener(this);
    specFeatures.add(myButton, c);

    myButton = new JButton(SQL_Panel.BUTTON_EXPORT_DATABASE_TABLE);
    myButton.addActionListener(this);
    specFeatures.add(myButton);
    myButton = new JButton(SQL_Panel.BUTTON_IMPORT_DATABASE_INSERT);
    myButton.addActionListener(this);
    specFeatures.add(myButton);
    myButton = new JButton(SQL_Panel.BUTTON_IMPORT_DATABASE_UPD);
    myButton.addActionListener(this);
    specFeatures.add(myButton, c);

    this.add(specFeatures, BorderLayout.CENTER);

  }// constructor

  private void setStatus(String parStatus) {
    this.statusField.setText(parStatus);
  }

  public void actionPerformed(ActionEvent arg0) {
    JButton source = (JButton) (arg0.getSource());

    Set myKeys = this.scp2Patch.keySet();
    Iterator itKeys = myKeys.iterator();
    String strKey = "";
    while (itKeys.hasNext()) {
      strKey = (String) itKeys.next();
      if (source.getText().equals("Patch " + strKey)) {
        //System .out.println("Gefunden -> generic patch");

        JFileChooser chooser = new JFileChooser(this.scpDirectory);
        ExtensionFileFilter filter = new ExtensionFileFilter();
        filter.addExtension("scp");
        filter.setDescription("Search for *.scp");
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
          GenericPatchSCP myGenericPatchSCP = new GenericPatchSCP();
          try {
            boolean useUTF8 = false;
            if (this.updButton.isSelected()) {
              useUTF8 = true;
            }
            SCPWritten mySCPW = myGenericPatchSCP.merge(
                this.myWoWWDBManager_API, chooser.getSelectedFile()
                    .getAbsolutePath(), (PatchConfig) this.scp2Patch
                    .get(strKey), useUTF8, false, patchLocale.getSelectedItem()
                    .toString());
            String msg = myGenericPatchSCP.patch_info_simple(mySCPW, chooser
                .getSelectedFile().getAbsolutePath());

            JOptionPane.showMessageDialog(this, msg, "Info "
                + WDBearManager.VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);
          } catch (WDBMgr_IOException ex) {
            String msg = "The destination SCP file could not be created";
            JOptionPane.showMessageDialog(this, msg, "Error "
                + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
            return;
          } catch (WDBMgr_NoDataAvailableException ex) {
            String msg = "Merging impossible\n"
                + "There are no entries inside the database";
            JOptionPane.showMessageDialog(this, msg, "Warning "
                + WDBearManager.VERSION_INFO, JOptionPane.WARNING_MESSAGE);
          } catch (Exception ex) {

            StringWriter mySW = new StringWriter();
            PrintWriter printWriter = new PrintWriter(mySW);
            ex.printStackTrace(printWriter);
            this.myLogger.error("Error while merging SCP with database:");
            this.myLogger.error(mySW.getBuffer().toString());

            String msg = "Error while merging SCP with database\n"
                + ex.getMessage();
            JOptionPane.showMessageDialog(this, msg, "Error "
                + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
            return;
          }
          this.scpDirectory = chooser.getSelectedFile();
          this.moduleProps.put(this.SCP_DIR, chooser.getSelectedFile()
              .getAbsolutePath());
          try {
            FileOutputStream myOS = new FileOutputStream(this.propFILE);
            this.moduleProps.store(myOS, "Config file for the SQL module");
            myOS.close();
          } catch (Exception ex) {
            // do nothing
          }
        }
        return;
      }
    }// dynamic patch?

    if (source.getText().equals(SQL_Panel.BUTTON_REFRESH)) {

      // refresh database
      Iterator itTable = hmpTable2JLabel.keySet().iterator();
      try {
        String tabName = "";
        JLabel jlbStatus = null;
        DTO_Interface qbeDTO = null;
        while (itTable.hasNext()) {
          tabName = (String) itTable.next();
          qbeDTO = myWoWWDBManager_API.createDTOfromTable(tabName);
          jlbStatus = (JLabel) hmpTable2JLabel.get(tabName);
          if (this.wdbLocale.getSelectedItem()
              .equals(WDB_Helper.INF_LOCALE_ALL) == false) {
            qbeDTO.setColumnValue(WDB_Helper.INF_LOCALE, this.wdbLocale
                .getSelectedItem());
          }
          jlbStatus.setText(String.valueOf(this.myWoWWDBManager_API
              .getCountOf(qbeDTO)));
          // Output to log file
          this.myLogger.debug(tabName + ": "
              + this.myWoWWDBManager_API.getCountOf(qbeDTO));
        }
        // Output to log file

        // refresh the JComboBox that contains languages
        Object lastOption = this.wdbLocale.getSelectedItem();
        ComboBoxModel myCBM = new MyComboBoxModel(this.readInf_Locale());
        this.wdbLocale.setModel(myCBM);
        this.wdbLocale.setSelectedItem(lastOption);
        // Patch locale
        lastOption = this.patchLocale.getSelectedItem();
        this.patchLocale.setModel(myCBM);
        this.patchLocale.setSelectedItem(lastOption);

      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "WDB Error\n"
            + "Error while accessing database\n\n" + ex.getMessage() + "\n\n",
            "Error" + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
      }
    } else if ((source.getText()
        .equals(SQL_Panel.BUTTON_IMPORT_MULTI_WDB_INSERT))
        || (source.getText().equals(SQL_Panel.BUTTON_IMPORT_MULTI_WDB_UPDATE))) {
      JFileChooser chooser = new JFileChooser(this.wdbDirectory);
      ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("wdb");
      filter.setDescription("Import multiple WDB files");
      chooser.setFileFilter(filter);
      chooser.setMultiSelectionEnabled(true);

      int returnVal = chooser.showOpenDialog(this);
      boolean update = true;
      if (source.getText().equals(SQL_Panel.BUTTON_IMPORT_MULTI_WDB_INSERT)) {
        update = false;
      }

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File[] wdbFiles = chooser.getSelectedFiles();
        //Collection wdbItems = null;

        PB_WDB_Import myImp = new PB_WDB_Import(this.myWoWWDBManager_API,
            wdbFiles, update);
        WDB_ProgressBar myPB = new WDB_ProgressBar(this, myImp, "WDB-Import");
        myPB.start();
        /*
         ObjectsWritten objWritten = new ObjectsWritten();

         for (int i = 0; i < wdbFiles.length; i++) {
         try {
         // load WDB
         wdbItems = this.myWoWWDBManager_API.readWDBfile(wdbFiles[i]
         .getAbsolutePath());
         } catch (WDBMgr_Exception ex) {
         JOptionPane.showMessageDialog(this, "WDB Error\n"
         + "Error reading WDB file: '" + wdbFiles[i].getAbsolutePath()
         + "'\n" + "Aborting import\n" + ex.getMessage(), "Error"
         + WDBManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
         break;
         }
         try {
         if (wdbItems.size() > 0) {
         // import
         ObjectsWritten tmpOW = this.myWoWWDBManager_API
         .insertOrUpdateToSQLDB(wdbItems, false);
         objWritten.setNumErrorInsert(objWritten.getNumErrorInsert()
         + tmpOW.getNumErrorInsert());
         objWritten.setNumErrorUpdate(objWritten.getNumErrorUpdate()
         + tmpOW.getNumErrorUpdate());
         objWritten.setNumInsert(objWritten.getNumInsert()
         + tmpOW.getNumInsert());
         objWritten.setNumSkipped(objWritten.getNumSkipped()
         + tmpOW.getNumSkipped());
         objWritten.setNumUpdate(objWritten.getNumUpdate()
         + tmpOW.getNumUpdate());
         }
         } catch (WDBMgr_Exception ex) {
         JOptionPane.showMessageDialog(this, "WDB Error\n"
         + "Error writing to database file: '"
         + wdbFiles[i].getAbsolutePath() + "'\n" + "Aborting import\n"
         + ex.getMessage(), "Error" + WDBManager.VERSION_INFO,
         JOptionPane.ERROR_MESSAGE);
         break;
         }
         }// for... all files


         this.taskFinished(objWritten);
         */
        //        JOptionPane.showMessageDialog(this, "WDB Import to SQL\n"
        //            + "Import of WDB files successfull", "Info "
        //            + WDBManager.VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);
        this.wdbDirectory = chooser.getSelectedFile();
        this.moduleProps.put(this.WDB_DIR, chooser.getSelectedFile()
            .getAbsolutePath());
        try {
          FileOutputStream myOS = new FileOutputStream(this.propFILE);
          this.moduleProps.store(myOS, "Config file for the SQL module");
          myOS.close();
        } catch (Exception ex) {
          // do nothing
        }
      }// user did choose file(s)
    }// MULTI_WDB_UPDATE
    else if ((source.getText()
        .equals(SQL_Panel.BUTTON_IMPORT_RECURSIVELY_WDB_INSERT))
        || (source.getText()
            .equals(SQL_Panel.BUTTON_IMPORT_RECURSIVELY_WDB_UPDATE))) {
      JFileChooser chooser = new JFileChooser(this.wdbDirectory);
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returnVal = chooser.showOpenDialog(this);
      boolean update = true;
      if (source.getText().equals(
          SQL_Panel.BUTTON_IMPORT_RECURSIVELY_WDB_INSERT)) {
        update = false;
      }

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File startDir = chooser.getSelectedFile();
        //Collection wdbItems = null;

        PB_WDB_ImportRecursive myImp = new PB_WDB_ImportRecursive(
            this.myWoWWDBManager_API, startDir, update);
        WDB_ProgressBar myPB = new WDB_ProgressBar(this, myImp,
            "Recursive WDB-Import");
        myPB.start();

        this.wdbDirectory = chooser.getSelectedFile();
        this.moduleProps.put(this.WDB_DIR, chooser.getSelectedFile()
            .getAbsolutePath());
        try {
          FileOutputStream myOS = new FileOutputStream(this.propFILE);
          this.moduleProps.store(myOS, "Config file for the SQL module");
          myOS.close();
        } catch (Exception ex) {
          // do nothing
        }
      }// user did choose file(s)
    }// RECURSE_WDB_UPDATE
    else if ((source.getText().equals(BUTTON_EXPORT_DATABASE_TABLE))) {
      JFileChooser chooser = new JFileChooser(this.wdbtxtDirectory);
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("wdbtxt");
      filter.setDescription("Export database to wdbtxt format");
      chooser.setFileFilter(filter);

      // User must choose what locale to export

      Object[] possibilities = this.readInf_Locale();

      String wdbLocale = (String) JOptionPane.showInputDialog(this,
          "WDBear locales\n" + "Please select the locale you want to use: ",
          "Locale chooser", JOptionPane.PLAIN_MESSAGE, null, possibilities,
          "ham");
      // User must choose what locale to export

      if (wdbLocale == null) {
        return;
      }

      int returnVal = chooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
          PB_WDBTXT_Export myExp = new PB_WDBTXT_Export(
              this.myWoWWDBManager_API, chooser.getSelectedFile(), wdbLocale);
          WDB_ProgressBar myPB = new WDB_ProgressBar(this, myExp,
              "WDBTXT-Import");
          myPB.start();

          /*
           String msg = ModuleWDBTXT.exportToWDBTXT(this.myWoWWDBManager_API,
           chooser.getSelectedFile());
           JOptionPane.showMessageDialog(this, "WDB DB export to WDTXT file\n"
           + "Export to wdbtxt files successfull\n" + msg + "\n\""
           + "Please check directory :"
           + chooser.getSelectedFile().getAbsolutePath(), "Info "
           + WDBManager.VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);
           */
          this.wdbtxtDirectory = chooser.getSelectedFile();
          this.moduleProps.put(this.WDBTXT_DIR, chooser.getSelectedFile()
              .getAbsolutePath());
          try {
            FileOutputStream myOS = new FileOutputStream(this.propFILE);
            this.moduleProps.store(myOS, "Config file for the SQL module");
            myOS.close();
          } catch (Exception ex) {
            // do nothing
          }
        } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(this, "WDB Error\n"
              + "Error writing wdbtxt files to: '"
              + chooser.getSelectedFile().getAbsolutePath() + "'\n"
              + "Aborting export\n" + ex.getMessage(), "Error"
              + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
        }
      }
    }// BUTTON_EXPORT_DATABASE_TABLE
    else if ((source.getText().equals(SQL_Panel.BUTTON_IMPORT_DATABASE_INSERT))
        || (source.getText().equals(SQL_Panel.BUTTON_IMPORT_DATABASE_UPD))) {
      JFileChooser chooser = new JFileChooser(this.wdbtxtDirectory);
      ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("wdbtxt");
      filter.setDescription("Import multiple WDB files");
      chooser.setFileFilter(filter);
      chooser.setMultiSelectionEnabled(true);

      int returnVal = chooser.showOpenDialog(this);
      boolean update = true;
      if (source.getText().equals(SQL_Panel.BUTTON_IMPORT_DATABASE_INSERT)) {
        update = false;
      }

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File[] wdbtxtFiles = chooser.getSelectedFiles();
        //StringBuffer wdbRet = new StringBuffer();

        // Check for compatibility
        RandomAccessFile burIn = null;
        for (int i = 0; i < wdbtxtFiles.length; i++) {
          try {
            burIn = new RandomAccessFile(wdbtxtFiles[i], "r");
          } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Testing WDBTXT files\n"
                + "Could not open: \""+ wdbtxtFiles[i].getAbsolutePath()+"\"n\n",
                "Error" + WDBearManager.VERSION_INFO,
                JOptionPane.ERROR_MESSAGE);
            return;
          }
          String readLine = "";
          boolean errorOccurred = false;
          try {
            readLine = burIn.readLine();
            readLine = burIn.readLine();
            readLine = burIn.readLine();
            burIn.close();
            // 3rd Line -> compatibility infos
            if( (readLine != null) && (readLine.length() > PB_WDBTXT_Export.COMPAT_PREFIX.length() )) {
              readLine = readLine.substring(PB_WDBTXT_Export.COMPAT_PREFIX.length());
              if( readLine.equals( WDBearManager.COMPATIBILITY_INFO ) == false ) {
                JOptionPane.showMessageDialog(this, "Testing WDBTXT files\n"
                    + "File: \""+ wdbtxtFiles[i].getAbsolutePath()+"\"n\n" +
                    "Compatibility Version: "+ readLine +"\n\n"+
                    "Supported Version    : "+ WDBearManager.COMPATIBILITY_INFO+"\n\n"+
                    "Import cancelled.",
                    "Error" + WDBearManager.VERSION_INFO,
                    JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
          } catch (Exception ex) {
            errorOccurred = true;
          }
          if( errorOccurred == true ) {
            JOptionPane.showMessageDialog(this, "Testing WDBTXT files\n"
                + "Error reading: \""+ wdbtxtFiles[i].getAbsolutePath()+"\"\n\n",
                "Error" + WDBearManager.VERSION_INFO,
                JOptionPane.ERROR_MESSAGE);
            return;
          }
        }// for... selected WDBTXT files

        // use PBar
        PB_WDBTXT_Import myExp = new PB_WDBTXT_Import(this.myWoWWDBManager_API,
            wdbtxtFiles, update);
        WDB_ProgressBar myPB = new WDB_ProgressBar(this, myExp, "WDB-Import");
        myPB.start();

        this.wdbtxtDirectory = chooser.getSelectedFile();
        this.moduleProps.put(this.WDBTXT_DIR, chooser.getSelectedFile()
            .getAbsolutePath());
        try {
          FileOutputStream myOS = new FileOutputStream(this.propFILE);
          this.moduleProps.store(myOS, "Config file for the SQL module");
          myOS.close();
        } catch (Exception ex) {
          // do nothing
        }
      }
    }

  }//actionPerformed

  public void taskFinished(Object parObj, long parMillies) {
    if (parObj != null) {
      if (parObj instanceof ObjectsWritten) {
        ObjectsWritten myOWr = (ObjectsWritten) parObj;

        int numObjects = myOWr.getNumInsert() + myOWr.getNumUpdate()
            + myOWr.getNumErrorInsert() + myOWr.getNumErrorUpdate()
            + myOWr.getNumSkipped();

        JOptionPane.showMessageDialog(this, "Database import successfull\n"
            + "INSERT: " + myOWr.getNumInsert() + "\n" + "UPDATE: "
            + myOWr.getNumUpdate() + "\n" + "Error INSERT: "
            + myOWr.getNumErrorInsert() + "\n" + "Error UPDATE: "
            + myOWr.getNumErrorUpdate() + "\n"
            + "Objects skipped (if update was not selected) : "
            + myOWr.getNumSkipped() + "\n" + "Processed: "
            + ((numObjects * 1000) / parMillies) + " objects per second ",
            "Info " + WDBearManager.VERSION_INFO,
            JOptionPane.INFORMATION_MESSAGE);
      } else if (parObj instanceof Object[]) {
        // [0] -> Long, number of objects
        // [1] -> "Files written to <directory>"
        Object[] arrObjs = (Object[]) parObj;
        int numObjects = ((Long) arrObjs[0]).intValue();
        JOptionPane.showMessageDialog(this, "DB Module Panel\n"
            + "Everything went fine.\n" + arrObjs[1].toString() + "\n"
            + "Number of objects: " + numObjects + "\n" + "Processed: "
            + ((numObjects * 1000) / parMillies) + " objects per second ",
            "Info " + WDBearManager.VERSION_INFO,
            JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(this, "DB Module Panel\n"
            + "Everything went fine.\n" + parObj.toString() + "\n"
            + "Time elapsed: " + parMillies / 1000, "Info "
            + WDBearManager.VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);
      }
    }
    // else {
    // .out.println("NULL");
    //    }
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Caller_I#taskError(java.lang.Throwable)
   */
  public void taskError(Throwable parEx) {
    JOptionPane.showMessageDialog(this, "User Task error\n"
        + "System message:\n" + parEx.getMessage(), "Error "
        + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
    return;
  }

  /**
   * Read all "inf_locale" entries from the database.
   * 
   * "inf_locale" identifies the locale encoding of an entry,
   * eg "deDE", "enUS", "enGB"
   * 
   * This method returns a String array containing all found locale
   * entries *and* the "ALL" entry. "ALL" means that the user
   * ignores the locale setting
   * 
   * @return String[] containing all found inf_locale entries *and* "ALL"
   */
  private String[] readInf_Locale() {
    Collection colLangs = null;
    try {
      DTO_Interface myDTO = myWoWWDBManager_API.createDTOfromTable("itemcache");
      colLangs = myWoWWDBManager_API.getGroupBy(myDTO,
          new String[] { WDB_Helper.INF_LOCALE }, new String[] {},
          (DTO_Interface) null);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }

    String[] possibilities = new String[colLangs.size() + 1];
    Iterator itLangs = colLangs.iterator();
    int counter = 0;
    while (itLangs.hasNext()) {
      HashMap loopMap = (HashMap) itLangs.next();
      possibilities[counter] = (String) loopMap.get(WDB_Helper.INF_LOCALE);
      counter++;
    }
    possibilities[counter] = WDB_Helper.INF_LOCALE_ALL;

    return possibilities;
  }// readInf_Locale

  class MyComboBoxModel extends AbstractListModel implements ComboBoxModel {
    private Object selectedObject = null;

    private Object[] contents = null;

    public MyComboBoxModel(String[] parContents) {
      this.contents = parContents;
    }

    public void setSelectedItem(Object item) {
      selectedObject = item;
      //dbg("" + selectedObject);
      fireContentsChanged(this, -1, -1);
    }

    public Object getSelectedItem() {
      return selectedObject;
    }

    public Object getElementAt(int i) {
      if (contents != null && i <= contents.length) {
        return contents[i];
      }
      return null;
    }

    public int getSize() {
      if (contents != null) {
        return contents.length;
      }
      return 0;
    }
  }
}// SQL_Panel
