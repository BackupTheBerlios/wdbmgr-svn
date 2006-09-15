package com.gele.utils.exception.readprops;

import com.gele.utils.exception.NestingException;


/**
 * An error occurred while reading the properties file with
 * the parameters<br/>
 *
 */

public class ErrorReadingPropertiesFileException extends NestingException{

  public ErrorReadingPropertiesFileException() {
    super();
  }

  public ErrorReadingPropertiesFileException(String msg) {
    super(msg);
  }

  public ErrorReadingPropertiesFileException(String msg, Throwable parThrow) {
    super(msg, parThrow);
  }
} // class: ErrorReadingPropertiesFileException
