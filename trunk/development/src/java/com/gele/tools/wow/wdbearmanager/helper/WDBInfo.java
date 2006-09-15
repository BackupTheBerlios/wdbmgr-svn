/*
 * CreateDateTime: 21.02.2006, 12:46:37
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: SQLManager.java 197 2005-10-30 18:16:31Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2005-10-30 19:16:31 +0100 (So, 30 Okt 2005) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 197 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/sql/SQLManager.java $
 * $Rev: 197 $
 * 
 */

package com.gele.tools.wow.wdbearmanager.helper;

import java.util.Properties;

import com.gele.utils.ReadPropertiesFile;

/**
 * Class that contains information about a WDB file
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * 
 */
public class WDBInfo {

  // ID of the WDB file
  private String wdbID = "";

  // version of the WDB file, the wdb file was created with a client of
  // this version
  private String wdbBuildVersionHex = "";

  // Client locale (4 byte inside WDB) eg enUS, enGB, deDE
  private String clientLocale = "";

  // version of the WDB file, the wdb file was created with a client of
  // this version
  private int wdbBuildVersionDec = -1;

  // IDs of the WDB files
  /** ID for gameobjectcache.wdb */
  public static final String GAMEOBJECTCACHE = "BOGW";

  /** ID for gameobjectcache.wdb */
  public static final String CREATURECACHE = "BOMW";

  /** ID for itemcache.wdb */
  public static final String ITEMCACHE = "BDIW";

  /** ID for itemnamecache.wdb */
  public static final String ITEMNAMECACHE = "BDNW";

  /** ID for itemtextcaxhe.wdb */
  public static final String ITEMTEXTXAXHE = "XTIW";

  /** ID for npccache.wdb */
  public static final String NPCCACHE = "CPNW";

  /** ID for pagetextcache.wdb */
  public static final String PAGETEXTCACHE = "XTPW";

  /** ID for questcache.wdb */
  public static final String QUESTCACHE = "TSQW";

  /** ID for wowcache.wdb */
  public static final String WOWCACHE = "NDRW";

  // Properties
  // key: build-version
  // value: version string
  private static Properties buildProperties = null;

  private String propFILE = "wdb_compatibility.properties";

  /**
   * Init the Info class
   * 
   */
  public WDBInfo() {
    // Speed reasons
    // Only read once for the whole application's runtime
    if (buildProperties == null) {
      try {
        buildProperties = ReadPropertiesFile.readProperties(this.propFILE);
      } catch (Exception ex) {
        // do nothing
        buildProperties = new Properties();
      }
    }
  }// constructor

  /**
   * First four bytes of the WDB file<br/> <br/>
   * <ul>
   * <li>"BOGW" (42 4F 47 57) - gameobjectcache.wdb</li>
   * <li>"BOMW" (42 4F 4D 57) - creaturecache.wdb</li>
   * <li>"BDIW" (42 44 49 57) - itemcache.wdb</li>
   * <li>"BDNW" (42 44 4E 57) - itemnamecache.wdb</li>
   * <li>"XTIW" (58 54 49 57) - itemtextcaxhe.wdb</li>
   * <li>"CPNW" (43 50 4E 57) - npccache.wdb</li>
   * <li>"XTPW" (58 54 50 57) - pagetextcache.wdb</li>
   * <li>"TSQW" (54 53 51 57) - questcache.wdb</li>
   * <li>"NDRW" (4E 44 52 57) - wowcache.wdb</li>
   * </ul>
   * 
   * @param parWDBId
   */
  public void setWDBId(String parWDBId) {
    this.wdbID = parWDBId;
  }

  public String getWDBId() {
    return this.wdbID;
  }

  /**
   * Hex encoded version
   * 
   * @return hex number representing the version, 4 bytes
   */
  public String getWdbBuildVersionHex() {
    return wdbBuildVersionHex;
  }

  /**
   * Set the hex number of this WDB file<br/> <br/> Number must be four bytes:<br/>
   * <br/> B0 12 00 00
   * 
   * 12B0 = 4787 = 1.8.2
   * 
   * @param wdbVersion
   */
  public void setWdbBuildVersionHex(String wdbVersion) {
    this.wdbBuildVersionHex = wdbVersion.toUpperCase();
  }

  /**
   * Hex encoded version
   * 
   * @return decimal number representing the version
   */
  public int getWdbBuildVersionDec() {
    return this.wdbBuildVersionDec;
  }

  /**
   * Set the decimal number of this WDB file<br/> <br/> Number must be four
   * bytes:<br/> <br/> B0 12 00 00
   * 
   * 12B0 = 4787 = 1.8.2
   * 
   * @param wdbVersion
   */
  public void setWdbBuildVersionDec(int wdbVersion) {
    this.wdbBuildVersionDec = wdbVersion;
  }

  /**
   * Version, eg. 1.2.3
   * 
   * @return Version of the application that created this WDB
   */
  public String getWdbVersion() {
    String retValue = (String) buildProperties.get(String
        .valueOf(this.wdbBuildVersionDec));
    if (retValue == null) {
      return "";
    } else {
      return retValue;
    }
  }

  /**
   * Return the compatibility version of this WDB file<br/>
   * <br/>
   * eg:
   * A WDB file of version 1.4.2 is compatible with 1.2
   */
  public String getCompatibilityVersion() {
    return buildProperties.getProperty(this.getWdbBuildVersionHex());
  }

  /**
   * Return the client's locale of the client that created the WDB file<br/>
   * 
   * @return something like "deDE", "enUS", "enGB", etc
   */
  public String getClientLocale() {
    return clientLocale;
  }

  /**
   * Set the client's locale of the client that created the WDB file<br/>
   * 
   * @param clientLocale something like "deDE", "enUS", "enGB", etc
   */
  public void setClientLocale(String clientLocale) {
    this.clientLocale = clientLocale;
  }

}// WDBInfo
