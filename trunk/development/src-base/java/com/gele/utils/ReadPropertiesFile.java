/*
 * CreateDateTime: 09.06.2004, 15:32:34
 *
 * Read a .properties file and return an instance of java.util.Properties<br/>
 * 
 * If the file could not be read, an Exception is thrown<br/>
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock </a>
 *
 * @version   $Id: ReadPropertiesFile.java 219 2006-09-15 10:40:44Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-15 12:40:44 +0200 (Fr, 15 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 219 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src-base/java/com/gele/utils/ReadPropertiesFile.java $
 * $Rev: 219 $
 */

package com.gele.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.gele.utils.exception.readprops.ErrorReadingPropertiesFileException;

/**
 * @author <a href="mailto:lousy.kizura@gmail.com">Gerhard Leibrock</a><br>
 * 
 * Read a .properties file from the file-system <em>or</em>
 * from inside a JAR archive.<br/>
 * <em>If</em> the .properties file could not be found inside
 * the file system (same directory), then the class
 * tries to read it from the JAR archive, where it is
 * stored inside<br/>
 * 
 */
public class ReadPropertiesFile {

  public static Properties readProperties(String parPropFilename)
      throws ErrorReadingPropertiesFileException {

    InputStream propIStream = null;
    try {
      propIStream = new FileInputStream(parPropFilename);
    } catch (FileNotFoundException ex) {
      // does not work as a filename, maybe inside a JAR -> try it as a resource
      try {
        propIStream = ReadPropertiesFile.class.getResourceAsStream("/"
            + parPropFilename);
      } catch (NullPointerException exx) {
        throw new ErrorReadingPropertiesFileException(parPropFilename
            + ": file not found." + exx.getLocalizedMessage(), exx);
      } catch (Exception exx) {
        exx.printStackTrace();
        throw new ErrorReadingPropertiesFileException(parPropFilename
            + ": file not found." + exx.getLocalizedMessage(), exx);
      }
    }

    if( propIStream == null ) {
      throw new ErrorReadingPropertiesFileException(parPropFilename
          + ": file not found.");
    }
    Properties myProps = new Properties();
    try {
      myProps.load(propIStream);
      propIStream.close();
    } catch (IOException ex) {
      throw new ErrorReadingPropertiesFileException(parPropFilename
          + ": file not found." + ex.getLocalizedMessage(), ex);
    }

    return myProps;
  }// readProperties

  /**
   * Read a properties file, that is placed inside the same directory as
   * the class, that is provided as a parameter<br/>
   * 
   * @param parClass read file relative to this class
   * (either inside JAR or file system)
   * @param parPropFilename filename of the properties file
   * @return instance of java.util.Properties
   */
  public static Properties readProperties(Class parClass, String parPropFilename)
      throws ErrorReadingPropertiesFileException {

    InputStream propIStream = null;
    try {
      String pkg = parClass.getPackage().getName();
      pkg = pkg.replace('.', File.separatorChar);
      File myFile = new File(pkg, parPropFilename);
      propIStream = new FileInputStream(myFile);
    } catch (FileNotFoundException ex) {
      // does not work as a filename, maybe inside a JAR -> try it as a resource
      try {
        propIStream = parClass.getResourceAsStream(parPropFilename);
      } catch (NullPointerException exx) {
        throw new ErrorReadingPropertiesFileException(parPropFilename
            + ": file not found." + exx.getLocalizedMessage(), exx);
      } catch (Exception exx) {
        exx.printStackTrace();
        throw new ErrorReadingPropertiesFileException(parPropFilename
            + ": file not found." + exx.getLocalizedMessage(), exx);
      }
    }
    Properties myProps = new Properties();
    try {
      myProps.load(propIStream);
      propIStream.close();
    } catch (IOException ex) {
      throw new ErrorReadingPropertiesFileException(parPropFilename
          + ": file not found." + ex.getLocalizedMessage(), ex);
    }

    return myProps;
  }// readProperties

}// ReadPropertiesFile.java
