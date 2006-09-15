package com.gele.utils.exception.simplefactory;



/**
 * The class specified inside the config file for the SimpleFactory
 * could not be found.
 * <br/>
 *
 */

public class SF_CouldNotInstantiateClassException extends SF_Exception{

  public SF_CouldNotInstantiateClassException() {
    super();
  }

  public SF_CouldNotInstantiateClassException(String msg) {
    super(msg);
  }

  public SF_CouldNotInstantiateClassException(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: ConfigFileInvalidException
