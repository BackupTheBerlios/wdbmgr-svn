/*
 * CreateDateTime: 16.03.2005, 14:18:43
 *
 * @author <a href="mailto:lousy.kizura@gmail.com">Kizura Zgabi</a><br/>
 * 
 * Contains the number of objects, that were written:
 * 
 * INSERT
 * UPDATE
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: SCPWritten.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/SCPWritten.java $
 * $Rev: 195 $
 */
package com.gele.tools.wow.wdbearmanager.inout;

/**
 * This class is returned after an SCP file was patched<br/>
 * <br/>
 * While patching an SCP file, some cases are interesting:<br/>
 * <ul>
 * <li> Number of entries in database</li>
 * <li> Number of entries patched in SCP file</li>
 * </ul>
 * 
 * @author @author <a href="mailto:lousy.kizura@gmail.com">Kizura Zgabi</a><br/>
 * 
 */
public class SCPWritten {

  /**
   * Number of entries inside the SCP file<br/>
   * 
   * @return Returns the numInSCP.
   */
  public int getNumInSCP() {
    return this.numInSCP;
  }
  /**
   * Set the number of entries inside the SCP file<br/>
   * 
   * @param numInSCP The numInSCP to set.
   */
  public void setNumInSCP(int numInSCP) {
    this.numInSCP = numInSCP;
  }
  private int numInDB = 0;
  private int numPatched = 0;
  private String patchedIDs = "";
  private int numInSCP = 0;

  /**
   * @return Returns the patchedIDs.
   */
  public String getPatchedIDs() {
    return this.patchedIDs;
  }
  /**
   * @param patchedIDs The patchedIDs to set.
   */
  public void setPatchedIDs(String patchedIDs) {
    this.patchedIDs = patchedIDs;
  }
  /**
   * Number of entries found in database<br/>
   * 
   * @return Returns the numInDB.
   */
  public int getNumInDB() {
    return this.numInDB;
  }
  /**
   * @param numInDB The numInDB to set.
   */
  public void setNumInDB(int numInDB) {
    this.numInDB = numInDB;
  }
  /**
   * Number of entries found in database that were also found inside
   * SCP file -> patched them<br/>
   * 
   * @return Returns the numPatched.
   */
  public int getNumPatched() {
    return this.numPatched;
  }
  /**
   * @param numPatched The numPatched to set.
   */
  public void setNumPatched(int numPatched) {
    this.numPatched = numPatched;
  }
}// SCPWritten
