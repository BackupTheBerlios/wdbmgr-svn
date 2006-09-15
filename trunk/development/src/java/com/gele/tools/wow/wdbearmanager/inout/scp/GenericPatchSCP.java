/*
 * Created on 16.03.2005
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: GenericPatchSCP.java 218 2006-09-14 23:45:59Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/scp/GenericPatchSCP.java $
 * $Rev: 218 $
 */
package com.gele.tools.wow.wdbearmanager.inout.scp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import com.gele.base.dbaccess.DTO_Interface;
import com.gele.base.dbaccess.Generic_DTO;
import com.gele.tools.wow.wdbearmanager.WDBearManager;
import com.gele.tools.wow.wdbearmanager.api.WDBearManager_I;
import com.gele.tools.wow.wdbearmanager.castor.patch.Destination;
import com.gele.tools.wow.wdbearmanager.castor.patch.Line;
import com.gele.tools.wow.wdbearmanager.castor.patch.PatchConfig;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_Exception;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_FileNotFound;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_IOException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_NoDataAvailableException;
import com.gele.tools.wow.wdbearmanager.exception.WDBMgr_UnknownWDBFile;
import com.gele.tools.wow.wdbearmanager.helper.WDB_Helper;
import com.gele.tools.wow.wdbearmanager.inout.SCPWritten;

public class GenericPatchSCP implements GenericPatch_I {
  // Logging with Log4J
  Logger myLogger = Logger.getLogger(GenericPatchSCP.class);

  /**
   * Merge database with existing creatures.scp file
   * 
   * @param parConfig config extracted from "patchSCP.xml"
   * 
   * @param parFileSCP name of the scp file
   * 
   * @param parUseUTF8 write UTF-8 letters
   * 
   * @param parLocale deDE, enUS, etc (if ALL is provided - locale is ignored)
   * 
   * @return number of entries inside database
   */
  public SCPWritten merge(WDBearManager_I parAPI, String parFileSCP,
      PatchConfig parConfig, boolean parUseUTF8, boolean parVerbose,
      String parLocale) throws WDBMgr_UnknownWDBFile,
      WDBMgr_NoDataAvailableException, WDBMgr_IOException, WDBMgr_Exception {
    SCPWritten retValue = new SCPWritten();

    String xmlFile = parConfig.getXmlConfig().getName();

    String marker = "[" + parConfig.getMarker().getName();

    Generic_DTO myWDB_DTO = WDB_Helper.createDTOfromXML(null, xmlFile);
    if (parLocale.equals(WDB_Helper.INF_LOCALE_ALL) == false) {
      myWDB_DTO.setColumnValue(WDB_Helper.INF_LOCALE, parLocale);
    }

    // all entries from database
    Collection allItems = null;
    try {
      allItems = parAPI.getAllObjects(myWDB_DTO);
    } catch (WDBMgr_NoDataAvailableException ex) {
      throw new WDBMgr_NoDataAvailableException(
          "No data found inside database", ex);
    } catch (Exception ex) {
      throw new WDBMgr_Exception(ex.getMessage(), ex);
    }
    // .out.println("Anzahl in DB: " + allItems.size());
    retValue.setNumInDB(allItems.size());
    // firstCol = ID
    String firstCol = "";
    Iterator itDummy = myWDB_DTO.getColumns();
    if (itDummy.hasNext()) {
      firstCol = (String) itDummy.next();
    }
    Iterator itItems = allItems.iterator();
    HashMap hmId2Item = new HashMap();
    DTO_Interface loopDTO = null;
    while (itItems.hasNext()) {
      loopDTO = (DTO_Interface) itItems.next();
      hmId2Item.put(loopDTO.getColumnValue(firstCol), loopDTO);
    }

    // compare against quests.scp
    File filQuests = new File(parFileSCP);
    File filPatched = new File(parFileSCP + "_patch");
    File filPatchedInfo = new File(parFileSCP + "_patch_info");

    BufferedReader myFR = null;
    BufferedWriter myBW = null;
    BufferedWriter myBWInfo = null;
    try {
      myFR = new BufferedReader(new FileReader(filQuests));
    } catch (FileNotFoundException ex) {
      throw new WDBMgr_FileNotFound("Could not open file '" + parFileSCP + "'",
          ex);
    }
    try {
      myBW = new BufferedWriter(new FileWriter(filPatched));
    } catch (IOException ex) {
      throw new WDBMgr_IOException("Could not create file '"
          + filPatched.getAbsolutePath() + "'", ex);
    }
    try {
      myBWInfo = new BufferedWriter(new FileWriter(filPatchedInfo));
    } catch (IOException ex) {
      throw new WDBMgr_IOException("Could not create file '"
          + filPatched.getAbsolutePath() + "'", ex);
    }

    int numPatched = 0;
    String line = "";
    String tmpLine = "";
    Integer questID = null;
    DTO_Interface gotchaDTO = null;
    int numMarkers = 0;
    PythonInterpreter interp = new PythonInterpreter();
    interp.set("wdbmgrapi", parAPI);

    try {

      StringBuffer patchedIDs = new StringBuffer();
      int replaceCount = parConfig.getReplaceCount();
      boolean replaceFound = false;
      while ((line = myFR.readLine()) != null) {
        tmpLine = line.trim();
        if (tmpLine.length() == 0) {
          // end of description
        }
        // .out.println(">> Zeile : "+ tmpLine);
        if (tmpLine.startsWith(marker)) {
          numMarkers++;
          // add CR/LF before each "quest"
          //myBW.write("\r\n");
          questID = Integer.valueOf(tmpLine.substring(marker.length() + 1,
              tmpLine.length() - 1));
          //System .out.println("Quest ID: "+ questID);
          //System .out.println("Zeile   : "+ tmpLine);
          // Look, if we got this quest inside the database
          if ((gotchaDTO = (DTO_Interface) hmId2Item.get(questID)) != null) {
            if (parVerbose) {
              this.myLogger.info("Patching object: " + questID);
            }
            numPatched++;
            patchedIDs.append(questID + " ");
            if (((numPatched + 1) % 20) == 0) {
              patchedIDs.append(" \r\n");
            }

            myBW.write(line);
            myBW.write("\r\n");
            // read till end
            myFR.mark(2048);
            while ((line = myFR.readLine()) != null) {
              if (line.length() == 0) {
                break;
              }
              if (line.startsWith(marker)) {
                myFR.reset();
                break;
              }
              myFR.mark(2048);

              //System .out.println(line);
              replaceFound = false;
              for (int i = 0; i < replaceCount; i++) {
                // Something we should replace with the values
                // from the database?
                if (line.startsWith(parConfig.getReplace(i).getSource() + "=")) {
                  replaceFound = true;
                  String strText = readTextWithCRLF(myFR, parConfig.getReplace(
                      i).getSource());

                  byte[] bytText = null;
                  if (parConfig.getReplace(i).getMultiline().equals("1")) {
                    // split after newline
                    String[] newLineSplit = ((String) gotchaDTO
                        .getColumnValue(parConfig.getReplace(i).getLine(0)
                            .getDestination(0).getName())).split("\n");
                    for (int j = 0; j < newLineSplit.length; j++) {
                      myBW.write(parConfig.getReplace(i).getSource() + "=");
                      // maybe CR+LF
                      if (newLineSplit[i].charAt(newLineSplit[i].length() - 1) == '\r') {
                        newLineSplit[i] = newLineSplit[i].substring(0,
                            newLineSplit[i].length());
                      }
                      if (parUseUTF8 == true) {
                        bytText = newLineSplit[j].getBytes("UTF8");
                        //                      myBW.write(new String(bytText, "UTF8"));
                        myBW.write(new String(bytText));
                      } else {
                        myBW.write(newLineSplit[j]);
                        //                        bytText = newLineSplit[j].getBytes();
                        //                        myBW.write(new String(bytText));
                      }
                      myBW.write("\r\n");
                    }
                  } else { // no multiple lines

                    String destName = "";
                    String destWDB = "";
                    String jythonScript = "";

                    HashMap hmpFlag = new HashMap();
                    // Process the "Line" values
                    // Make sure we dont parse a key more than one time
                    if (hmpFlag.get(parConfig.getReplace(i).getSource()) == null) {
                      int attrWritten = 0;
                      for (int j = 0; j < parConfig.getReplace(i)
                          .getLineCount(); j++) {

                        Line loopLine = parConfig.getReplace(i).getLine(j);
                        // Constraint: First value of multiple ones MUST NOT
                        // be Null (Zero)
                        Destination loopDest = loopLine.getDestination(0);
                        if ((loopLine.getDestinationCount() >= 1)
                            && (loopDest.getNotNull() != null)) {
                          destName = loopDest.getName();
                          destWDB = gotchaDTO.getColumnValue(destName)
                              .toString();
                          if (Integer.parseInt(destWDB) == 0) {
                            continue;
                          }
                        }
                        //                        // We got "Source" (what to replace inside the SCP)
                        //                        if ((attrWritten >= 1)
                        //                            && (j < (parConfig.getReplace(i).getLineCount() ))) {
                        //                          myBW.write("\r\n");
                        //                        }
                        attrWritten++;
                        StringBuffer writeTXT = new StringBuffer();
                        myBW.write(parConfig.getReplace(i).getSource() + "=");
                        for (int k = 0; k < loopLine.getDestinationCount(); k++) {
                          loopDest = loopLine.getDestination(k);
                          destName = loopDest.getName();
                          destWDB = gotchaDTO.getColumnValue(destName)
                              .toString();
                          jythonScript = loopDest.getContent();
                          if (jythonScript.length() != 0) {
                            interp.set(destName, destWDB);
                            ByteArrayInputStream myBAIS = new ByteArrayInputStream(
                                jythonScript.getBytes());
                            interp.execfile(myBAIS);
                            // .out.println(">>" + jythonScript + "<<");
                            PyObject myPO = interp.get("retValue");
                            // .out.println(">>" + myPO + "<<");

                            destWDB = myPO.toString();
                          }
                          if (writeTXT.length() != 0) {
                            writeTXT.append(" " + destWDB.toString());
                          } else {
                            writeTXT.append(destWDB.toString());
                          }
                        }// for... Number of "Destination" definitions
                        if (parUseUTF8 == false) {
                          myBW.write(writeTXT.toString());
                        } else {
                          bytText = writeTXT.toString().getBytes("UTF8");
                          //                      myBW.write(new String(bytText, "UTF8"));
                          myBW.write(new String(bytText));
                        }
                        myBW.write("\r\n");
                      }// for... Number of Line definitions ( j )
                      hmpFlag.put(parConfig.getReplace(i).getSource(),
                          "something");
                    }

                  }// no multiple lines
                  break;
                }// if... found "replace"
              }// loop... all replace ... ( i )
              if (replaceFound == false) {
                // we dont have data to replace this
                //System .out.println("Line: "+ line);
                myBW.write(line);
                myBW.write("\r\n");
              }
            }// while... readLine
          }// we got DTO for this quest
          else {
            myBW.write(line);
            //myBW.write("\r\n");
          }
        }// startsWith... [MARKER...
        else {
          // write patched file
          myBW.write(line);
        }
        myBW.write("\r\n");
      }// while... readLine
      retValue.setNumInSCP(numMarkers);
      retValue.setPatchedIDs(patchedIDs.toString());
      retValue.setNumPatched(numPatched);

      // write info
      myBWInfo.write(new Date().toString());
      myBWInfo.write("\r\n");
      myBWInfo.write(this.patch_info(retValue, parFileSCP));
      myBWInfo.write("\r\n");
      myBWInfo.write(WDBearManager.VERSION_INFO);

      myFR.close();
      myBW.close();
      myBWInfo.close();
    } catch (IOException ex) {
      throw new WDBMgr_IOException("Error while accessing files", ex);
    } catch (IllegalArgumentException ex) {
      throw new WDBMgr_Exception("Error in patchSCP.xml -> " + ex.getMessage(),
          ex);
    }

    return retValue;
  } //SCPWritten

  /**
   * Contains all IDs
   */
  public String patch_info(SCPWritten mySCPW, String parFilename) {
    double dPercentage = ((double) (mySCPW.getNumPatched() * 100) / (double) (mySCPW
        .getNumInSCP()));
    NumberFormat formatter = NumberFormat.getInstance(Locale.US);
    formatter.setMaximumFractionDigits(2);
    String strPercentage = formatter.format(dPercentage);
    return "Patch statistics\n" + "Entries in database:    "
        + mySCPW.getNumInDB() + "\n" + "Entries in SCP:  "
        + mySCPW.getNumInSCP() + "\n" + "Patched in .scp: "
        + mySCPW.getNumPatched() + " - " + strPercentage + "% \n"
        + "Patched IDs:      " + mySCPW.getPatchedIDs() + "\n"
        + "Patched file: " + parFilename + "_patch\n" + "Patched file info: "
        + parFilename + "_patch_info\n";
  }

  /**
   * Status info without the numbers patched (IDs)
   */
  public String patch_info_simple(SCPWritten mySCPW, String parFilename) {
    double dPercentage = ((double) (mySCPW.getNumPatched() * 100) / (double) (mySCPW
        .getNumInSCP()));
    NumberFormat formatter = NumberFormat.getInstance(Locale.US);
    formatter.setMaximumFractionDigits(2);
    String strPercentage = formatter.format(dPercentage);
    return "Patch statistics\n" + "Entries in database:    "
        + mySCPW.getNumInDB() + "\n" + "Entries in SCP:    "
        + mySCPW.getNumInSCP() + "\n" + "Patched in .scp: "
        + mySCPW.getNumPatched() + " - " + strPercentage + "% \n"
        + "Patched file: " + parFilename + "_patch\n" + "Patched file info: "
        + parFilename + "_patch_info\n";
  }

  private static String readTextWithCRLF(BufferedReader parBR,
      String parSkipThis) {
    String retValue = "";
    // read till end of file
    // or
    // till next [... found
    // or
    // till next blahblah= found

    String strLine = "";
    boolean foundKey = false;
    try {
      parBR.mark(2048);
      while ((strLine = parBR.readLine()) != null) {
        // found KEY=...
        if (strLine.indexOf("=") != -1) {
          // line DOES NOT start with same key
          if (strLine.startsWith(parSkipThis + "=") == false) {
            parBR.reset();
            return retValue;
          }
        }
        if (strLine.startsWith("[") == true) {
          parBR.reset();
          return retValue;
        }
        retValue += strLine;
        parBR.mark(2048);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
      System.exit(0);
    }

    return retValue;
  }// skipTextWithCRLF

}// MergeQuestsSCP
