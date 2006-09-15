/*
 * CreateDateTime: 16.03.2005, 14:18:43
 * 
 * Contains the number of objects, that were written:
 * 
 * INSERT
 * UPDATE
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: ObjectsWritten.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/inout/ObjectsWritten.java $
 * $Rev: 195 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.inout;

/**
 * When inserting/updating objects into the database, the systems returns
 * values, of how many objects were updated, inserted etc
 * <br/>
 * This information is kept here in this class. 
 * 
 * @author <a href="mailto:lousy.kizura@gmail.com">Kizura Zgabi</a><br/>
 * 
 */
public class ObjectsWritten {
  private int numUpdate = 0;
  private int numInsert = 0;
  private int numErrorInsert = 0;
  private int numErrorUpdate = 0;
  private int numSkipped = 0;

  /**
   * The user only wanted "insert", not update of existing entries. This
   * method returns the number of objects, that were not written, because
   * they already existed (in some version) inside the database<br/>
   * 
   * @return Returns the number of objects not inserted, because they already exist
   */
  public int getNumSkipped() {
    return this.numSkipped;
  }
  /**
   * @param numSkipped The numSkipped to set.
   */
  public void setNumSkipped(int numSkipped) {
    this.numSkipped = numSkipped;
  }
  /**
   * Number of objects that could not be INSERTed<br/>
   * An error occurred while trying to insert the object, maybe
   * database down or some constraints were violated<br/>
   * 
   * @return Returns the number of error that occurred while INSERT.
   */
  public int getNumErrorInsert() {
    return this.numErrorInsert;
  }
  /**
   * @param numErrorInsert The numErrorInsert to set.
   */
  public void setNumErrorInsert(int numErrorInsert) {
    this.numErrorInsert = numErrorInsert;
  }
  /**
   * @return Returns the numErrorUpdate.
   */
  public int getNumErrorUpdate() {
    return this.numErrorUpdate;
  }
  /**
   * @param numErrorUpdate The numErrorUpdate to set.
   */
  public void setNumErrorUpdate(int numErrorUpdate) {
    this.numErrorUpdate = numErrorUpdate;
  }
  /**
   * @return Returns the number of INSERTed objects.
   */
  public int getNumInsert() {
    return this.numInsert;
  }
  /**
   * @param numInsert Set number of INSERTs.
   */
  public void setNumInsert(int numInsert) {
    this.numInsert = numInsert;
  }
  /**
   * @return Returns the number of Updates.
   */
  public int getNumUpdate() {
    return this.numUpdate;
  }
  /**
   * @param numUpdate Set number of updates
   */
  public void setNumUpdate(int numUpdate) {
    this.numUpdate = numUpdate;
  }
}// objectsWritten.java
