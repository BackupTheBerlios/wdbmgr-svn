# Jython example to access WDBearManager
# kizura, March/April 2005
# Needs at least WDBearManager PRE8
#
# All rights reserved.
#
# This script is used for WDBearManager
#
# It reads the entries from table "questcache" and
# prints out the quest_id and the quest's name
# -> Please check "questcache.xml" for all attributes! <-
#
# Every script gets "wdbmgrapi" variable
# wdbmgrapi is of type "WDBearManager_API"
# Please refer to Java Docs for more information
#
# version   $Rev: 155 $
#
from java.util import *

# Connect to database
numObjs = wdbmgrapi.getCountOfTable('questcache')
print 'Number of entries in table "QuestCache": %d' % (numObjs)
if numObjs > 0:
  objQuestcache = wdbmgrapi.getAllObjectsTable('questcache') 
  print "Count: %d" % objQuestcache.size()
  itObjs = objQuestcache.iterator()
  for i in itObjs:
    print "QuestID: %d, QuestNName: %s" % (i.getColumnValue("wdb_QuestId"),i.getColumnValue("wdb_name"))
