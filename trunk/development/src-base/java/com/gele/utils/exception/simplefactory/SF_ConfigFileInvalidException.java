package com.gele.utils.exception.simplefactory;



/**
 * The config file for the SimpleFactory contains invalid settings.
 * <br/>
 *
 */

public class SF_ConfigFileInvalidException extends SF_Exception{

  public SF_ConfigFileInvalidException() {
    super();
  }

  public SF_ConfigFileInvalidException(String msg) {
    super(msg);
  }

  public SF_ConfigFileInvalidException(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: ConfigFileInvalidException
