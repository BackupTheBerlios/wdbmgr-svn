/*
 * Created on 01. Nov 2004
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: SimpleFactory.java 205 2006-08-16 14:49:30Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/utils/SimpleFactory.java $
 * $Rev: 205 $
 */
package com.gele.utils;

import java.util.Properties;

import com.gele.utils.exception.readprops.ErrorReadingPropertiesFileException;
import com.gele.utils.exception.simplefactory.SF_ClassNotFoundException;
import com.gele.utils.exception.simplefactory.SF_ConfigFileInvalidException;
import com.gele.utils.exception.simplefactory.SF_CouldNotInstantiateClassException;
import com.gele.utils.exception.simplefactory.SF_ErrorReadingPropertiesException;
import com.gele.utils.exception.simplefactory.SF_Exception;

/**
 * @author   <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * 
 */
public class SimpleFactory {
  
  private static String PROP_CLASSNAME = "classname";

  /**
   * Read the given config file, create an instance of the class and return the
   * instance <br/> <br/>
   * 
   * @throws SF_Exception indicates that an error occurred, see below for the detailed
   * errors
   * 
   * <ul>
   *  <li> SF_ClassNotFoundException<br/>
   *   the class specified inside the config file could not be found
   *  <li> SF_ConfigFileInvalidException <br/>
   *   the config file did not contain the correct 
   *   parameter setting </li>
   *  <li> SF_CouldNotInstantiateClassException<br/>
   *   an error occurred while
   *   instantiating the specified class  </li>
   *  <li> SF_ErrorReadingPropertiesException<br/>
   *   the specified config file could not be read </li>
   * </ul>
   * 
   * @param parFilename name of the config file, must be on top level
   * if you want a relative path, use the other getInstance method
   * 
   */
  public static Object getInstance(String parFilename) throws SF_Exception {
    return SimpleFactory.getInstance( null, parFilename );
  }
    
/**
 * Simple Factory, that read a config file and returns an instance
 * of the class that is specified there.<br/>
 * 
 * Why we need this -> RTFM about factories...
 * 
 * @param parClass  Read properties file that is stored with this clas
 * @param parFilename name of the properties file
 * @return instance of the specified class
 * @throws SF_Exception Error occurred
 */
  public static Object getInstance(Class parClass, String parFilename) throws SF_Exception {

    Object retValue = null;
    Properties myProps = null;
    try {
      if( parClass != null ) {
      	myProps = ReadPropertiesFile.readProperties(parClass, parFilename);
      }else {
      	myProps = ReadPropertiesFile.readProperties(parFilename);
      }
    } catch (ErrorReadingPropertiesFileException ex) {
      throw new SF_ErrorReadingPropertiesException(
          "The properties file could not be read", ex);
    }

    String classname = myProps.getProperty(PROP_CLASSNAME);
    if (classname == null) {
      // no classname -> config file invalid
      throw new SF_ConfigFileInvalidException(
          "Missing parameter '"+PROP_CLASSNAME+"' inside config file '" + parFilename
              + "'");
    }
    Class newClass = null;
    try {
      newClass = Class.forName(classname);
    } catch (ClassNotFoundException ex) {
      throw new SF_ClassNotFoundException(
          "The class specified inside the config file could not be found", ex);
    }
    try {
      retValue = newClass.newInstance();
    } catch (InstantiationException ex) {
      throw new SF_CouldNotInstantiateClassException(
          "Error instantiating class", ex);
    } catch (IllegalAccessException ex) {
      throw new SF_CouldNotInstantiateClassException(
          "Error instantiating class", ex);
    }

    return retValue;
  }// getInstance
  
}// SimpleFactory
