package com.gele.utils.exception.simplefactory;



/**
 * The config file could not be found. This exception indicates this
 * error. It just encapsulates the exception thrown by the
 * ReadPropertiesFile class.<br/>
 * <br/>
 *
 */

public class SF_ErrorReadingPropertiesException extends SF_Exception{

  public SF_ErrorReadingPropertiesException() {
    super();
  }

  public SF_ErrorReadingPropertiesException(String msg) {
    super(msg);
  }

  public SF_ErrorReadingPropertiesException(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: ConfigFileInvalidException
