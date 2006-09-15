package com.gele.utils.exception.simplefactory;

import com.gele.utils.exception.NestingException;


/**
 * Base exception for all exceptions thrown by the simple factory.
 * All exceptions are derived from this one.
 * <br/>
 *
 */

public class SF_Exception extends NestingException{

  public SF_Exception() {
    super();
  }

  public SF_Exception(String msg) {
    super(msg);
  }

  public SF_Exception(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: ConfigFileInvalidException
