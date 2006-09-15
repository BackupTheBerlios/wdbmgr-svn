# Jython example to access WoWWDBManager
# kizura, March 2005
# Needs at least WowWDBManager PRE4
#
# All rights reserved.
#
# This script is used for WoWWDBManager
#
# Every script gets "wdbmgrapi" variable
# wdbmgrapi is of type "WoWWDBManager_API"
# Please refer to Java Docs for more information
#
# It reads the entries from table "questcache" and
# prints out the quest_id and the quest's name
# -> Please check "questcache.xml" for all attributes!Â´<-
#
from java.util import *
dto = wdbmgrapi.createDTOfromTable( "questcache" )
print dto.getPrimaryKeyColumn()

# Connect to Hypersonic SQL database
sqlMgrHSQL = wdbmgrapi.connectToDatabase( "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost", "sa", "wdbpasswd", "" )

print wdbmgrapi.getDatabaseMetaData(sqlMgrHSQL).getDatabaseProductName()
# Connect to MySQL database

print 'Number of entries in table "QuestCache"'
print wdbmgrapi.getCountOf('questcache') 
objQuestcache = wdbmgrapi.getAllObjects('questcache') 
print "Anzahl: %d" % objQuestcache.size()
itObjs = objQuestcache.iterator()
for i in itObjs:
  print "QuestID: %d, QuestNName: %s" % (i.getColumnValue("wdb_QuestId"),i.getColumnValue("wdb_name"))
