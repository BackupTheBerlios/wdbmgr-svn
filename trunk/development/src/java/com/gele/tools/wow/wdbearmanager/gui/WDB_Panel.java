/*
 * Created on 15.03.2005
 * 
 * This panel is used to import WDB files and to display
 * their contents<br/>
 * <br/>
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDB_Panel.java 216 2006-09-10 16:55:52Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-10 18:55:52 +0200 (So, 10 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 216 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/gui/WDB_Panel.java $
 * $Rev: 216 $
 *
 */
package com.gele.tools.wow.wdbearmanager.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.WDB_TableModel;
import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorInsideXMLConfigFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorReadingWDBFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorReadingXMLConfigFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorWritingCSVFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorWritingTXTFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoXMLConfigFileForThisWDBException;
import com.gele.tools.wow.wdbearmanager.gui.helper.pbar.PBar_SQLImport;
import com.gele.tools.wow.wdbearmanager.gui.helper.pbar.WDBMgr_Caller_I;
import com.gele.tools.wow.wdbearmanager.gui.helper.pbar.WDB_ProgressBar;
import com.gele.tools.wow.wdbearmanager.helper.ExtensionFileFilter;
import com.gele.tools.wow.wdbearmanager.helper.TableSorter;
import com.gele.tools.wow.wdbearmanager.helper.WDBInfo;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;
import com.gele.tools.wow.wdbearmanager.inout.csv.WriteCSV;
import com.gele.tools.wow.wdbearmanager.inout.txt.WriteTXT;
import com.gele.utils.ReadPropertiesFile;

/**
 * Panel to import WDB files, store them inside SQL database, etc
 * etc
 * 
 */
public class WDB_Panel extends JPanel implements ActionListener,
    WDBMgr_Caller_I {

  /**
   * 
   */
  private static final long serialVersionUID = -5111495129189124748L;

  JLabel statusField = null;

  // contains the WDB info (display)
  JTable myJTable = new JTable();

  JScrollPane listScroller = null;

  JPanel centerPanel = null;

  private static String BUTTON_OPEN_WDB = "load WDB";

  private static String BUTTON_EXP_CSV = "export as CSV";

  private static String BUTTON_IMP_DB = "import in database";

  private static String BUTTON_UPDATE = "update DB";

  private JRadioButton updButton = new JRadioButton(BUTTON_UPDATE);

  private static String BUTTON_EXP_AS_TXT = "export as TXT";
  
  
  // PROPERTIES: START

  // Directory for the "open wdb" dialog
  File wdbDirectory = new File("");

  private String WDB_DIR = "wdbDir";

  // Directory for the "write csv" dialog
  File csvDirectory = new File("");

  private String CSV_DIR = "csvDir";

  // Directory for the "write rtf" dialog
  File txtDirectory = new File("");

  private String TXT_DIR = "txtDir";
  

  // PROPERTIES: END

  // Contains all read infos from WDB file
  Collection items = null;

  Properties moduleProps = null;

  private String propFILE = "module_wdb.properties";

  WDBearManager_I myWoWWDBManager_API = null;

  public WDB_Panel(WDBearManager_I parWoWWDBManager_API) {

    this.myWoWWDBManager_API = parWoWWDBManager_API;

    try {
      this.moduleProps = ReadPropertiesFile.readProperties(this.propFILE);
      if (this.moduleProps.getProperty(this.WDB_DIR) != null) {
        this.wdbDirectory = new File(this.moduleProps.getProperty(this.WDB_DIR));
      }
      if (this.moduleProps.getProperty(this.CSV_DIR) != null) {
        this.csvDirectory = new File(this.moduleProps.getProperty(this.CSV_DIR));
      }
      if (this.moduleProps.getProperty(this.TXT_DIR) != null) {
        this.txtDirectory = new File(this.moduleProps.getProperty(this.TXT_DIR));
      }
    } catch (Exception ex) {
      // do nothing
      this.moduleProps = new Properties();
    }

    this.setLayout(new BorderLayout());
    JPanel buttonPanel = new JPanel();

    JButton loadWDB = new JButton(WDB_Panel.BUTTON_OPEN_WDB);
    loadWDB.addActionListener(this);
    buttonPanel.add(loadWDB);
    JButton expCSV = new JButton(WDB_Panel.BUTTON_EXP_CSV);
    expCSV.addActionListener(this);
    buttonPanel.add(expCSV);
    JButton impSQL = new JButton(WDB_Panel.BUTTON_IMP_DB);
    impSQL.addActionListener(this);
    buttonPanel.add(impSQL);
    this.updButton
        .setToolTipText("if selected -> records inside database will be updated");
    buttonPanel.add(this.updButton);

    JButton expRTF = new JButton(WDB_Panel.BUTTON_EXP_AS_TXT);
    expRTF.addActionListener(this);
    buttonPanel.add(expRTF);

    this.add(buttonPanel, BorderLayout.PAGE_START);

    // JTable
    this.myJTable = new JTable();
    this.myJTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.myJTable.setAutoCreateColumnsFromModel(false);
    this.listScroller = new JScrollPane(this.myJTable);
    this.validate();
    this.centerPanel = new JPanel();
    this.centerPanel.setLayout(new GridLayout(0, 1));
    this.centerPanel.add(this.listScroller);
    this.add(this.centerPanel, BorderLayout.CENTER);

    // Status Field
    this.statusField = new JLabel("WDB Status: OK");
    this.add(this.statusField, BorderLayout.PAGE_END);

  }// constructor

  private void guiOpenWDB(File parWDBFile) {
    String filename = parWDBFile.getName();

    //WoWWDBManager_API myWoWWDBManager_API = new WoWWDBManager_API();
    
    // Identify the version of the WDB file
    WDBInfo myWDBInfo = null;
    try {
      myWDBInfo = this.myWoWWDBManager_API.getWDBInfo(parWDBFile
        .getAbsolutePath());
    }catch( Exception ex ) {
      JOptionPane.showMessageDialog(this,
          "Error retrieving information about the WDB file...\n"
              + "Please check the WDB file", "Error "
              + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
      return;
    }
    JOptionPane.showMessageDialog(this,
        "WDB Compatibility Version: "+myWDBInfo.getCompatibilityVersion()+"\n"
      + "WDB Build-VersionHEX: '"+myWDBInfo.getWdbBuildVersionHex()+"'\n"
      + "WDB Version: "+myWDBInfo.getWdbVersion()+"\n"
      + "Client Locale: "+myWDBInfo.getClientLocale()+"\n"
      + "WDB ID     : "+myWDBInfo.getWDBId()+"\n",
        "Information "
      + WDBearManager.VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);

    try {
      this.items = this.myWoWWDBManager_API.readWDBfile(parWDBFile
          .getAbsolutePath());
      if( this.myWoWWDBManager_API.getWarningMessage().length() != 0 ) {
        // Warning occurred
        JOptionPane.showMessageDialog(this,
            "A warning occurred while reading the WDB file:\n"
            + this.myWoWWDBManager_API.getWarningMessage(),
            "Warning "
                + WDBearManager.VERSION_INFO, JOptionPane.WARNING_MESSAGE);
        
      }
    } catch (WDBMgr_ErrorReadingXMLConfigFile ex) {
      JOptionPane.showMessageDialog(this,
          "No XML format definition for the WDB\n"
          + "The format of the WDB file is unknown\n\n"
          + "WDB file has build version: "+myWDBInfo.getWdbBuildVersionHex()+"\n\n"
          + "Please check matching table inside 'wdb_compatibility.properties'\n"
          + "and the entries inside 'wdbear_format'\n",
          "Error "
              + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
      return;
    } catch (WDBMgr_ErrorInsideXMLConfigFile ex) {
      JOptionPane.showMessageDialog(this,
          "Wrong XML format definition for the WDB\n"
              + "The XML format of the WDB file contains an unknown type\n\n"
              + ex.getMessage(),
              "Error "
              + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
      return;
    } catch (WDBMgr_ErrorReadingWDBFile ex) {
      JOptionPane.showMessageDialog(this, "Error reading the WDB file\n"
          + "The file could not be opened", "Error " + WDBearManager.VERSION_INFO,
          JOptionPane.ERROR_MESSAGE);
      return;
    } catch (WDBMgr_NoXMLConfigFileForThisWDBException ex) {
      JOptionPane.showMessageDialog(this, ex.getMessage(), "Error "
          + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (this.items.isEmpty()) {
      JOptionPane.showMessageDialog(this, "No data inside the WDB file\n"
          + "The selected WDB file contains no data", "Warning "
          + WDBearManager.VERSION_INFO, JOptionPane.WARNING_MESSAGE);
      return;
    }
    Iterator itWDBS = this.items.iterator();
    DTO_Interface myDTO = null;
    if (itWDBS.hasNext()) {
      myDTO = (DTO_Interface) itWDBS.next();
    }
    // Create GUI
    WDB_TableModel myWTM = new WDB_TableModel();
    TableSorter sorter = new TableSorter(myWTM);
    myWTM.setData(myDTO.getColumns(), this.items);
    this.centerPanel.removeAll();
    this.myJTable = new JTable(sorter);

    /*
     //  Ask to be notified of selection changes.
     ListSelectionModel rowSM = this.myJTable.getSelectionModel();
     rowSM.addListSelectionListener(new ListSelectionListener() {
     public void valueChanged(ListSelectionEvent e) {
     //Ignore extra messages.
     if (e.getValueIsAdjusting()) return;

     ListSelectionModel lsm =
     (ListSelectionModel)e.getSource();
     if (lsm.isSelectionEmpty()) {
     .out.println("no rows are selected");
     } else {
     int selectedRow = lsm.getMinSelectionIndex();
     .out.println("selectedRow is selected");
     }
     }
     });
     */

    this.listScroller = new JScrollPane(this.myJTable);
    sorter.setTableHeader(this.myJTable.getTableHeader()); //ADDED THIS
    //this.myJTable.setPreferredScrollableViewportSize(new Dimension(640, 480));
    this.myJTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.myJTable.setAutoCreateColumnsFromModel(false);

    this.centerPanel.add(this.listScroller);

    //    System. out.println("openWDB -> sortieren");
    //    sortAllRowsBy(myWTM, 0, false);

    this.validate();

    //this.add(this.myPanel, BorderLayout.CENTER);

    this.setStatus("Loaded : " + filename + " ( " + this.items.size()
        + " objects ) // Version: "+ myWDBInfo.getWdbVersion());
  }// openWDB

  /* (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent arg0) {
    JButton source = (JButton) (arg0.getSource());

    if (source.getText().equals(WDB_Panel.BUTTON_OPEN_WDB)) {

      JFileChooser chooser = new JFileChooser(this.wdbDirectory);
      ExtensionFileFilter filter = new ExtensionFileFilter();
      filter.addExtension("wdb");
      //      filter.addExtension("gameobjectcache.wdb");
      //      filter.addExtension("itemcache.wdb");
      //      filter.addExtension("itemtextcaxhe.wdb");
      //      filter.addExtension("npccache.wdb");
      //      filter.addExtension("pagetextcache.wdb");
      //      filter.addExtension("questcache.wdb");
      filter.setDescription("Search for WDB files");
      chooser.setFileFilter(filter);

      int returnVal = chooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        this.guiOpenWDB(chooser.getSelectedFile());
        this.wdbDirectory = chooser.getSelectedFile();

        this.moduleProps.put(this.WDB_DIR, chooser.getSelectedFile()
            .getAbsolutePath());
        try {
          FileOutputStream myOS = new FileOutputStream(this.propFILE);
          this.moduleProps.store(myOS, "Config file for the WDB module");
          myOS.close();
        } catch (Exception ex) {
          // do nothing
        }

      }
    } else if (source.getText().equals(WDB_Panel.BUTTON_EXP_CSV)) {
      if (this.items == null || this.items.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No WDB data available for the export\n"
                + "Please open a WDB file before you try to export", "Warning "
                + WDBearManager.VERSION_INFO, JOptionPane.WARNING_MESSAGE);
        return;
      }

      JFileChooser chooser = new JFileChooser(this.csvDirectory);
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returnVal = chooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
          String msg = WriteCSV.writeCSV(chooser.getSelectedFile(), this.items);
          JOptionPane.showMessageDialog(this, msg, "Info "
              + WDBearManager.VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);
        } catch (WDBMgr_ErrorWritingCSVFile ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
        }
        this.csvDirectory = chooser.getSelectedFile();
        this.moduleProps.put(this.CSV_DIR, chooser.getSelectedFile()
            .getAbsolutePath());
        try {
          FileOutputStream myOS = new FileOutputStream(this.propFILE);
          this.moduleProps.store(myOS, "Config file for the WDB module");
          myOS.close();
        } catch (Exception ex) {
          // do nothing
        }
      }
    } else if (source.getText().equals(WDB_Panel.BUTTON_EXP_AS_TXT)) {
      if (this.items == null || this.items.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No WDB data available for the export\n"
                + "Please open a WDB file before you try to export", "Warning "
                + WDBearManager.VERSION_INFO, JOptionPane.WARNING_MESSAGE);
        return;
      }

      JFileChooser chooser = new JFileChooser(this.txtDirectory);
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int returnVal = chooser.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
          String msg = WriteTXT.writeTXT(chooser.getSelectedFile(), this.items);
          JOptionPane.showMessageDialog(this, msg, "Info "
              + WDBearManager.VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);
        } catch (WDBMgr_ErrorWritingTXTFile ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
        }
        this.txtDirectory = chooser.getSelectedFile();
        this.moduleProps.put(this.TXT_DIR, chooser.getSelectedFile()
            .getAbsolutePath());
        try {
          FileOutputStream myOS = new FileOutputStream(this.propFILE);
          this.moduleProps.store(myOS, "Config file for the SQL module");
          myOS.close();
        } catch (Exception ex) {
          // do nothing
        }

      }
    } else if (source.getText().equals(WDB_Panel.BUTTON_IMP_DB)) {
      if (this.items == null || this.items.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "No WDB data available for the import\n"
                + "Please open a WDB file before you try to import", "Warning "
                + WDBearManager.VERSION_INFO, JOptionPane.WARNING_MESSAGE);
        return;
      }
      try {
        // first dto -> to identify the data
        Iterator itWDBS = this.items.iterator();
        DTO_Interface myDTO = null;
        if (itWDBS.hasNext()) {
          myDTO = (DTO_Interface) itWDBS.next();
        }
        boolean allUpd = false;
        if (this.updButton.isSelected()) {
          allUpd = true;
        }
        /*
        // START HERE
        Date startDate = new Date();
        ObjectsWritten obWr = this.myWoWWDBManager_API.insertOrUpdateToSQLDB( this.items, allUpd);
        .out.println(  "NumErrorInsert: "+ (obWr.getNumErrorInsert()));
        .out.println(  "NumErrorUpdate: "+ (obWr.getNumErrorUpdate()));
        .out.println(  "NumInsert: "+ (obWr.getNumInsert()));
        .out.println(  "NumSkipped: "+ (obWr.getNumSkipped()));
        .out.println(  "NumUpdate: "+ (obWr.getNumUpdate()));
        Date endDate = new Date();
        long millies = endDate.getTime() - startDate.getTime();
        .out.println("Processed: "+((this.items.size()*1000)/millies)+" objects per second ");
        // END HERE
        */

        PBar_SQLImport mySQLImp = new PBar_SQLImport(this.myWoWWDBManager_API,
            null);
        mySQLImp.setObjects(this.items, allUpd);
        WDB_ProgressBar myPB = new WDB_ProgressBar(this, mySQLImp, "WDB-Import");
        myPB.start();

      } catch (Throwable ex) {
        JOptionPane.showMessageDialog(this, "Database import failed\n"
            + "System message: '" + ex.getMessage() + "'", "Error "
            + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
        return;
      }
      /*
       try {
       // first dto -> to identify the data
       Iterator itWDBS = this.items.iterator();
       DTO_Interface myDTO = null;
       if (itWDBS.hasNext()) {
       myDTO = (DTO_Interface) itWDBS.next();
       }
       boolean allUpd = false;
       if( this.updButton.isSelected() ) {
       allUpd = true;
       }
       ObjectsWritten myOWr = this.myWoWWDBManager_API.insertOrUpdateToSQLDB(this.items,
       allUpd);
       JOptionPane.showMessageDialog(this, "Database import successfull\n"
       + "INSERT: "
       + myOWr.getNumInsert() + "\n" + "UPDATE: " + myOWr.getNumUpdate()
       + "\n" + "Error INSERT: " + myOWr.getNumErrorInsert() + "\n"
       + "Error UPDATE: " + myOWr.getNumErrorUpdate() + "\n"
       + "Objects skipped (if update was not selected) : "
       + myOWr.getNumSkipped(), "Info " + WDBManager.VERSION_INFO,
       JOptionPane.INFORMATION_MESSAGE);
       } catch (WDBMgr_MissingConfigParameterException ex) {
       //ex.printStackTrace();
       JOptionPane.showMessageDialog(this,
       "Database import failed\n"
       + "Please check your DB-Config file: '"+this.myWoWWDBManager_API.getDBconfigFile() +"'",
       "Error " + WDBManager.VERSION_INFO,
       JOptionPane.ERROR_MESSAGE);
       return;
       }catch( WDBMgr_CouldNotCreateSchemaException ex ) {
       JOptionPane.showMessageDialog(this, "Database trouble - Maybe down or not capable\n"+
       "The database could not create the table to store information\n\n"+
       "DB-Message: "+ex.getMessage()+"\n\n"
       +"Please report this error to your admin(screenshot)\n"
       +WDBManager.EMAIL
       , "Error " + WDBManager.VERSION_INFO,
       JOptionPane.ERROR_MESSAGE);
       }catch( WDBMgr_Exception ex ) {
       JOptionPane.showMessageDialog(this,
       "Database import failed\n"
       + ex.getMessage(),
       "Error " + WDBManager.VERSION_INFO,
       JOptionPane.ERROR_MESSAGE);
       return;
       }
       */
    } else {
      System.exit(0);
    }

  }// actionPerformed

  private void setStatus(String parStatus) {
    this.statusField.setText(parStatus);
  }

  /**
   * Called by the ProgressBar<br/>
   * 
   * @param parObj
   */
  public void taskFinished(Object parObj, long parMillies) {
    ObjectsWritten myOWr = (ObjectsWritten) parObj;
    if (myOWr != null) {
      int numObjects = myOWr.getNumInsert()+myOWr.getNumUpdate()
      +myOWr.getNumErrorInsert()
      +myOWr.getNumErrorUpdate()
      +myOWr.getNumSkipped();

      JOptionPane.showMessageDialog(this, "Database import successfull\n"
          + "INSERT: " + myOWr.getNumInsert() + "\n" + "UPDATE: "
          + myOWr.getNumUpdate() + "\n" + "Error INSERT: "
          + myOWr.getNumErrorInsert() + "\n" + "Error UPDATE: "
          + myOWr.getNumErrorUpdate() + "\n"
          + "Objects skipped (if update was not selected) : "
          + myOWr.getNumSkipped()+"\n"
          + "Processed: "+((numObjects*1000)/parMillies)+" objects per second "
					, "Info " + WDBearManager.VERSION_INFO,
          JOptionPane.INFORMATION_MESSAGE);
    }
    // else {
    // .out.println("Taskfinished -> NULL");
    //    }
  }

  /* (non-Javadoc)
   * @see com.gele.tools.wow.wdbmanager.gui.helper.pbar.WDBMgr_Caller_I#taskError(java.lang.Throwable)
   */
  public void taskError(Throwable parEx) {
    JOptionPane.showMessageDialog(this,
        "Task error\n"
            + "System message:\n"
            +parEx.getMessage(), "Error "
            + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
    return;
  }

}// WDB_Panel.java
