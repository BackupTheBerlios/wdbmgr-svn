/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: ObjectNotDeletedException.java 205 2006-08-16 14:49:30Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/exception/ObjectNotDeletedException.java $
 * $Rev: 205 $
 */

package com.gele.base.dbaccess.exception;

import com.gele.base.exception.NestingException;


/**
 * Wenn ein Objekt aus der Datenbank gel�scht werden soll, k�nnen Fehler
 * auftreten. In solch einem Fall wird eine Exception geworfen.
 * 
 * @author <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a> @copyright
 *         <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a>
 * @see ObjectNotStoredException
 * @see ObjectNotFoundException
 * @version 1.0
 */
public class ObjectNotDeletedException extends NestingException {

  /**
   * 
   */
  private static final long serialVersionUID = 4893718184916089807L;

  /**
   * Constructs an ObjectNotDeletedException with no detail message. A detail
   * message is a String that describes this particular exception.
   */
  public ObjectNotDeletedException() {
    super("ObjectNotDeletedException");
  }

  /**
   * Constructs a ObjectNotDeletedException with the specified detail message.
   * A detail message is a String that describes this particular exception.
   * 
   * @param s
   *            the detail message
   */
  public ObjectNotDeletedException(String s) {
    super(s);
  }

  /**
   * @param nestedException
   */
  public ObjectNotDeletedException(Throwable nestedException) {
    super(nestedException);

  }

  /**
   * @param msg
   * @param nestedException
   */
  public ObjectNotDeletedException(String msg, Throwable nestedException) {
    super(msg, nestedException);

  }

}
