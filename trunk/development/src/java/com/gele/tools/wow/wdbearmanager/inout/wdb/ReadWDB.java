/*
 * Created on 13.03.2005
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: ReadWDB.java 201 2006-04-10 18:50:37Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-04-10 20:50:37 +0200 (Mo, 10 Apr 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 201 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/wdb/ReadWDB.java $
 * $Rev: 201 $
 */
package com.gele.tools.wow.wdbearmanager.inout.wdb;

import java.util.Collection;

import com.gele.base.dbaccess.WDB_DTO;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorInsideXMLConfigFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorReadingWDBFile;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_ErrorReadingXMLConfigFile;
import com.gele.tools.wow.wdbearmanager.helper.WDB_Helper;

public class ReadWDB {

  public ReadWDB() {

  }

  /**
   * @param parXMLConfig
   * @param parFilename
   * @return Collection of DTOs for each entry
   * @throws WDBMgr_ErrorReadingXMLConfigFile
   * @throws WDBMgr_ErrorReadingWDBFile
   * @throws WDBMgr_ErrorInsideXMLConfigFile
   *           the XML config file contained wrong types
   */
  public Collection readWDBFile(String parXMLConfig, String parFilename)
      throws WDBMgr_ErrorInsideXMLConfigFile, WDBMgr_ErrorReadingXMLConfigFile,
      WDBMgr_ErrorReadingWDBFile {
    return this.readWDBFile(null, parXMLConfig, parFilename);
  }

  /**
   * Read a WDB file<br/>
   * 
   * Parses XML config<br/> Reads WDB with the infos from the XML config file<br/>
   * 
   * @param parClass
   *          Read relative to this class
   * 
   * @param parXMLConfig
   *          XML file that contains the configuration of the WDB file
   * @param parFilename
   *          name of the file to be parsed
   * @return Collection containing instances of WDB_DTO objects
   * 
   * @throws WDBMgr_ErrorReadingXMLConfigFile
   *           the XML config file could not be parsed
   * @throws WDBMgr_ErrorReadingWDBFile
   *           the WDB file could not be opened
   * @throws WDBMgr_ErrorInsideXMLConfigFile
   *           the XML config file contained wrong types
   * 
   */
  public Collection readWDBFile(Class parClass, String parXMLConfig,
      String parFilename) throws WDBMgr_ErrorInsideXMLConfigFile,
      WDBMgr_ErrorReadingXMLConfigFile, WDBMgr_ErrorReadingWDBFile {
    
    setWarningMessage("");

    WDB_DTO myWDB_DTO = WDB_Helper.createDTOfromXML(parClass, parXMLConfig);
    // instantiate the DAO for the WDB and go go go

    WDB_DAO myWDB_DAO = new WDB_DAO();
    myWDB_DAO.setFilename(parFilename);
    Collection retValue = myWDB_DAO.getAllObjects(myWDB_DTO);
    this.setWarningMessage( myWDB_DAO.getWarningMessage() );

    return retValue;

  }// readFile
  
  private String warningMessage = "";
/**
 * 
 * @param parMsg
 */
  public void setWarningMessage( String parMsg ) {
    this.warningMessage = parMsg;
  }
/**
 * 
 * @return warning message, can be empty if none is available
 */
  public String getWarningMessage() {
    return this.warningMessage;
  }


}// ReadWDB
