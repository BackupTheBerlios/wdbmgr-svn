package com.gele.base.exception;

/**
 * Capture the contents of remotely thrown Exceptions<br/>
 * In a multi-tiered environment it is important to give the
 * client (presentation layer) the most useful information that
 * can be displayed to the user.<br/>
 * With nested Exceptions it is possible to hide the real implementation
 * specific cause of an error and make it more user friendly.
 * The user cannot cope with SQLException or RemoteException, it needs
 * more detailed information like "ServerNotFoundException" or
 * "ArticleNotFoundInDatabase"<br/>
 *
 * Source-Code taken from http://www.javaworld.com/javatips/jw-javatip91_p.html<br/>
 *
 * @see <a href="http://www.javaworld.com/javatips/jw-javatip91_p.html">
 *       Java Tip 91: Use nested exceptions in a multitiered environment</a>
 *
 */

import java.io.PrintWriter;
import java.io.StringWriter;

public class NestingException extends Exception {
  // the nested exception

  /**
   * 
   */
  private static final long serialVersionUID = -8390433918609876785L;


  private Throwable nestedException;
  // String representation of stack trace - not transient!

  private String stackTraceString;
  // convert a stack trace to a String so it can be serialized

  static public String generateStackTraceString(Throwable t) {

    StringWriter s = new StringWriter();

    t.printStackTrace(new PrintWriter(s));

    return s.toString();

  }
  // java.lang.Exception constructors

  public NestingException() {
  }
  public NestingException(String msg) {

    super(msg);

  }
  // additional c'tors - nest the exceptions, storing the stack trace

  public NestingException(Throwable nestedException) {

    this.nestedException = nestedException;

    this.stackTraceString = generateStackTraceString(nestedException);

  }
  public NestingException(String msg, Throwable nestedException) {

    this(msg);

    this.nestedException = nestedException;

    this.stackTraceString = generateStackTraceString(nestedException);

  }
  // methods

  public Throwable getNestedException() {
    return this.nestedException;
  }
  // descend through linked-list of nesting exceptions, & output trace

  // note that this displays the 'deepest' trace first

  public String getStackTraceString() {

    // if there's no nested exception, there's no stackTrace
    if (this.nestedException == null)
      return "* no stacktrace available *"; //$NON-NLS-1$
    StringBuffer traceBuffer = new StringBuffer();
    if (this.nestedException instanceof NestingException) {

      traceBuffer.append(
        ((NestingException) this.nestedException).getStackTraceString());

      traceBuffer.append("-------- nested by:\n"); //$NON-NLS-1$

    }
    traceBuffer.append(this.stackTraceString);

    return traceBuffer.toString();

  }
  // overrides Exception.getMessage()

  public String getMessage() {

    // superMsg will contain whatever String was passed into the

    // constructor, and null otherwise.

    String superMsg = super.getMessage();
    // if there's no nested exception, do like we would always do

    if (getNestedException() == null)
      return superMsg;
    StringBuffer theMsg = new StringBuffer();
    // get the nested exception's message

    String nestedMsg = getNestedException().getMessage();
    if (superMsg != null)
      theMsg.append(superMsg).append(": ").append(nestedMsg); //$NON-NLS-1$

    else
      theMsg.append(nestedMsg);
    return theMsg.toString();

  }
  // overrides Exception.toString()

  public Throwable getOriginalException() {
    Throwable nesting = null;
    nesting = getNestedException();
    
      
    while (nesting!=null & nesting instanceof NestingException ) {
      nesting = ((NestingException) nesting).getNestedException();
    
    }
    
    return nesting;
  }
  
  public String toString() {

    StringBuffer theMsg = new StringBuffer(super.toString());
    if (getNestedException() != null)
      theMsg.append("; \n\t---> nested ").append(getNestedException()); //$NON-NLS-1$
    return theMsg.toString();

  }
  /* (non-Javadoc)
   * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
   */
  public void printStackTrace(PrintWriter s) {
    s.write("Message:\n"); //$NON-NLS-1$
    s.write("--------------------------------------------\n"); //$NON-NLS-1$
    s.write(this.getMessage());
    s.write("\n"); //$NON-NLS-1$
    s.write("\n"); //$NON-NLS-1$
    s.write("Stacktrace:\n"); //$NON-NLS-1$
    s.write("--------------------------------------------\n"); //$NON-NLS-1$
    super.printStackTrace(s);
    s.write("-------- nested by:\n"); //$NON-NLS-1$
    s.write(this.getStackTraceString());
  }

} // class: NestingException
