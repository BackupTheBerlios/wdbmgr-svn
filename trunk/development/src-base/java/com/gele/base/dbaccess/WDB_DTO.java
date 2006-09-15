package com.gele.base.dbaccess;


/**
 * Special DTO to reflect the needs for a WDB DTO.<br/>
 * This class only contains the additional methods, that are
 * needed for a WDB_DTO.<br/>
 * The base class can be used to access relational databases,
 * CSV, etc<br/>
 * <br/>
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
 *
 */
public class WDB_DTO extends Generic_DTO {
  // Number of bytes to be skipped (in the beginning of a WDB file)
  //
  // PRE 1.6.0               ->              16
  //     1.6.x               ->              20
  private int skipBytes = 16;
  
  // Bytes, that mark the EOF
  // For WDB:
  //  8 times 0x00 (8 Bytes with ZERO)
  private byte[] eofMarker = new byte[]{0, 0, 0, 0, 0, 0, 0, 0 };
  
  /**
   * 
   */
  private static final long serialVersionUID = -8332164603435666538L;
  
  public void setSkipBytes( int parSkipBytes ) {
    this.skipBytes = parSkipBytes;
  }
  
  public int getSkipBytes() {
    return this.skipBytes;
  }

  public byte[] getEofMarker() {
    return eofMarker;
  }

  public void setEofMarker(byte[] eofMarker) {
    this.eofMarker = eofMarker;
  }
}// WDB_DTO
