package com.gele.tools.wow.wdbearmanager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.python.util.PythonInterpreter;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_API;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_IOException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoDataAvailableException;
import com.gele.tools.wow.wdbearmanager.gui.Python_Panel;
import com.gele.tools.wow.wdbearmanager.gui.SQL_Panel;
import com.gele.tools.wow.wdbearmanager.gui.WDB_Panel;
import com.gele.tools.wow.wdbearmanager.gui.helper.JEPyperlinkListener;
import com.gele.tools.wow.wdbearmanager.helper.DBUpdater;
import com.gele.tools.wow.wdbearmanager.inout.ObjectsWritten;
import com.gele.tools.wow.wdbearmanager.inout.SCPWritten;
import com.gele.tools.wow.wdbearmanager.inout.csv.WriteCSV;
import com.gele.tools.wow.wdbearmanager.inout.sql.SQLManager;
import com.gele.tools.wow.wdbearmanager.inout.txt.WriteTXT;
import com.gele.tools.wow.wdbearmanager.plugin.WDBearPlugin;
import com.gele.utils.ReadPropertiesFile;
import com.jgoodies.plaf.LookUtils;
import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.DesertBlue;

/*
 * Created on 09.01.2005
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
 * $LastChangedDate: 2006-09-15 01:45:59 +0200 (Fr, 15 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 218 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/WDBearManager.java $
 * $Rev: 218 $
 * 
 * Changes:
 * 
 * PRE-11:
 * Error with MySQL when sending empty SQL statement (db update)
 * Can happen, if a Jython script call was issued
 * 
 * patchSCP.xml -> Added patch for gameobjects.scp (Thx to Hochelf)
 * 
 * Added "do you want to download the update"
 * 
 * 
 * PRE-10:
 * dbupdate: Script deleted table, but it was not re-created.
 * Could be fixed by just re-starting (PRE-10 fixes this issue)
 * 
 * 
 * PRE-9:
 * Added dbupdate (Checks for DBVersion and loads patch scripts)
 * Added "check for update"
 * 
 * 
 * PRE-8
 * Added "plugin" possibility
 *  
 * 
 */

/**
 * Main class for the WDB Manager application<br/>
 * 
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 218 $
 *
 */
public class WDBearManager extends JFrame implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 7131280271829885619L;

  // Constants
  // - the name of the directory that contains all the XML config files
  public static final String WDBEAR_CONFIG = "wdbear-config";

  // Logging with Log4J
  Logger myLogger = Logger.getLogger(WDBearManager.class);

  // Property file containing settings for "auto update, check for update"
  private static String PROPS_CHECK_UPDATE = "wdbearmanager_update.properties";

  private static String KEY_UPD_FILE = "checkupdate";

  private static String KEY_WDBMGR_SITE = "visitsite";

  private static String KEY_VERSION_INFO = "versioninfo";

  // PLUGIN: START
  private String PLUGIN_NAME = "plugin";

  private String PLUGIN_CLASS = "class";

  private String PLUGIN_IMAGE = "image";

  // PLUGIN: END

  // command line params: START
  private Options options = null;

  // Name of the DB config file
  // *HACK*, to allow easy access
  private String WDB_DB_CONFIG_FILE = "wdbearmanager_sql.properties";

  //email
  public static String EMAIL = "lousy.kizura@gmail.com";

  // use GUI
  private String PARAM_GUI = "gui";

  private boolean useGUI = false;

  // specify Jython script values
  private String PARAM_JYTHON = "J";

  HashMap paramJython = new HashMap();

  // name of wdb file
  private String PARAM_WDBFILE = "wdbfile";

  // use this locale for patching, etc
  // Every WDB information is encoded with a locale, eg
  // enUS, enGB, deDE, etc
  // It is necessary to specify which locale to use. If not, the results
  // are unpredictable.
  private String PARAM_LOCALE = "locale";

  private String paramLocale = "ALL";

  String paramWdbfile = "";

  // name of database config file
  private String PARAM_DBCONFIG = "dbconfig";

  String paramDBconfig = "";

  // folder to write the csv to
  private String PARAM_CSVFOLDER = "csvfolder";

  String paramCSVFolder = "";

  // folder to write the csv to
  private String PARAM_TXTFOLDER = "txtfolder";

  String paramTXTFolder = "";

  // write wdb into SQL database?
  private String PARAM_WRITE_SQL = "storeDB";

  boolean writeSQL = false;

  // verbose mode?
  private String PARAM_VERBOSE = "verbose";

  boolean useVerbose = false;

  // do update?
  private String PARAM_UPDATE = "update";

  boolean doUpdate = false;

  // write UTF-8 while patching SCP files
  private String PARAM_PATCH_UTF8 = "patchUTF8";

  boolean patchUTF8 = true;

  // PATCH SCP - START

  // -patchSCP quest -SCPname myQuests.scp
  // name of the SCP file to patch
  private String SCP_NAME = "SCPname";

  String paramSCPname = "";

  public final static String PATCH_SCP = "patchSCP";

  String paramPatchSCP = "";

  //
  // PATCH SCP - END

  // User can specify a Jython script
  private String PARAM_SCRIPT = "script";

  private String paramScript = "";

  public static final String VERSION_INFO = ">> WDBearManager 2006-09-14 by kizura // 1.2.7-W1.12.0 <<";

  // If the database schema has changed - update this to the latest version number
  // This constant is used to write WDBTXT and to import it
  public static final String COMPATIBILITY_INFO = "1.2.6-W1.12.0";

  // command line params: END

  // Menu items
  //  private static String MENU_OPEN = "Open";
  //
  //  private static String MENU_EXPORT = "Export";

  private static String MENU_EXIT = "Exit";

  private static String MENU_ABOUT = "About";

  private static String MENU_HELP = "Help";

  private static String MENU_JDOCS = "JavaDocs";

  // See it update is available
  private static String MENU_CHECKUPDATE = "check for update";

  JPanel myPanel = new JPanel();

  JTable myJTable = new JTable();

  // Directory for the "open wdb" dialog
  File wdbDirectory = new File("");

  // Directory for the "write csv" dialog
  File csvDirectory = new File("");

  // Contains all read infos from WDB file
  Collection items = null;

  public void parseCommandLine(String[] parArgs) {
    // parse the command line
    this.options = new Options();

    // flags
    Option optTmp = new Option(this.PARAM_GUI, false, "use GUI");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    optTmp = new Option(this.PARAM_PATCH_UTF8, false,
        "write UTF-8 text when patching SCP files (not recommended for WadEmu)");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    optTmp = new Option(this.PARAM_UPDATE, false,
        "update existing database entries");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    optTmp = new Option(this.PARAM_WRITE_SQL, false,
        "write to SQL database, needs '" + this.PARAM_WDBFILE + "'");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    optTmp = new Option(this.PARAM_VERBOSE, false, "verbose mode, print info");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    // key-value
    optTmp = new Option(
        this.PARAM_DBCONFIG,
        true,
        "specify alternative database properties; default: 'wdbearmanager_sql.properties'");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    optTmp = new Option(this.PARAM_WDBFILE, true, "name of wdb file to import");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    optTmp = new Option(this.PARAM_LOCALE, true,
        "locale to be used(eg: enUS, deDE, etc) "
            + "This is needed if you want to patch SCP files or export WDBTEXT");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    optTmp = new Option(this.PARAM_CSVFOLDER, true,
        "output CSV to this folder, needs '" + this.PARAM_WDBFILE + "'");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);
    optTmp = new Option(this.PARAM_TXTFOLDER, true,
        "output TXT to this folder, needs '" + this.PARAM_WDBFILE + "'");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);
    optTmp = new Option(this.SCP_NAME, true,
        "name of the SCP file to patch, use this in conjunction with '"
            + WDBearManager.PATCH_SCP + "'");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);
    optTmp = new Option(WDBearManager.PATCH_SCP, true,
        "type of the SCP file to patch, eg 'quests', 'items', 'creatures', or 'pages'");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    optTmp = new Option(
        this.PARAM_SCRIPT,
        true,
        "specify a Jython script, receives 'wdbmgrapi' variable which contains WDBearManager_API instance");
    optTmp.setRequired(false);
    this.options.addOption(optTmp);

    Option property = OptionBuilder.withArgName("parameter=value").hasArg()
        .withValueSeparator('^').withDescription("set value for Jython script")
        .create(this.PARAM_JYTHON);

    property.setRequired(false);
    this.options.addOption(property);

    //
    // Parse

    CommandLineParser parser = new GnuParser();
    CommandLine cmdLine = null;
    try {
      cmdLine = parser.parse(this.options, parArgs);
    } catch (ParseException ex) {
      //ex.printStackTrace();
      usage(this.options);
      System.exit(0);
    }

    if (cmdLine.hasOption(this.PARAM_JYTHON)) {
      // initialise the member variable
      String[] params = cmdLine.getOptionValues(this.PARAM_JYTHON);
      for (int i = 0; i < params.length; i++) {
        String[] result = params[i].split("=");
        if (result.length != 2) {

          this.myLogger.error("Error in specifying -J parameter '" + params[i]
              + "': Use param=value");
          System.exit(0);
        }
        this.paramJython.put(result[0], result[1]);
      }
    }

    // no options
    if (cmdLine.hasOption(this.PARAM_GUI)) {
      this.useGUI = true;
    }
    if (cmdLine.hasOption(this.PARAM_PATCH_UTF8)) {
      this.patchUTF8 = true;
    }

    if (cmdLine.hasOption(this.PARAM_WRITE_SQL)) {
      this.writeSQL = true;
    }
    if (cmdLine.hasOption(this.PARAM_VERBOSE)) {
      this.useVerbose = true;
    }
    if (cmdLine.hasOption(this.PARAM_UPDATE)) {
      this.doUpdate = true;
    }
    this.paramTXTFolder = cmdLine.getOptionValue(this.PARAM_TXTFOLDER,
        this.paramTXTFolder);
    this.paramCSVFolder = cmdLine.getOptionValue(this.PARAM_CSVFOLDER,
        this.paramCSVFolder);
    this.paramWdbfile = cmdLine.getOptionValue(this.PARAM_WDBFILE,
        this.paramWdbfile);
    this.paramLocale = cmdLine.getOptionValue(this.PARAM_WDBFILE,
        this.paramLocale);
    this.paramSCPname = cmdLine
        .getOptionValue(this.SCP_NAME, this.paramSCPname);
    this.paramPatchSCP = cmdLine.getOptionValue(WDBearManager.PATCH_SCP,
        this.paramPatchSCP);
    this.paramScript = cmdLine.getOptionValue(this.PARAM_SCRIPT,
        this.paramScript);

    this.paramDBconfig = cmdLine.getOptionValue(this.PARAM_DBCONFIG,
        this.paramDBconfig);

  }// parseCommandLine

  private static void usage(Options options) {

    // Use the inbuilt formatter class
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("Manage the WDBear files\n", options);
    System.out
        .println("This tool helps managing the information from the WDBear files");
    System.out
        .println("kizura, 2005\n"
            + "Be warned, this program comes with NO WARRANTY, it may not even be of any use\n"
            + "It can destroy your pc, your live, whatever, use at your own risk\n"
            + WDBearManager.VERSION_INFO);
  }

  public void init(String[] args) {

    // copy ARGs
    this.parseCommandLine(args);

    // Usefull for debugging
    this.myLogger.debug("java.version: " + System.getProperty("java.version"));
    this.myLogger.debug("java.vendor: " + System.getProperty("java.vendor"));
    this.myLogger.debug("java.vendor.url: "
        + System.getProperty("java.vendor.url"));
    this.myLogger.debug("os.name: " + System.getProperty("os.name"));
    this.myLogger.debug("os.arch: " + System.getProperty("os.arch"));
    this.myLogger.debug("os.version: " + System.getProperty("os.version"));

    if (this.paramDBconfig.length() != 0) {
      this.WDB_DB_CONFIG_FILE = this.paramDBconfig;
    }

    WDBearManager_API myAPI = new WDBearManager_API(this.WDB_DB_CONFIG_FILE);

    // Create all tables
    // -> Needed for the updata script <-
    // -> Otherwise it may crash with an empty database
    try {
      myAPI.getCountOfTable("creaturecache");
      myAPI.getCountOfTable("gameobjectcache");
      myAPI.getCountOfTable("itemcache");
      myAPI.getCountOfTable("itemnamecache");
      myAPI.getCountOfTable("itemtextchaxhe");
      myAPI.getCountOfTable("npccache");
      myAPI.getCountOfTable("pagetextcache");
      myAPI.getCountOfTable("questcache");
    } catch (Exception ex) {
      // ignore
      ex.printStackTrace();
      System.exit(0);
    }

    // Check, if database must be re-newed
    DBUpdater myDBU = new DBUpdater();
    myDBU.checkForUpdate(myAPI);

    WDBearManager_I myWoWWDBearManager_API = myAPI;

    //
    // print out some statistics
    //

    if (this.useGUI == false) {

      boolean paramSpec = false;
      // ASSERT
      DTO_Interface myDTO = null;

      if (this.paramWdbfile.length() != 0) {
        paramSpec = true;
        // Open WDB
        try {
          this.items = myWoWWDBearManager_API.readWDBfile(this.paramWdbfile);
        } catch (Exception ex) {
          this.myLogger.error("Error reading the WDB file");
          return;
        }
        // first dto -> to identify the data
        Iterator itWDBS = this.items.iterator();
        if (itWDBS.hasNext()) {
          myDTO = (DTO_Interface) itWDBS.next();
        }
      }
      // Create CSV?
      if (this.paramCSVFolder.length() != 0) {
        if (myDTO == null) {
          // NO WDB specified -> exit
          this.myLogger.error("Error: You did not specify -"
              + this.PARAM_WDBFILE + "\n");
          usage(this.options);
          return;
        }
        File csvFile = new File(this.paramCSVFolder, myDTO.getTableName()
            + ".csv");
        if (this.useVerbose) {
          this.myLogger.info("Creating CSV file: " + csvFile.getAbsolutePath());
        }
        try {
          WriteCSV.writeCSV(new File(this.paramCSVFolder), this.items);
          this.myLogger.info("CSV file written: " + csvFile.getAbsolutePath());
        } catch (Exception ex) {
          ex.printStackTrace();
          this.myLogger.error("Error writing the CSV file");
          return;
        }
      }
      // Create TXT?
      if (this.paramTXTFolder.length() != 0) {
        if (myDTO == null) {
          // NO WDB specified -> exit
          this.myLogger.error("Error: You did not specify -"
              + this.PARAM_WDBFILE + "\n");
          usage(this.options);
          return;
        }
        if (this.useVerbose) {
          String table = myDTO.getTableName();
          File txtFile = new File(this.paramTXTFolder, table + ".txt");
          this.myLogger.info("Creating TXT file: " + txtFile.getAbsolutePath());
        }
        try {
          WriteTXT.writeTXT(new File(this.paramTXTFolder), this.items);
        } catch (Exception ex) {
          //ex.printStackTrace();
          this.myLogger.error("Error writing the TXT file: " + ex.getMessage());
          return;
        }
      }
      // Store inside SQL database?
      if (this.writeSQL == true) {
        paramSpec = true;
        if (myDTO == null) {
          // NO WDB specified -> exit
          this.myLogger.error("Error: You did not specify -"
              + this.PARAM_WDBFILE + "\n");
          usage(this.options);
          return;
        }
        if (this.useVerbose) {
          this.myLogger.info("Storing data inside SQL database");
        }
        SQLManager myWriteSQL = null;
        try {
          myWriteSQL = new SQLManager(this.WDB_DB_CONFIG_FILE);
          ObjectsWritten myOWr = myWriteSQL.insertOrUpdateToSQLDB(this.items,
              this.doUpdate);
          this.myLogger.info("Operation successfull");
          if (this.useVerbose) {
            this.myLogger.info("DB statistics");
            this.myLogger.info("INSERT: " + myOWr.getNumInsert());
            this.myLogger.info("UPDATE: " + myOWr.getNumUpdate());
            this.myLogger.info("Error INSERT: " + myOWr.getNumErrorInsert());
            this.myLogger.info("Error UPDATE: " + myOWr.getNumErrorUpdate());
            if (this.doUpdate == false) {
              this.myLogger.info("Objects skipped: " + myOWr.getNumSkipped());
              System.out
                  .println("If you want to overwrite/update objects, use 'update' param");
            }
            this.myLogger.info(WDBearManager.VERSION_INFO);
          }
        } catch (Exception ex) {
          ex.printStackTrace();
          this.myLogger.error("Error importing to database");
          this.myLogger.error(ex.getMessage());
          return;
        }
      }// writeSQL

      // Patch *.SCP with contents of database
      if (this.paramSCPname.length() != 0) {
        if (this.paramPatchSCP.length() == 0) {
          this.myLogger.error("Error: You did not specify -"
              + WDBearManager.PATCH_SCP + "\n");
          usage(this.options);
          return;
        }

        paramSpec = true;
        this.myLogger.info("Patch scp file with the contents of the database");
        try {

          SCPWritten mySCPW = myWoWWDBearManager_API.patchSCP(
              this.paramSCPname, this.paramPatchSCP, this.patchUTF8,
              this.paramLocale);
          if (this.useVerbose) {
            this.myLogger.info("Merge statistics");
            System.out
                .println("Entries in database:    " + mySCPW.getNumInDB());
            this.myLogger.info("Merged with SCP: " + mySCPW.getNumPatched());
            this.myLogger.info("Patched IDs:      " + mySCPW.getPatchedIDs());
          }
          this.myLogger.info("Patched file: " + this.paramSCPname + "_patch");
        } catch (WDBMgr_IOException ex) {
          this.myLogger.error("The destination SCP file could not be created");
          this.myLogger.error(ex.getMessage());
          return;
        } catch (WDBMgr_NoDataAvailableException ex) {
          this.myLogger.info("Merging impossible");
          this.myLogger.info("There are no entries inside the database");
        } catch (Exception ex) {
          this.myLogger.error("Error while merging quests.scp with database");
          this.myLogger.error(ex.getMessage());
          return;
        }
      }// PatchSCP

      // Call jython script?
      if (this.paramScript.length() != 0) {
        paramSpec = true;
        this.myLogger.info("Calling Jython script");
        this.myLogger.info("---");
        PythonInterpreter interp = new PythonInterpreter();

        interp.set("wdbmgrapi", myWoWWDBearManager_API);
        // set parameters
        Set setKeys = this.paramJython.keySet();
        Iterator itKeys = setKeys.iterator();
        String jyParam = "";
        while (itKeys.hasNext()) {
          jyParam = (String) itKeys.next();
          interp.set(jyParam, (String) this.paramJython.get(jyParam));
        }
        interp.execfile(this.paramScript);

        this.myLogger.info("---");
        System.out.println("Jython script executed, "
            + WDBearManager.VERSION_INFO);
        return;
      }// paramScript

      if (paramSpec == false) {
        usage(this.options);
        return;
      }

      // Exit
      return;
    }// Command Line Version

    //
    // GUI
    //
    PlasticLookAndFeel.setMyCurrentTheme(new DesertBlue());
    try {
      UIManager.put("ClassLoader", LookUtils.class.getClassLoader());
      UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
    } catch (Exception e) {
    }
    //    try {
    //      com.incors.plaf.kunststoff.KunststoffLookAndFeel kunststoffLnF = new com.incors.plaf.kunststoff.KunststoffLookAndFeel();
    //      KunststoffLookAndFeel
    //          .setCurrentTheme(new com.incors.plaf.kunststoff.KunststoffTheme());
    //      UIManager.setLookAndFeel(kunststoffLnF);
    //    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
    //      // handle exception or not, whatever you prefer
    //    }
    //     this line needs to be implemented in order to make JWS work properly
    UIManager.getLookAndFeelDefaults().put("ClassLoader",
        getClass().getClassLoader());

    this.setTitle(WDBearManager.VERSION_INFO);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.getContentPane().setLayout(new BorderLayout());

    // construct GUI to display the stuff
    // Menu
    //  Where the GUI is created:
    JMenuBar menuBar;
    JMenu menu; //, submenu;
    JMenuItem menuItem;
    //JRadioButtonMenuItem rbMenuItem;
    //JCheckBoxMenuItem cbMenuItem;

    //    Create the menu bar.
    menuBar = new JMenuBar();

    //    Build the first menu.
    menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_A);
    menu.getAccessibleContext().setAccessibleDescription("Process WDB files");
    menuBar.add(menu);

    // Exit
    menuItem = new JMenuItem(MENU_EXIT, KeyEvent.VK_T);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
        ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Exit program");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    //    Build the first menu.
    menu = new JMenu("About");
    menu.setMnemonic(KeyEvent.VK_A);
    menu.getAccessibleContext().setAccessibleDescription("Whassup?");
    menuBar.add(menu);
    // Help
    menuItem = new JMenuItem(MENU_HELP, KeyEvent.VK_H);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
        ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Help me...");
    menuItem.addActionListener(this);
    menu.add(menuItem);
    // JavaDocs
    menuItem = new JMenuItem(MENU_JDOCS, KeyEvent.VK_J);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J,
        ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext()
        .setAccessibleDescription("Show API docs...");
    menuItem.addActionListener(this);
    menu.add(menuItem);
    // separator
    menu.addSeparator();
    // CheckForUpdate
    menuItem = new JMenuItem(MENU_CHECKUPDATE, KeyEvent.VK_U);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
        ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription(
        "Check for update...");
    menuItem.addActionListener(this);
    menu.add(menuItem);
    // separator
    menu.addSeparator();
    // ABOUT
    menuItem = new JMenuItem(MENU_ABOUT, KeyEvent.VK_T);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
        ActionEvent.ALT_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Ueber...");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    this.setJMenuBar(menuBar);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(0, 1));

    JTabbedPane tabbedPane = new JTabbedPane();
    ImageIcon wowIcon = createImageIcon("images/fromdisk.gif");

    JComponent panel1 = new WDB_Panel(myWoWWDBearManager_API);
    tabbedPane.addTab("WDB-Module", wowIcon, panel1, "Handle WDB files");
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

    ImageIcon panelIcon = createImageIcon("images/hsql.gif");
    JComponent panel2 = null;
    try {
      panel2 = new SQL_Panel(myWoWWDBearManager_API);
    } catch (Throwable ex) {
      System.err.println("Error while instantiating SQL Panel: ");
      System.err.println(ex.getMessage());
      ex.printStackTrace();
      System.exit(0);
    }
    tabbedPane.addTab("DB-Module", panelIcon, panel2, "Handle database");
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

    panelIcon = createImageIcon("images/pythonpoweredsmall.gif");
    JComponent panel3 = new Python_Panel(myWoWWDBearManager_API);
    tabbedPane.addTab("Scripts", panelIcon, panel3, "Scripting");
    tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

    // maybe user PLUGIN availabe
    // -> check for folders below "plugins"
    File filUserPanels = new File("plugins");
    // 1) find user plugins (scan for directories)
    // 2) scan for <name>.properties, where <name> is the name of the directory
    // 3) load the properties file and get the plugin running
    String[] strUserPlugins = filUserPanels.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return (new File(dir, name).isDirectory());
      }
    });
    if (strUserPlugins != null) {
      ArrayList urlJars = new ArrayList();

      //URL[] urlJars = new URL[strUserPlugins.length];
      String strCurrJar = "";
      String strPlugins[] = new String[strUserPlugins.length];
      try {
        for (int i = 0; i < strUserPlugins.length; i++) {
          File baseFile = new File("plugins", strUserPlugins[i]);
          File filProperties = new File(baseFile, strUserPlugins[i]
              + ".properties");
          if (filProperties.exists() ) {
            // set plugin folder and .properties name
            strPlugins[i] = strUserPlugins[i];
            this.myLogger
                .info("Found 'plugin' : " + baseFile.getAbsolutePath());
            this.myLogger.info("                 Trying to load .jar file");

            // Scan for JAR files and include them
            //System.out.println(baseFile.getAbsolutePath());
            String[] strJars = baseFile.list(new FilenameFilter() {
              public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
              }
            });
            for( int j=0; j<strJars.length; j++ ) {
              File filJAR = new File(baseFile, strJars[j]);
              strCurrJar = filJAR.getAbsolutePath();

              this.myLogger.info("Loading external 'plugin' JAR: " + strCurrJar);
              URL jarfile = new URL("jar", "", "file:" + strCurrJar + "!/");
              urlJars.add(jarfile);
            }

          } else {
            // print warning - a directory inside plugins, but there is no plugin
            this.myLogger
                .warn("Found directory inside plugins folder, but no .properties file");
            this.myLogger.warn("      Name of directory: " + strUserPlugins[i]);
            this.myLogger.warn("      Please review the directory!");
          }
        }// for... all user plugins
      } catch (Exception ex) {
        this.myLogger.error("Plugin: Error loading " + strCurrJar);
        this.myLogger.error("Please check your 'plugin' folder");
      }
      URLClassLoader cl = null;
      try {
        //      File file = new File("plugins", strUserJars[i]);
        //      this.myLogger.info("Lade externes JAR: " + file.getAbsolutePath());
        //      URL jarfile = new URL("jar", "", "file:" + file.getAbsolutePath() + "!/");
        URL[] loadURLs = new URL[ urlJars.toArray().length ];
        for( int j = 0; j<urlJars.toArray().length; j++ ) {
          loadURLs[j] = (URL) urlJars.get(j);
        }
        cl = URLClassLoader.newInstance(loadURLs);

        Thread.currentThread().setContextClassLoader(cl);

        //      String lcStr = "Test";
        //      Class loadedClass = cl.loadClass(lcStr);
        //      this.myLogger.info("Smooth...");

      } catch (Exception ex) {
        ex.printStackTrace();
      }

      // 2) load properties and instantiate the plugin
      //      String[] strPlugins = filUserPanels.list(new FilenameFilter() {
      //        public boolean accept(File dir, String name) {
      //          return (name.endsWith("_plugin.properties"));
      //        }
      //      });
      String strPluginName = "";
      String strPluginClass = "";
      String strPluginImage = "";
      WDBearPlugin pluginPanel = null;
      for (int i = 0; i < strPlugins.length; i++) {
        //this.myLogger.info(strPlugins[i]);
        Properties prpPlugin = null;
        try {
          prpPlugin = ReadPropertiesFile.readProperties(new File("plugins",
              strPlugins[i] + "/" + strPlugins[i]).getAbsolutePath()
              + ".properties");
        } catch (Exception ex) {
          this.myLogger.error("Error with plugin: " + strPlugins[i]);
          this.myLogger.error("Could not load properties file");
          continue;
        }
        if ((strPluginClass = prpPlugin.getProperty(this.PLUGIN_CLASS)) == null) {
          this.myLogger.error("Error with plugin: " + strPlugins[i]);
          this.myLogger.error("Property '" + this.PLUGIN_CLASS + "' not found");
          continue;
        }
        if ((strPluginName = prpPlugin.getProperty(this.PLUGIN_NAME)) == null) {
          this.myLogger.error("Error with plugin: " + strPlugins[i]);
          this.myLogger.error("Property '" + this.PLUGIN_NAME + "' not found");
          continue;
        }
        if ((strPluginImage = prpPlugin.getProperty(this.PLUGIN_IMAGE)) == null) {
          this.myLogger.error("Error with plugin: " + strPlugins[i]);
          this.myLogger.error("Property '" + this.PLUGIN_IMAGE + "' not found");
          continue;
        }

        File filPlgImg = new File("plugins", strPlugins[i] + "/"
            + strPluginImage);
        panelIcon = createImageIcon(filPlgImg.getAbsolutePath());
        if (panelIcon == null) {
          this.myLogger.error("Error with plugin: " + strPlugins[i]);
          this.myLogger.error("Could not read image '" + strPluginImage + "'");
          continue;
        }
        try {
          pluginPanel = (WDBearPlugin) (cl.loadClass(strPluginClass)
              .newInstance());
          pluginPanel.runPlugin(myAPI);
        } catch (Exception ex) {
          this.myLogger.error("Error with plugin: " + strPlugins[i]);
          this.myLogger.error("Could not instantiate '" + strPluginClass + "'");
          ex.printStackTrace();
          continue;
        }
        tabbedPane.addTab(strPluginName, panelIcon, pluginPanel, strPluginName);
      }// Plugins
    }// plugins folder found

    mainPanel.add(tabbedPane);

    this.getContentPane().add(mainPanel, BorderLayout.CENTER);

    this.setSize(1024, 768);
    //this.pack();
    this.show();

  }// init  /** Returns an ImageIcon, or null if the path was invalid.

  protected static ImageIcon createImageIcon(String path) {
    ImageIcon retVal = null;

    java.net.URL imgURL = WDBearManager.class.getResource(path);
    if (imgURL != null) {
      retVal = new ImageIcon(imgURL);
      if (retVal != null) {
        return retVal;
      } else {
        System.err.println("createImageIcon: Couldn't find file: " + path);
        return null;
      }
    }
    File imgFile = new File(path);
    try {
      imgURL = imgFile.toURL();
      retVal = new ImageIcon(imgURL);
      return retVal;
    } catch (Exception ex) {
      System.err.println("createImageIcon: Couldn't create IMG URL for file: "
          + path);
      return null;
    }
  }

  public String test() {
    return "Nase";
  }

  /*
   * Either connects to blizzard bnet pages to read the stats
   * or reads html pages offline.
   * eg:
   * "file://myOfflinePage.html"
   * -> Somebody saved the html page and now wants to extract the stats
   *    infos
   * "lousyplayer"
   * -> (Without 'file://') Indicates the system to connect to
   *    blizzard's bnet page an read the stats online.
   *  
   * @param args see above
   */
  public static void main(String[] args) {

    /*
     byte myByte = (byte)129;
     // 0xff -> unsigned
     System.out.println(myByte & 0xff);
     System.exit(0);
     // Test -> Export der Daten in TXT Datei
     WoWWDBManager_API myAPI = new WoWWDBManager_API();
     String tabName = "questcache";
     File filOutDir = new File("c:\\temp");
     
     
     // Import
     try {
     DTO_Interface objDTO = myAPI.createDTOfromTable(tabName);
     Iterator itCols = objDTO.getColumns();
     int iColNum = objDTO.getColumnsSize();
     String[] arrColumns = new String[iColNum];
     int counter = 0;
     String strColName = "";
     while( itCols.hasNext() ) {
     arrColumns[counter] = (String)itCols.next();
     counter++;
     }

     
     BufferedReader bwrIn = new BufferedReader( new FileReader(new File(filOutDir, tabName+".WDBTXT")));
     DTO_Interface dtoLoop = null;
     String readLine = "";
     
     String strValue = "";
     Vector vecWriteDTOs = new Vector();
     while( (readLine = bwrIn.readLine()) != null ) {
     dtoLoop = objDTO.createObject();
     if( readLine.startsWith( "["+tabName )) {
     strValue = (readLine.substring( ("["+tabName).length(), readLine.length()-1)).trim();
     //System.out.println("PKEY: '"+strValue+"'");
     dtoLoop.setPrimaryKeyValue(new Integer(strValue));

     // Start with 1, since 0 is primary key
     for( int i = 1; i<iColNum; i++) {
     readLine = bwrIn.readLine();
     if( readLine == null ) {
     System.err.println("Voreitiges Dateiende");
     System.exit(0);
     }
     // check for valid line
     if( readLine.startsWith(arrColumns[i]+"=") == false ) {
     System.err.println(readLine);
     System.err.println("Falsches Format: Erwarte "+arrColumns[i]+"=");
     System.exit(0);
     }
     // ok, set the attribute
     //System.out.println(readLine);
     strValue = (readLine.substring( (arrColumns[i]+"=").length(), readLine.length())).trim();
     dtoLoop.setColumnValue(arrColumns[i], strValue );
     // store it
     }
     vecWriteDTOs.addElement(dtoLoop);

     }else {
     // ignore
     }
     }// while... readLine
     System.out.println("Anzahl gelesen aus WDBTXT: "+ vecWriteDTOs.size());
     
     ObjectsWritten objWritten = myAPI.insertOrUpdateToSQLDB( vecWriteDTOs, false);
     System.out.println("error insert: " +objWritten.getNumErrorInsert());
     System.out.println("      insert: " +objWritten.getNumInsert());
     System.out.println("skipped     : " +objWritten.getNumSkipped());
     System.out.println("update      : " +objWritten.getNumUpdate());
     
     
     }catch( Exception ex) {
     ex.printStackTrace();
     System.exit(0);
     }
     System.exit(0);

     
     // Export
     try {
     String newLine = System.getProperty("line.separator");
     DTO_Interface objDTO = myAPI.createDTOfromTable(tabName);
     Iterator itCols = objDTO.getColumns();
     int iColNum = objDTO.getColumnsSize();
     String[] arrColumns = new String[iColNum];
     int counter = 0;
     String strColName = "";
     while( itCols.hasNext() ) {
     arrColumns[counter] = (String)itCols.next();
     counter++;
     }
     
     int numObjs = myAPI.getCountOf(objDTO);
     if( numObjs == 0 ) {
     System.out.println("No objects inside the database - exit");
     }
     Collection colObjs = myAPI.getAllObjects(objDTO);
     
     System.out.println("Exporting to WDBTXT -> "+ colObjs.size());
     Iterator itDTOs = colObjs.iterator();
     
     BufferedWriter bwrOut = new BufferedWriter( new FileWriter(new File(filOutDir, tabName+".WDBTXT")));
     DTO_Interface dtoLoop = null;
     while( itDTOs.hasNext() ) {
     dtoLoop = (DTO_Interface)itDTOs.next();
     bwrOut.write( "["+ tabName + " "+ dtoLoop.getPrimaryKeyValue().toString() +"]"+newLine );
     // Start with 1, since 0 is primary key
     for( int i = 1; i<iColNum; i++) {
     bwrOut.write( arrColumns[i]+"="+dtoLoop.getColumnValue(arrColumns[i]).toString()+newLine );
     }
     bwrOut.write( newLine );
     }
     bwrOut.close();
     }catch( Exception ex ) {
     ex.printStackTrace();
     System.exit(0);
     }
     System.exit(0);
     */

    //    try {
    //    	PBarTest myPBT = new PBarTest();
    //    }catch( Exception ex ) {
    //      ex.printStackTrace();
    //    }
    //    this.myLogger.info("Und weg");
    //    System.exit(0);
    //    PythonInterpreter interp = new PythonInterpreter();
    //
    //    WoWWDBManager_API myWoWWDBManager_API = new WoWWDBManager_API();
    //    
    //    interp.set("wdbmgrapi", myWoWWDBManager_API);
    //    interp.exec("print wdbmgrapi.getCountOf('questcache') ");
    //    WoWWDBManager_API myAPI = new WoWWDBManager_API();
    /*
     URL tranURL = null;
     try {
     tranURL = new URL("http", "wowger.wo.funpic.de",
     "/import_original_existing.php?num="+questID+"&translateversion=ts21");

     BufferedReader in = new BufferedReader(new InputStreamReader(tranURL
     .openStream()));

     String inputLine;
     inputLine = in.readLine();
     if (inputLine.equals("406")) {
     System. out.println("Der Quest wurde nicht gefunden");
     } else {
     System. out.println("Der Quest ist in der Datenbank vorhanden");
     }

     while ((inputLine = in.readLine()) != null) {
     System. out.println(inputLine);
     }
     in.close();
     
     // Überschreiben
     tranURL = new URL("http", "wowger.wo.funpic.de",
     "/import_original.php?"+questURL);
     in = new BufferedReader(new InputStreamReader(tranURL
     .openStream()));

     inputLine = in.readLine();
     System. out.println("Rückgabewert: "+ inputLine);

     while ((inputLine = in.readLine()) != null) {
     System. out.println(inputLine);
     }
     in.close();
     

     in.close();
     } catch (MalformedURLException ex) {
     ex.printStackTrace();
     System.exit(0);
     } catch (IOException ex) {
     ex.printStackTrace();
     System.exit(0);
     }

     //System.exit(0);
     */

    //    try {
    //      String tabName = "questcache";
    //      String colOrder = "wdb_name";
    //      
    //      this.myLogger.info( myAPI.getDatabaseMetaData().getDatabaseProductName() );
    //
    //      DTO_Interface dto = myAPI.createDTOfromTable(tabName);
    //      // Prepare the query to the database
    //      dto.setColumnValue("wdb_name", "*K*");
    //      
    //      this.myLogger.info( myAPI.getCountOf(dto) );
    //
    //      // Order BY
    //      DTO_Interface orderDTO = myAPI.createDTOfromTable(tabName);
    //      // Prepare the query to the database
    //      orderDTO.setColumnValue(colOrder, "ASC");
    //
    //      Collection objQuestcache = myAPI.getAllObjects(dto, orderDTO);
    //    } catch (Exception ex) {
    //      ex.printStackTrace();
    //    }
    //    System.exit(0);
    /*
     int questID = 61;
     String questURL = "";
     try {
     DTO_Interface myQuest = myAPI.createDTOfromTable("questcache");
     // Make 1:1 copy with all attributes
     DTO_Interface queryQuest = myQuest.createObject();
     // Retrieve only the object with "wdb_questid=1368"
     queryQuest.setColumnValue("wdb_QuestId", new Integer(questID));
     Collection colQuests = myAPI.getAllObjects(queryQuest);
     Iterator itQuests = colQuests.iterator();
     DTO_Interface quest1368 = null;
     if (itQuests.hasNext()) {
     quest1368 = (DTO_Interface) itQuests.next();
     } else {
     this.myLogger.error( "error: could not retrieve quest: "+ questID);
     System.exit(0);
     }
     //quest1368.setColumnValue("wdb_desc", "#"+quest1368.getColumnValue("wdb_desc"));
     System. out.println(questURL=myAPI.createTranslatorURLfromDTO(true, quest1368,
     "test", "test", "ts21"));

     } catch (Exception ex) {
     ex.printStackTrace();
     System.exit(0);
     }
     //System.exit(0);
     */
    // Instance of the program itself
    WDBearManager myApp = new WDBearManager();
    myApp.init(args);

  }// main

  /* (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent arg0) {
    JMenuItem source = (JMenuItem) (arg0.getSource());

    if (source.getText().equals(MENU_ABOUT)) {
      JOptionPane
          .showMessageDialog(
              this,
              WDBearManager.VERSION_INFO
                  + "\n"
                  + "by kizura\n"
                  + WDBearManager.EMAIL
                  + "\n\n"
                  + "\n"
                  + "Supports any WDB version\n"
                  + "\n"
                  + "Thanks to:\n"
                  + "DarkMan for testing,\n"
                  + "John for the 1.10 specs, Annunaki for helping with the header,\n"
                  + "Andrikos for the 1.6.x WDB specs, Pyro's WDBViewer, WDDG Forum,\n"
                  + "blizzhackers, etc etc\n\n"
                  + "This program uses: \n"
                  + "JGoodies from http://www.jgoodies.com/\n"
                  + "Hypersonic SQL database 1.7.3\n"
                  + "Apache Log4J, Xerces\n"
                  + "Jakarta Commons Logging and CLI\n"
                  + "Castor from ExoLab\n"
                  + "MySQL JDBC connector 3.1.7\n"
                  + "HTTPUNIT from Russell Gold\n"
                  + "Jython 2.1\n"
                  + "Refer to directory 'licenses' for more details about the software used\n"
                  + "PLEASE:\n"
                  + "If you like this program and find it usefull:\n"
                  + "Please donate money to a charity oranization of your choice.\n"
                  + "I recommend any organization that fights cancer.\n\n"
                  + "License:\n" + "WDBearMgr is placed under the GNU GPL. \n"
                  + "For further information, see the page :\n"
                  + "http://www.gnu.org/copyleft/gpl.html.\n"
                  + "See licenses/GPL_license.html\n" + "\n"
                  + "For a different license please contact the author.",
              "Info " + VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);
      return;
    } else if (source.getText().equals(MENU_HELP)) {
      JFrame myFrame = new JFrame("doc/html/index.html");
      //myFrame.setFont(   );
      URL urlHTML = null;
      try {
        JEditorPane htmlPane = new JEditorPane();
        //htmlPane.setFont(sourceFont);
        // .out.println("/scripts/"+source.getName()+".py");
        File scriptFile = new File("doc/html/index.html");
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
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, VERSION_INFO + "\n" + "by kizura\n"
            + WDBearManager.EMAIL + "\n\n"
            + "Could not open 'doc/html/index.html'",
            "Warning " + VERSION_INFO, JOptionPane.WARNING_MESSAGE);
      }
    } else if (source.getText().equals(MENU_JDOCS)) {
      JFrame myFrame = new JFrame("doc/javadoc/index.html");
      //myFrame.setFont(   );
      URL urlHTML = null;
      try {
        JEditorPane htmlPane = new JEditorPane();
        //htmlPane.setFont(sourceFont);
        // .out.println("/scripts/"+source.getName()+".py");
        File scriptFile = new File("doc/javadoc/index.html");
        urlHTML = scriptFile.toURL();
        htmlPane.setPage(urlHTML);
        htmlPane.setEditable(false);
        JEPyperlinkListener myJEPHL = new JEPyperlinkListener(htmlPane);
        htmlPane.addHyperlinkListener(myJEPHL);
        myFrame.getContentPane().add(new JScrollPane(htmlPane));
        myFrame.pack();
        myFrame.setSize(640, 480);
        myFrame.show();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, VERSION_INFO + "\n" + "by kizura\n"
            + WDBearManager.EMAIL + "\n\n"
            + "Could not open 'doc/javadoc/index.html'", "Warning "
            + VERSION_INFO, JOptionPane.WARNING_MESSAGE);
      }
    } else if (source.getText().equals(MENU_CHECKUPDATE)) {
      Properties dbProps = null;
      String filName = PROPS_CHECK_UPDATE;
      try {
        dbProps = ReadPropertiesFile.readProperties(filName);
        String updFile = dbProps.getProperty(KEY_UPD_FILE);
        if (updFile == null) {
          JOptionPane.showMessageDialog(this, VERSION_INFO + "\n"
              + "by kizura\n" + WDBearManager.EMAIL + "\n\n"
              + "Could not find update information", "Warning " + VERSION_INFO,
              JOptionPane.WARNING_MESSAGE);
          return;
        }
        String updSite = dbProps.getProperty(KEY_WDBMGR_SITE);
        if (updFile == null) {
          JOptionPane.showMessageDialog(this, VERSION_INFO + "\n"
              + "by kizura\n" + WDBearManager.EMAIL + "\n\n"
              + "Could not find SITE information", "Warning " + VERSION_INFO,
              JOptionPane.WARNING_MESSAGE);
          return;
        }

        URL urlUpdScript = new URL(updFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(
            urlUpdScript.openStream()));

        String versionTXT = in.readLine();
        String downloadName = in.readLine();
        in.close();

        if (versionTXT.equals(WDBearManager.VERSION_INFO)) {
          JOptionPane.showMessageDialog(this, VERSION_INFO + "\n"
              + "by kizura\n" + WDBearManager.EMAIL + "\n\n"
              + "You are using the latest version, no updates available",
              "Info " + VERSION_INFO, JOptionPane.INFORMATION_MESSAGE);
          return;
        } else {
          // Read version.txt
          String versionInfo = (String) dbProps.getProperty(KEY_VERSION_INFO);
          URL urlversionInfo = new URL(versionInfo);
          BufferedReader brVInfo = new BufferedReader(new InputStreamReader(
              urlversionInfo.openStream()));
          StringBuffer sbuVInfo = new StringBuffer();
          String strLine = "";
          boolean foundStart = false;
          while ((strLine = brVInfo.readLine()) != null) {
            if (strLine.startsWith("---")) {
              break;
            }
            if (foundStart == true) {
              sbuVInfo.append(strLine);
              sbuVInfo.append("\n");
              continue;
            }
            if (strLine.startsWith(versionTXT)) {
              foundStart = true;
              continue;
            }
          }
          brVInfo.close();

          int n = JOptionPane.showConfirmDialog(this,
              "New version available - Please visit " + updSite + "\n\n"
                  + versionTXT + "\n" + "\n" + "You are using version:\n"
                  + WDBearManager.VERSION_INFO + "\n" + "\n"
                  + "Do you want to download this version?\n\n"
                  + "Version information:\n" + sbuVInfo.toString(),
              VERSION_INFO + "by kizura", JOptionPane.YES_NO_OPTION);
          //          JOptionPane.showMessageDialog(this, VERSION_INFO + "\n"
          //              + "by kizura\n" + WDBManager.EMAIL + "\n\n"
          //              + "New version available - Please visit " + updSite,
          //              "Warning "
          //              + VERSION_INFO, JOptionPane.WARNING_MESSAGE);
          if (n == 0) {
            JFileChooser chooser = new JFileChooser(new File("."));
            chooser.setDialogTitle("Please select download location");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
              try {
                URL urlUpd = new URL(downloadName);
                BufferedInputStream bin = new BufferedInputStream(urlUpd
                    .openStream());
                System.out.println(new File(chooser.getSelectedFile(), urlUpd
                    .getFile()).getAbsolutePath());
                File thisFile = new File(chooser.getSelectedFile(), urlUpd
                    .getFile());
                BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(thisFile));

                byte[] bufFile = new byte[102400];
                int bytesRead = 0;
                while ((bytesRead = bin.read(bufFile)) != -1) {
                  bout.write(bufFile, 0, bytesRead);
                }
                bin.close();
                bout.close();

                JOptionPane.showMessageDialog(this,
                    "Update downloaded successfully" + "\n" + "Please check '"
                        + thisFile.getAbsolutePath() + "'", "Success "
                        + WDBearManager.VERSION_INFO,
                    JOptionPane.INFORMATION_MESSAGE);
                //String msg = WriteCSV.writeCSV(chooser.getSelectedFile(), this.items);
              } catch (Exception ex) {
                String msg = ex.getMessage();
                JOptionPane.showMessageDialog(this, msg + "\n"
                    + "Error downloading update", "Error "
                    + WDBearManager.VERSION_INFO, JOptionPane.ERROR_MESSAGE);
              }
            }
          }// user selected "download"
          return;
        }

      } catch (Exception ex) {
        ex.printStackTrace();
      }

    } else {
      System.exit(0);
    }

  }// actionPerformed}// WDBearManager
}// WDBearManager.java
