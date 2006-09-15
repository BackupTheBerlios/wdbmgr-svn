/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: ObjectNotFoundException.java 205 2006-08-16 14:49:30Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-08-16 16:49:30 +0200 (Mi, 16 Aug 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 205 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/exception/ObjectNotFoundException.java $
 * $Rev: 205 $
 */

package com.gele.base.dbaccess.exception;

import com.gele.base.exception.NestingException;

/**
 * Ein Objekt kann nicht aus der Datenbank gelesen werden. In solch einem Fall
 * wird eine Exception geworfen.
 * 
 * @author <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a> @copyright
 *         <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 * @see ObjectNotDeletedException
 * @see ObjectNotStoredException
 * @version 1.0
 */
public class ObjectNotFoundException extends NestingException {
  /**
   * 
   */
  private static final long serialVersionUID = 1777111322281308627L;

  /**
   * Constructs an ObjectNotFoundException with no detail message. A detail
   * message is a String that describes this particular exception.
   */
  public ObjectNotFoundException() {
    super("ObjectNotFound Exception");
  }

  /**
   * Constructs an ObjectNotFoundException with the specified detail message. A
   * detail message is a String that describes this particular exception.
   * 
   * @param s
   *            the detail message
   */
  public ObjectNotFoundException(String s) {
    super(s);
  }

  /**
   * @param nestedException
   */
  public ObjectNotFoundException(Throwable nestedException) {
    super(nestedException);

  }

  /**
   * @param msg
   * @param nestedException
   */
  public ObjectNotFoundException(String msg, Throwable nestedException) {
    super(msg, nestedException);

  }
}
