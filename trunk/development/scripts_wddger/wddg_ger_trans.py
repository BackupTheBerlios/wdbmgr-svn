# Jython example to access WoWWDBManager
# kizura, March 2005
# Needs at least WowWDBManager PRE8
#
# All rights reserved.
#
# This script is used for WoWWDBManager
#
# Transfer all QUEST information stored inside the database
# to the Translator done by pifnet-pif
#
#
# overwrite = yes|no
#             yes - Update quests
#             no  - Only insert
#
# tID       = TRANS_ID Id/username of the translator.
#
# tpw       = TRANS_PASS password of the translator
#
# transversion = TRANS_VERSION
#                eg "de21" for german, WDDG Version 2.1
#                (Country code is always 2 chars)
#
# destURL      = wowger.wo.funpic.de
#                Set your destination URL (*no* http://, just the address)
#
# akey         = Administrator key
#                Only admins are allowed to use this functionality
#
# Every script gets "wdbmgrapi" variable
# wdbmgrapi is of type "WoWWDBManager_I"
#
# Please refer to doc/javadocs for more information
#
# version   $Rev: 162 $
#
from java.util import *
from java.net import *
from java.io import *
import sys

# Create a DTO from the name of the table
# (Make sure the XML file exists)
dto = wdbmgrapi.createDTOfromTable( "questcache" )

# Connect to database
countOfEntries = wdbmgrapi.getCountOf(dto)
print 'Number of entries in table "questcache": %d' %(countOfEntries)

update = 1
if overwrite == "no":
  update = 0

objQuestcache = wdbmgrapi.getAllObjectsTable('questcache') 
itObjs = objQuestcache.iterator()

# Statistics
questDoesNotExist406 = 0
questWritten200 = 0
questExists201 = 0
questIncomplete402 = 0
questQuestIsSame403 = 0
questQuestInDbButDifferent405 = 0

percentageFinished = 10;
counter = 0
tenPercent = countOfEntries / 10

for i in itObjs:
  counter = counter + 1
  if counter == tenPercent:
    counter = 0
    sys.stdout.write(str(percentageFinished))
    sys.stdout.write("#")
    percentageFinished = percentageFinished + 10

  transURL = wdbmgrapi.createTranslatorURLfromDTO( update, i, tID, tpw, transversion )
  myURL = URL( "http", destURL, "/import_original.php?"+transURL+"&akey="+akey )
  bufReader = BufferedReader( InputStreamReader(myURL.openStream()))
  inputLine = bufReader.readLine()
  if inputLine== "500":
    print "Wrong username/password/adminkey"
    break;

  if inputLine== "200":
    questWritten200 = questWritten200 + 1
  elif inputLine== "201":
    questExists201 = questExists201 + 1
  elif inputLine== "406":
    questDoesNotExist406 = questDoesNotExist406 + 1
  elif inputLine== "402":
    questIncomplete402 = questIncomplete402 + 1
  elif inputLine== "403":
    questQuestIsSame403 = questQuestIsSame403 + 1
  elif inputLine== "405":
    questQuestInDbButDifferent405 = questQuestInDbButDifferent405 + 1
  else:
    print inputLine

  bufReader.close()
# Statistics
print " "
print "406: Quest does not exist    : %d" % (questDoesNotExist406)
print "200: Quest written           : %d" % (questWritten200)
print "201: Quest exists            : %d" % (questExists201)
print "402: Quest incomplete        : %d" % (questIncomplete402)
print "403: Quest identical         : %d" % (questQuestIsSame403)
print "405: Quest different, use UPD: %d" % (questQuestInDbButDifferent405)

