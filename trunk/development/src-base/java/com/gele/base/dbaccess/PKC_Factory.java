/*
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: PKC_Factory.java 193 2005-10-30 16:22:47Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/base/dbaccess/PKC_Factory.java $
 * $Rev: 193 $
 */

package com.gele.base.dbaccess;

/**
 * Title:        PKC_Factory
 * Description:
 *
 * Please refer to "dao_config.xsd" for a detailed description of the
 * xml config file.
 *
 */

// 3rd party classes: CASTOR to parse the XML config file
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * This class is instantiated on the client machine<br/>
 *
 */
public class PKC_Factory {

  /**
   * Constructor is private, so no one can instantiate it<br/>
   *
   */
  private PKC_Factory() {
  }

  /**
   * Create a new instance of the class specified inside the XML file<br/>
   *
   * @param parDBconfig Config Class
   * 
   * @return instance of the class; see dao_config.xml
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws InvalidParameterException if the specified dbType could not be
   * found inside the config file
   * @throws Exception
   */
  public static PrimaryKeyCreator createInstance(DBConfig_I parDBconfig)
    throws
      ClassNotFoundException,
      IllegalAccessException,
      InstantiationException,
      IOException,
      InvalidParameterException,
      Exception {

    PrimaryKeyCreator returnValue = null;

    // Instantiate Java class
    Class genericClass = Class.forName(parDBconfig.getPKCClass());
    returnValue = (PrimaryKeyCreator) genericClass.newInstance();
    
    if (returnValue == null)
      throw new InvalidParameterException(
        "Failure on creating class: " + parDBconfig.getPKCClass());
    
    return returnValue;
  } // createInstance(STRING)
  //########## createInstance(STRING) ##########


} // PKC_Factory.java
//########## PKC_Factory.java ##########
