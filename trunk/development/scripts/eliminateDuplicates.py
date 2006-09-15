# kizura, August 2006
# Needs at least WDBearManager 1.0
#
# All rights reserved.
#
# This script is used for WDBearManager
#
# This script will scan a WDBEAR database for duplicate entries.
# If it finds them, it write some information to a LOG file
# and eliminates all duplicates found.
#
# Every script gets "wdbmgrapi" variable
# wdbmgrapi is of type "WDBearManager_API"
# Please refer to Java Docs for more information
#
# version   $Rev: 155 $
#
import java.util
import java.io
import java.lang
import java.text
import java.sql

# Tables
tabNames = 'creaturecache', 'gameobjectcache', 'itemcache', 'itemnamecache', 'npccache', 'pagetextcache', 'questcache'
# 'itemtextcaxhe',
# Locales
infLocales = 'deDE', 'enUS', 'enGB'
# DB prefix - See wdbearmanager_sql.properties
dbPrefix = 'wowwdb_'

filLOG = java.io.File("eliminateDuplicates.log")
print filLOG.getAbsolutePath()
myBW = java.io.BufferedWriter(java.io.FileWriter(filLOG))

for i in tabNames:
  #print i
  # Create a DTO from the name of the table
  # (Make sure the XML file exists)
  dto = wdbmgrapi.createDTOfromTable( i )
  myBW.write(java.util.Date().toString())
  myBW.write('\n')
  myBW.write('Table : ')
  myBW.write(i)
  myBW.write('\n')
  for j in infLocales:
    myBW.write('Locale : ')
    myBW.write(j)
    myBW.write('\n')
    #print j
    # Only use WDB info with this Locale
    dto.setColumnValue( 'inf_locale', j )
    #print dto.getPrimaryKeyColumn()
    wdbQuery = 'SELECT WDB_ID, COUNT(WDB_ID) AS NumOccurrences '
    wdbQuery = wdbQuery + 'FROM ' + dbPrefix+i + ' '
    wdbQuery = wdbQuery + 'where inf_locale = \''+j+'\' '
    wdbQuery = wdbQuery + 'GROUP BY WDB_ID HAVING ( COUNT(WDB_ID) > 1 )'
    # print wdbQuery
    myConnection = wdbmgrapi.getConnection()
    myStatement = myConnection.createStatement()
    myResultSet = myStatement.executeQuery(wdbQuery)
    objsDeleted = 0
    numDeleted = 0
    while myResultSet.next():
      wdbID = myResultSet.getInt(1)
      wdbIDCount = myResultSet.getInt(2)
      myBW.write('WDB_ID : ')
      myBW.write(java.lang.String.valueOf(wdbID))
      myBW.write(' - ')
      myBW.write(java.lang.String.valueOf(wdbIDCount))
      myBW.write(' times in database ')
      myBW.write('\n')

      # print wdbID
      # print wdbIDCount
      # Read the record
      print ">>" + dto.getPrimaryKeyValues()[0]
      dto.setColumnValue( dto.getPrimaryKeyValues()[0], wdbID )
      readDTO = wdbmgrapi.getAllObjects(dto).firstElement()
      # Delete all occurrences
      numDeleted = wdbmgrapi.deleteObject( dto )
      objsDeleted = objsDeleted + numDeleted
      # Store it
      wdbmgrapi.insertOrUpdateToSQLDB( readDTO, java.lang.Boolean.FALSE )
    print "Deleted Dups: " + i + ' ' + j + ': ' + java.lang.String.valueOf(objsDeleted)
    myBW.write('Objects eliminated: ')
    myBW.write(java.lang.String.valueOf(objsDeleted))
    myBW.write('\n')

    myResultSet.close()
    myStatement.close()
myBW.close()
print 'Logfile: ' + filLOG.getAbsolutePath()

  
