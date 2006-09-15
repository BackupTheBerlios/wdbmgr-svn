# Jython example to access WoWWDBManager
# kizura, March 2005
# Needs at least WowWDBManager PRE7
#
# All rights reserved.
#
# This script is used for WoWWDBManager
#
# Allow the user to query the WDB database.
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
#
# Every script gets "wdbmgrapi" variable
# wdbmgrapi is of type "WoWWDBManager_API"
# Please refer to Java Docs for more information
#
#
from java.util import *

# Create a DTO from the name of the table
# (Make sure the "questcache.xml" file exists)
# print out the DTO's primary column
dto = wdbmgrapi.createDTOfromTable( tabName )
print dto.getPrimaryKeyColumn()

# Connect to Hypersonic SQL database
# Print info about the database
# (Just to show, that we can connect to *any* supported database)
#sqlMgrHSQL = wdbmgrapi.connectToDatabase( "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://localhost", "sa", "wdbpasswd", "" )
#print wdbmgrapi.getDatabaseMetaData(sqlMgrHSQL).getDatabaseProductName()
#
# end of: Show how to connect to *any* supported database

# Connect to database
print 'Number of entries in table "QuestCache"'
print wdbmgrapi.getCountOf('questcache') 
objQuestcache = wdbmgrapi.getAllObjects('questcache') 
print "Count: %d" % objQuestcache.size()
itObjs = objQuestcache.iterator()
for i in itObjs:
  print "QuestID: %d, QuestNName: %s" % (i.getColumnValue("wdb_QuestId"),i.getColumnValue("wdb_name"))

# The user sets param=value in the GUI
print param