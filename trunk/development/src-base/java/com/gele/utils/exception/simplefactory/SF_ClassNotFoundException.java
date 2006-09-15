package com.gele.utils.exception.simplefactory;



/**
 * The class specified inside the config file for the SimpleFactory
 * could not be found.
 * <br/>
 *
 */

public class SF_ClassNotFoundException extends SF_Exception{

  public SF_ClassNotFoundException() {
    super();
  }

  public SF_ClassNotFoundException(String msg) {
    super(msg);
  }

  public SF_ClassNotFoundException(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: ConfigFileInvalidException
