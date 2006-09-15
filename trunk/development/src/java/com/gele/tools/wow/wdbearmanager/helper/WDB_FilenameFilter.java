/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */

package com.gele.tools.wow.wdbearmanager.helper;

/*
 * @(#)ExampleFileFilter.java	1.14 03/01/23
 */

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

/**
 * A convenience implementation of FileFilter that filters out
 * all files except for those with the same name.
 *
 * Case is ignored.
 *
 * Example - create a new filter that filerts out all files
 * "nase.gif" and "nase.jpg"
 *
 *     JFileChooser chooser = new JFileChooser();
 *     ExampleFileFilter filter = new ExampleFileFilter(
 *                   new String{"nase.gif", "nase.jpg"}, "2 Files")
 *     chooser.addChoosableFileFilter(filter);
 *     chooser.showOpenDialog(this);
 * 
 * 
 * NOTE:
 * Example taken from www.javasoft.com/tutorial
 * 
 * Modified by lousy.kizura@gmail.com
 * 
 *
 * @version 1.14 01/23/03
 * @author Jeff Dinkins
 */
public class WDB_FilenameFilter extends FileFilter {

  //private static String TYPE_UNKNOWN = "Type Unknown";

  //private static String HIDDEN_FILE = "Hidden File";

  private Hashtable filters = null;

  private String description = null;

  private String fullDescription = null;

  private boolean useExtensionsInDescription = true;

  /**
   * Creates a file filter. If no filters are added, then all
   * files are accepted.
   *
   * @see #addExtension
   */
  public WDB_FilenameFilter() {
    this.filters = new Hashtable();
  }

  /**
   * Creates a file filter that accepts files with the given extension.
   * Example: new ExampleFileFilter("jpg");
   *
   * @see #addExtension
   */
  public WDB_FilenameFilter(String extension) {
    this(extension, null);
  }

  /**
   * Creates a file filter that accepts the given file type.
   * Example: new ExampleFileFilter("jpg", "JPEG Image Images");
   *
   * Note that the "." before the extension is not needed. If
   * provided, it will be ignored.
   *
   * @see #addExtension
   */
  public WDB_FilenameFilter(String extension, String description) {
    this();
    if (extension != null)
      addExtension(extension);
    if (description != null)
      setDescription(description);
  }

  /**
   * Creates a file filter from the given string array.
   * Example: new ExampleFileFilter(String {"gif", "jpg"});
   *
   * Note that the "." before the extension is not needed adn
   * will be ignored.
   *
   * @see #addExtension
   */
  public WDB_FilenameFilter(String[] filters) {
    this(filters, null);
  }

  /**
   * Creates a file filter from the given string array and description.
   * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
   *
   * Note that the "." before the extension is not needed and will be ignored.
   *
   * @see #addExtension
   */
  public WDB_FilenameFilter(String[] filters, String description) {
    this();
    for (int i = 0; i < filters.length; i++) {
      // add filters one by one
      addExtension(filters[i]);
    }
    if (description != null)
      setDescription(description);
  }

  /**
   * Return true if this file should be shown in the directory pane,
   * false if it shouldn't.
   *
   * Files that begin with "." are ignored.
   *
   * @see #getExtension
   */
  public boolean accept(File f) {
    if (f != null) {
      if (f.isDirectory()) {
        return true;
      }
      String fname = f.getName().toLowerCase();
      if (fname != null && this.filters.get(fname) != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return the extension portion of the file's name .
   *
   * @see #getExtension
   * @see FileFilter#accept
   */
  public String getExtension(File f) {
    if (f != null) {
      String filename = f.getName();
      int i = filename.lastIndexOf('.');
      if (i > 0 && i < filename.length() - 1) {
        return filename.substring(i + 1).toLowerCase();
      }
    }
    return null;
  }

  /**
   * Adds a filetype "dot" extension to filter against.
   *
   * For example: the following code will create a filter that filters
   * out all files except those that end in ".jpg" and ".tif":
   *
   *   ExampleFileFilter filter = new ExampleFileFilter();
   *   filter.addExtension("jpg");
   *   filter.addExtension("tif");
   *
   * Note that the "." before the extension is not needed and will be ignored.
   */
  public void addExtension(String extension) {
    if (this.filters == null) {
      this.filters = new Hashtable(5);
    }
    this.filters.put(extension.toLowerCase(), this);
    this.fullDescription = null;
  }

  /**
   * Returns the human readable description of this filter. For
   * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
   *
   * @see #setDescription
   * @see #setExtensionListInDescription
   * @see #isExtensionListInDescription
   * @see #getDescription
   */
  public String getDescription() {
    if (this.fullDescription == null) {
      if (this.description == null || isExtensionListInDescription()) {
        this.fullDescription = this.description == null ? "(" : this.description + " (";
        // build the description from the extension list
        Enumeration extensions = this.filters.keys();
        if (extensions != null) {
          this.fullDescription += "." + (String) extensions.nextElement();
          while (extensions.hasMoreElements()) {
            this.fullDescription += ", ." + (String) extensions.nextElement();
          }
        }
        this.fullDescription += ")";
      } else {
        this.fullDescription = this.description;
      }
    }
    return this.fullDescription;
  }

  /**
   * Sets the human readable description of this filter. For
   * example: filter.setDescription("Gif and JPG Images");
   *
   * @see #setDescription
   * @see #setExtensionListInDescription
   * @see #isExtensionListInDescription
   */
  public void setDescription(String description) {
    this.description = description;
    this.fullDescription = null;
  }

  /**
   * Determines whether the extension list (.jpg, .gif, etc) should
   * show up in the human readable description.
   *
   * Only relevent if a description was provided in the constructor
   * or using setDescription();
   *
   * @see #getDescription
   * @see #setDescription
   * @see #isExtensionListInDescription
   */
  public void setExtensionListInDescription(boolean b) {
    this.useExtensionsInDescription = b;
    this.fullDescription = null;
  }

  /**
   * Returns whether the extension list (.jpg, .gif, etc) should
   * show up in the human readable description.
   *
   * Only relevent if a description was provided in the constructor
   * or using setDescription();
   *
   * @see #getDescription
   * @see #setDescription
   * @see #setExtensionListInDescription
   */
  public boolean isExtensionListInDescription() {
    return this.useExtensionsInDescription;
  }
}