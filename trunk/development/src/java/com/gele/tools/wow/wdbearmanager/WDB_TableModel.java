
package com.gele.tools.wow.wdbearmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import com.gele.base.dbaccess.DTO_Interface;
/*
 * Created on 09.01.2005
 *
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: WDB_TableModel.java 209 2006-09-04 14:34:24Z gleibrock $
 *
 * License:
 * This software is placed under the GNU GPL.
 * For further information, see the page :
 * http://www.gnu.org/copyleft/gpl.html.
 * For a different license please contact the author.
 * 
 * $LastChangedDate: 2006-09-04 16:34:24 +0200 (Mo, 04 Sep 2006) $ 
 * $LastChangedBy: gleibrock $ 
 * $LastChangedRevision: 209 $ 
 * $Author: gleibrock $ 
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/WDB_TableModel.java $
 * $Rev: 209 $
 * 
 */

/**
 * 
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Rev: 209 $
 * 
 */
public class WDB_TableModel extends AbstractTableModel {
  
  /**
   * 
   */
  private static final long serialVersionUID = -5002211617228485582L;

  private String[] columnNames = null;

  Collection colWDB_DTOs = null;
  private Object[][] data = null;

  public void setData( Iterator parColNames, Collection parWDB_DTOs) {
   ArrayList myAL = new ArrayList();
   while (parColNames.hasNext()) {
     myAL.add(parColNames.next());
   }
   String[] arrColNames = new String[myAL.size()];
   Iterator itCols = myAL.iterator();
   int counter = 0;
   while( itCols.hasNext() ) {
     arrColNames[counter] = (String)itCols.next();
     counter++;
   }
    
   this.columnNames = arrColNames; 
   this.colWDB_DTOs = parWDB_DTOs;
   // construct data
   this.data = new Object[parWDB_DTOs.size()][arrColNames.length];
   DTO_Interface loopDTO = null;
   Iterator itWBDs = parWDB_DTOs.iterator();
   counter = 0;
   while(itWBDs.hasNext()) {
     loopDTO = (DTO_Interface)itWBDs.next();
     this.data[counter] = new Object[arrColNames.length];
     for( int j = 0; j<arrColNames.length; j++ ) {
       this.data[counter][j] = loopDTO.getColumnValue(arrColNames[j]); 
     }
     counter++;
   }
  }// setData

  public int getColumnCount() {
    return this.columnNames.length;
  }

  public int getRowCount() {
//System. out.println("WDB_TableModel -> getRowCount, data: "+ this.data );
    return this.data.length;
  }

  public String getColumnName(int col) {
//System. out.println(this.columnNames[col]);
    return col+":"+this.columnNames[col];
  }

  public Object getValueAt(int row, int col) {
    return this.data[row][col];
  }

  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }
}