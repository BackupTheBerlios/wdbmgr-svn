/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: ObjectNotAvailableException.java 193 2005-10-30 16:22:47Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2005-10-30 17:22:47 +0100 (So, 30 Okt 2005) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 193 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/exception/ObjectNotAvailableException.java $
 * $Rev: 193 $
 */

package com.gele.base.dbaccess.exception;

import com.gele.base.dbaccess.DTO_Interface;

public class ObjectNotAvailableException extends ObjectNotStoredException {

  private static final long serialVersionUID = 4082456569309404072L;

  private DTO_Interface dtoObject = null;

  public ObjectNotAvailableException() {
  }
  public ObjectNotAvailableException(String parMsg) {
    super(parMsg);
  }
  public ObjectNotAvailableException(
    String parMsg,
    DTO_Interface parDTOObject) {
    super(parMsg);
    this.dtoObject = parDTOObject;
  }
  public DTO_Interface getDtoObject() {
    return this.dtoObject;
  }
  public void setDtoObject(DTO_Interface dtoObject) {
    this.dtoObject = dtoObject;
  }
}