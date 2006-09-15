#
# Created on 2005 Apr 05
# Version 1.0
#
# Jython example to access WoWWDBManager
# kizura, March 2005
# Needs at least WowWDBManager PRE8
#
# All rights reserved.
#
# This script is used for WoWWDBManager
#
# Transfer all GAMEOBJECTS information stored inside the database
# to the Translator done by pifnet-pif
#
#
# overwrite = yes|no
#             yes - Update objects
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

from java.net import URLEncoder;
from java.net import URL;
from java.io import *
import sys

#
# To import gameobjects to the Translator
# <br/>
#  
# param parDTO
#  
# return URL encoded String containing all needed parameters to
# access the translator.
#
#

def createTranslatorURLfromDTO( parOverwrite, parDTO, parTranslatorID, parTranslatorPass,  parTranlateVersion):

  # num = gameobjectnummer 
  # name = wdb_Name1
  # type = wdb_Unk3
  # model = wdb_ModellID
  # sound0 = wdb_Unk9
  # sound1 = wdb_Unk10
  # sound2 = wdb_Unk11
  # sound3 = wdb_Unk12
  # sound4 = wdb_Unk13
  # sound5 = wdb_Unk14
  # sound6 = wdb_Unk15
  # sound7 = wdb_Unk16
  # sound8 = wdb_Unk17
  # sound9 = wdb_Unk18
  # loot = Unknown in WDB format
  # size = Unknown in WDB format
  # flags = Unknown in WDB format
  # 
  # tID
  # tpw
  # translateversion
  retValue = ""

  if parOverwrite == "yes":
    retValue = retValue + "overwrite=yes"
  else:
    retValue = retValue + "overwrite=no"

  # num
  retValue  = retValue + "&num=" + parDTO.getColumnValue("wdb_ObjectID").toString()
  # name = wdb_Name1
  retValue = retValue + "&name=" + URLEncoder.encode( parDTO.getColumnValue("wdb_Name1"))
  # type = wdb_Unk3
  retValue = retValue + "&type=" + parDTO.getColumnValue("wdb_Unk3").toString()
  # model = wdb_ModellID
  retValue = retValue + "&model=" + parDTO.getColumnValue("wdb_ModellID").toString()
  # sound0 = wdb_Unk9
  dtoVal = (parDTO.getColumnValue("wdb_Unk9") )
  if dtoVal != 0:
    retValue = retValue + "&sound0=" + str(dtoVal)
  else:
    retValue = retValue + "&sound0=none"
  # sound1 = wdb_Unk10
  dtoVal = (parDTO.getColumnValue("wdb_Unk10") )
  if dtoVal != 0:
    retValue = retValue + "&sound1=" + str(dtoVal)
  else:
    retValue = retValue + "&sound1=none"
  # sound2 = wdb_Unk11
  dtoVal = (parDTO.getColumnValue("wdb_Unk11") )
  if dtoVal != 0:
    retValue = retValue + "&sound2=" + str(dtoVal)
  else:
    retValue = retValue + "&sound2=none"
  # sound3 = wdb_Unk12
  dtoVal = (parDTO.getColumnValue("wdb_Unk12") )
  if dtoVal != 0:
    retValue = retValue + "&sound3=" + str(dtoVal)
  else:
    retValue = retValue + "&sound3=none"
  # sound4 = wdb_Unk13
  dtoVal = (parDTO.getColumnValue("wdb_Unk13") )
  if dtoVal != 0:
    retValue = retValue + "&sound4=" + str(dtoVal)
  else:
    retValue = retValue + "&sound4=none"
  # sound5 = wdb_Unk14
  dtoVal = (parDTO.getColumnValue("wdb_Unk14") )
  if dtoVal != 0:
    retValue = retValue + "&sound5=" + str(dtoVal)
  else:
    retValue = retValue + "&sound5=none"
  # sound6 = wdb_Unk15
  dtoVal = (parDTO.getColumnValue("wdb_Unk15") )
  if dtoVal != 0:
    retValue = retValue + "&sound6=" + str(dtoVal)
  else:
    retValue = retValue + "&sound6=none"
  # sound7 = wdb_Unk16
  dtoVal = (parDTO.getColumnValue("wdb_Unk16") )
  if dtoVal != 0:
    retValue = retValue + "&sound7=" + str(dtoVal)
  else:
    retValue = retValue + "&sound7=none"
  # sound8 = wdb_Unk17
  dtoVal = (parDTO.getColumnValue("wdb_Unk17") )
  if dtoVal != 0:
    retValue = retValue + "&sound8=" + str(dtoVal)
  else:
    retValue = retValue + "&sound8=none"
  # sound9 = wdb_Unk18
  dtoVal = (parDTO.getColumnValue("wdb_Unk18") )
  if dtoVal != 0:
    retValue = retValue + "&sound9=" + str(dtoVal)
  else:
    retValue = retValue + "&sound9=none"
  # loot = Unknown in WDB format
  retValue = retValue + "&loot=none"
  # size = Unknown in WDB format
  retValue = retValue + "&size=none"
  # flags = Unknown in WDB format
  retValue = retValue + "&flags=none"
  #  
  # tID = translatorID
  retValue = retValue + "&tID="
  retValue = retValue + URLEncoder.encode(parTranslatorID)
  # tpw = translator's password
  retValue = retValue + "&tpw="
  retValue = retValue + URLEncoder.encode(parTranslatorPass)
  # translateversion = translateversion
  retValue = retValue + "&translateversion="
  retValue = retValue + URLEncoder.encode(parTranlateVersion)

  return retValue


# Create a DTO from the name of the table
# (Make sure the XML file exists)
dto = wdbmgrapi.createDTOfromTable( "gameobjectcache" )

# Some objects have negative ID, dont know if this is correct
# !Filter them out!
dto.setColumnValue( "wdb_ObjectID", ">=0" )

# Connect to database
countOfEntries = wdbmgrapi.getCountOf(dto)
print 'Number of entries in table "gameobjectcache": %d' %(countOfEntries)

if countOfEntries == 0:
  print "No data available - EXIT"
  sys.exit(1)

objGameobjectcache = wdbmgrapi.getAllObjects(dto) 

itObjs = objGameobjectcache.iterator()
counter = 0

# Statistics
objectDoesNotExist406 = 0
objectWritten200 = 0
objectExists201 = 0
objectIncomplete402 = 0
objectObjectIsSame403 = 0
objectObjectInDbButDifferent405 = 0

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

  transURL = createTranslatorURLfromDTO( overwrite, i, tID, tpw, transversion)

  # with admin key
  myURL = URL( "http", destURL, "/import_gameobjects_original.php?"+transURL+"&akey="+akey )
  #myURL = URL( "http", destURL, "/import_gameobjects_original.php?"+transURL )
  bufReader = BufferedReader( InputStreamReader(myURL.openStream()))
  inputLine = bufReader.readLine()
  if inputLine== "500":
    print "Wrong username/password/adminkey"
    break;

  if inputLine== "200":
    objectWritten200 = objectWritten200 + 1
  elif inputLine== "201":
    objectExists201 = objectExists201 + 1
  elif inputLine== "406":
    objectDoesNotExist406 = objectDoesNotExist406 + 1
  elif inputLine== "402":
    objectIncomplete402 = objectIncomplete402 + 1
  elif inputLine== "403":
    objectObjectIsSame403 = objectObjectIsSame403 + 1
  elif inputLine== "405":
    objectObjectInDbButDifferent405 = objectObjectInDbButDifferent405 + 1
  else:
    print inputLine

  bufReader.close()
# Statistics
print " "
print "406: Object does not exist    : %d" % (objectDoesNotExist406)
print "200: Objectwritten            : %d" % (objectWritten200)
print "201: Object exists            : %d" % (objectExists201)
print "402: Object incomplete        : %d" % (objectIncomplete402)
print "403: Object identical         : %d" % (objectObjectIsSame403)
print "405: Object different, use UPD: %d" % (objectObjectInDbButDifferent405)
