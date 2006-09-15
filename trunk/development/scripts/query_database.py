# Jython example to access WDBearManager
# kizura, March 2005
# Needs at least WDBearManager PRE7
#
# All rights reserved.
#
# This script is used for WDBearManager
#
# Allow the user to query the WDBear database.
#
# Generates a TXT file with the found entries from the
# database.
#
# params (eg: tabName=questcache):
# tabName - Name of the database table
#           creaturecache
#           gameobjectcache
#           itemcache
#           itemtextcaxhe
#           npccache
#           pagetextcache
#           questcache
#
# colName - Use this column for searching
#
# colValue - Compare all columns with this value
#
# colOrder - Name of the column to sort the result (ASC)
#
#
# Every script gets "wdbmgrapi" variable
# wdbmgrapi is of type "WDBearManager_API"
# Please refer to Java Docs for more information
#
# version   $Rev: 155 $
#
from java.util import *
from java.io import *

# Create a DTO from the name of the table
# (Make sure the XML file exists)
dto = wdbmgrapi.createDTOfromTable( tabName )
# Prepare the query to the database
dto.setColumnValue( colName, colValue );

# Order BY
orderDTO = wdbmgrapi.createDTOfromTable( tabName )
# Prepare the query to the database
orderDTO.setColumnValue( colOrder, "ASC" );


# Connect to database
print "Searching for entries in table %s where %s = %s" % (tabName, colName, colValue)
print 'Number of entries in table "%s": %d' %(tabName, wdbmgrapi.getCountOf(dto))
try:
  objWDBCache = wdbmgrapi.getAllObjects(dto, orderDTO) 
  print "Count: %d" % objWDBCache.size()
  txtFile = File(".")
  # Use API method (we are lazy, aren't we)
  wdbmgrapi.writeTXT( txtFile, objWDBCache )
  print "TXT file written: %s" %(txtFile.getAbsolutePath())
  itObjs = objWDBCache.iterator()
#  for i in itObjs:
#    print "QuestID: %d, QuestNName: %s" % (i.getColumnValue("wdb_QuestId"),i.getColumnValue("wdb_name"))
    
except:
  print "No data found"
