/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: DatabaseTroubleException.java 193 2005-10-30 16:22:47Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/exception/DatabaseTroubleException.java $
 * $Rev: 193 $
 */

package com.gele.base.dbaccess.exception;

import com.gele.base.exception.NestingException;

/**
 * Bei der Arbeit mit Objekten in einer Datenbank k�nnen Fehler auftreten.
 * Diese werden durch diese Exception signalisiert.
 * 
 * @author <a href="mailto:lousy.kizura@gmail.com">Kizura aka Lousyplayer aka Gerhard Leibrock </a>
 *  @copyright
 *         <a href="mailto:lousy.kizura@gmail.com">Kizura aka Lousyplayer aka Gerhard Leibrock </a>
 * @see ObjectNotDeletedException
 * @see ObjectNotStoredException
 * @see ObjectNotFoundException
 * @version 1.0
 */
public class DatabaseTroubleException extends NestingException {
  /**
   * 
   */
  private static final long serialVersionUID = -459785440594359700L;

  /**
   * Constructs a DatabaseTroubleException with no detail message. A detail
   * message is a String that describes this particular exception.
   */
  public DatabaseTroubleException() {
    super("DatabaseTroubleException");
  }
  /**
   * Constructs a DatabaseTroubleException with the specified detail message. A
   * detail message is a String that describes this particular exception.
   * 
   * @param s
   *            the detail message
   */
  public DatabaseTroubleException(String s) {
    super(s);
  }

  /**
   * @param nestedException
   */
  public DatabaseTroubleException(Throwable nestedException) {
    super(nestedException);

  }

  /**
   * @param msg
   * @param nestedException
   */
  public DatabaseTroubleException(String msg, Throwable nestedException) {
    super(msg, nestedException);

  }
} // DatabaseTroubleException
//########## DatabaseTroubleException ##########
