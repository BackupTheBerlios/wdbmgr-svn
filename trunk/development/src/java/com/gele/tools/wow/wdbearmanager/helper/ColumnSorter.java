/*
 * CreateDateTime: 14.03.2005, 16:25:28
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: ColumnSorter.java 195 2005-10-30 16:44:25Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2005-10-30 17:44:25 +0100 (So, 30 Okt 2005) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 195 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/helper/ColumnSorter.java $
 * $Rev: 195 $
 * 
 */
package com.gele.tools.wow.wdbearmanager.helper;

import java.util.Comparator;
import java.util.Vector;

/**
 * Code taken from: http://javaalmanac.com/egs/javax.swing.table/Sorter.html
 * 
 */
//This comparator is used to sort vectors of data
public class ColumnSorter implements Comparator {
  int colIndex;

  boolean ascending;

  public ColumnSorter(int colIndex, boolean ascending) {
    this.colIndex = colIndex;
    this.ascending = ascending;
  }

  //This comparator is used to sort vectors of data
  public int compare(Object a, Object b) {
    Vector v1 = (Vector) a;
    Vector v2 = (Vector) b;
    Object o1 = v1.get(this.colIndex);
    Object o2 = v2.get(this.colIndex);

    // Treat empty strains like nulls
    if (o1 instanceof String && ((String) o1).length() == 0) {
      o1 = null;
    }
    if (o2 instanceof String && ((String) o2).length() == 0) {
      o2 = null;
    }

    // Sort nulls so they appear last, regardless
    // of sort order
    if (o1 == null && o2 == null) {
      return 0;
    } else if (o1 == null) {
      return 1;
    } else if (o2 == null) {
      return -1;
    } else if (o1 instanceof Comparable) {
      if (this.ascending) {
        return ((Comparable) o1).compareTo(o2);
      } else {
        return ((Comparable) o2).compareTo(o1);
      }
    } else {
      if (this.ascending) {
        return o1.toString().compareTo(o2.toString());
      } else {
        return o2.toString().compareTo(o1.toString());
      }
    }
  }
}

