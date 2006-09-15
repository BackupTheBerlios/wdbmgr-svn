/*
 * Created on 28.03.2005
 *
 * This class manages the connection the Translator PHP program
 * done by Pifnet-Pif, 2005
 * 
 * @author    <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 * @copyright <a href="mailto:lousy.kizura@gmail.com">Kizura aka Gerhard Leibrock </a>
 *
 * @version   $Id: TranslatorHelper.java 195 2005-10-30 16:44:25Z gleibrock $
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
 * $HeadURL: svn://painkiller/wdb-manager/development/src/java/com/gele/tools/wow/wdbearmanager/helper/TranslatorHelper.java $
 * $Rev: 195 $
 */
package com.gele.tools.wow.wdbearmanager.helper;

import java.net.URLEncoder;

import com.gele.base.dbaccess.DTO_Interface;

/**
 * 
 * Receives a QUEST DTO and returns the String to store
 * the DTO inside the Translator database<br/>
 *
 * @author    $Author: gleibrock $ <a href="mailto:lousy.kizura@gmail.com">Kiz Urazgubi.</a>
 * @version   $Id: TranslatorHelper.java 195 2005-10-30 16:44:25Z gleibrock $
 * 
 */
public class TranslatorHelper {

  public TranslatorHelper() {

  }

  /**
   * To import a quest<br/>
   * <br/>
   * 
   * @param parDTO
   * 
   * @return URL encoded String containing all needed parameters to
   * access the translator.
   * 
   */
  public static String createTranslatorURLfromDTO(boolean parOverwrite,
      DTO_Interface parDTO, String parTranslatorID, String parTranslatorPass,
      String parTranlateVersion) {
    StringBuffer tmpVal = new StringBuffer();
    StringBuffer retValue = new StringBuffer();

    // num = questnummer
    // name = name
    // desc = description
    // details = details
    // levels = levels
    // level = level
    // zone = zone
    // objectives = objectives
    // seonddesc = second_desc
    // srcitem = src_item
    // nextquest = next_quest
    // rewardgold = reward_gold
    // questflags = quest_flags
    // deliver = deliver
    // kill = kill
    // rewardchoice = reward_choice
    // rewarditem = reward_item
    // unk10 = unk10
    // position = position
    // unk2 = unk2
    // faction = faction
    // reputation = reputation
    // end = end

    // tID = translatorID
    // tpw = translator's password
    // translateversion = translateversion

    if (parOverwrite == true) {
      retValue.append(("overwrite=yes"));
    } else {
      retValue.append(("overwrite=no"));
    }
    // num
    retValue.append("&num=" + parDTO.getColumnValue("wdb_QuestId").toString());
    // name
    retValue.append("&name=");
    retValue.append(URLEncoder.encode((String) parDTO
        .getColumnValue("wdb_name")));
    // desc
    if( ((String) parDTO.getColumnValue("wdb_desc")).length() > 0 ) {
	    retValue.append("&desc=");
  	  retValue.append(URLEncoder.encode((String) parDTO
    	    .getColumnValue("wdb_desc")));
    }else {
	    retValue.append("&desc=none");
    }
    // details = details
    if( ((String) parDTO.getColumnValue("wdb_details")).length() > 0 ) {
	    retValue.append("&details=");
  	  retValue.append(URLEncoder.encode((String) parDTO
    	    .getColumnValue("wdb_details")));
    }else {
	    retValue.append("&details=none");
    }
    // levels = levelsa, levelsb
    retValue.append("&levels=");
    tmpVal = new StringBuffer();
    tmpVal.append(((Integer) parDTO.getColumnValue("wdb_levelsa")).intValue());
    tmpVal.append(" ");
    tmpVal.append(((Integer) parDTO.getColumnValue("wdb_levelsb")).intValue());
    retValue.append(URLEncoder.encode(tmpVal.toString()));
    // level = level; compared with quests.scp, seems to be levelsb
    retValue.append("&level=");
    retValue
        .append(((Integer) parDTO.getColumnValue("wdb_levelsb")).intValue());
    // zone = zone
    retValue.append("&zone=");
    retValue.append(((Integer) parDTO.getColumnValue("wdb_zone")).intValue());
    // objectives = wdb_details
    retValue.append("&objectives=");
    retValue.append(URLEncoder.encode((String) parDTO
        .getColumnValue("wdb_details")));
    // seonddesc = second_desc
    if( ((String) parDTO.getColumnValue("wdb_second_desc")).length() > 0 ) {
	    retValue.append("&seconddesc=");
  	  retValue.append(URLEncoder.encode((String) parDTO
    	    .getColumnValue("wdb_second_desc")));
    }else {
	    retValue.append("&seconddesc=none");
    }
    // srcitem = src_item
    if (((Integer) parDTO.getColumnValue("wdb_src_item")).intValue() != 0) {
      retValue.append("&srcitem=");
      retValue.append(((Integer) parDTO.getColumnValue("wdb_src_item"))
          .intValue());
    } else {
      retValue.append("&srcitem=none");
    }
    // not stored inside DWB
    retValue.append("&rewardxp=none");

    // nextquest = next_quest
    if (((Integer) parDTO.getColumnValue("wdb_next_quest")).intValue() != 0) {
      retValue.append("&nextquest=");
      retValue.append(((Integer) parDTO.getColumnValue("wdb_next_quest"))
          .intValue());
    } else {
      retValue.append("&nextquest=none");
    }
    // rewardgold = reward_gold
    if (((Integer) parDTO.getColumnValue("wdb_reward_gold")).intValue() != 0) {
      retValue.append("&rewardgold=");
      retValue.append(((Integer) parDTO.getColumnValue("wdb_reward_gold"))
          .intValue());
    } else {
      retValue.append("&rewardgold=none");
    }
    // questflags = quest_flags
    retValue.append("&questflags=");
    retValue.append(URLEncoder.encode((String) parDTO
        .getColumnValue("wdb_quest_flags")));
    // deliver = deliver
    // can occur max. 4 times inside quest.scp
    //
    // Achtung: Deliver kann 1 oder mehrfach in quests.scp vorkommen
    if (((Integer) parDTO.getColumnValue("wdb_deliver1")).intValue() != 0) {
      retValue.append("&deliver=");
      tmpVal = new StringBuffer();
      tmpVal.append(((Integer) parDTO.getColumnValue("wdb_deliver1"))
          .intValue());
      tmpVal.append(" ");
      tmpVal.append(((Integer) parDTO.getColumnValue("wdb_deliver1Amount"))
          .intValue());
      if (((Integer) parDTO.getColumnValue("wdb_deliver2")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_deliver2"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_deliver2Amount"))
            .intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_deliver3")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_deliver3"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_deliver3Amount"))
            .intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_deliver4")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_deliver4"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_deliver4Amount"))
            .intValue());
      }
      tmpVal.append("~end");
      retValue.append(URLEncoder.encode(tmpVal.toString()));
    } else {
      retValue.append("&deliver=none");
    }

    // kill = kill
    // can occur max. 4 times inside quest.scp
    if (((Integer) parDTO.getColumnValue("wdb_NPCObjective1")).intValue() != 0) {
      tmpVal = new StringBuffer();
      retValue.append("&kill=");
      tmpVal.append(((Integer) parDTO.getColumnValue("wdb_NPCObjective1"))
          .intValue());
      tmpVal.append(" ");
      tmpVal
          .append(((Integer) parDTO.getColumnValue("wdb_NPCObjective1Amount"))
              .intValue());
      if (((Integer) parDTO.getColumnValue("wdb_NPCObjective2")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_NPCObjective2"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO
            .getColumnValue("wdb_NPCObjective2Amount")).intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_NPCObjective3")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_NPCObjective3"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO
            .getColumnValue("wdb_NPCObjective3Amount")).intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_NPCObjective4")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_NPCObjective4"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO
            .getColumnValue("wdb_NPCObjective4Amount")).intValue());
      }
      tmpVal.append("~end");
      retValue.append(URLEncoder.encode(tmpVal.toString()));
    } else {
      retValue.append("&kill=none");
    }

    // rewardchoice = reward_choice
    //
    // can occure up to 6 times (eg. quest 18 has 5 reward_choice)
    if (((Integer) parDTO.getColumnValue("wdb_reward_choice1a")).intValue() != 0) {
      retValue.append("&rewardchoice=");
      tmpVal = new StringBuffer();
      tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice1a"))
          .intValue());
      tmpVal.append(" ");
      tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice1b"))
          .intValue());
      if (((Integer) parDTO.getColumnValue("wdb_reward_choice2a")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice2a"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice2b"))
            .intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_reward_choice3a")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice3a"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice3b"))
            .intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_reward_choice4a")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice4a"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice4b"))
            .intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_reward_choice5a")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice5a"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice5b"))
            .intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_reward_choice6a")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice6a"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_choice6b"))
            .intValue());
      }
      tmpVal.append("~end");
      retValue.append(URLEncoder.encode(tmpVal.toString()));
    } else {
      retValue.append("&rewardchoice=none");
    }

    // rewarditem = reward_item
    // up to 4 times
    if (((Integer) parDTO.getColumnValue("wdb_reward_item1a")).intValue() != 0) {
      retValue.append("&rewarditem=");
      tmpVal = new StringBuffer();
      tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_item1a"))
          .intValue());
      tmpVal.append(" ");
      tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_item1b"))
          .intValue());
      if (((Integer) parDTO.getColumnValue("wdb_reward_item2a")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_item2a"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_reward_item2b"))
            .intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_Unk21")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_Unk21"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_Unk22"))
            .intValue());
      }
      if (((Integer) parDTO.getColumnValue("wdb_Unk23")).intValue() != 0) {
        tmpVal.append("~");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_Unk23"))
            .intValue());
        tmpVal.append(" ");
        tmpVal.append(((Integer) parDTO.getColumnValue("wdb_Unk24"))
            .intValue());
      }
      tmpVal.append("~end");
      retValue.append(URLEncoder.encode(tmpVal.toString()));
    } else {
      retValue.append("&rewarditem=none");
    }

    // unk10 = wdb_Unk7
    if (((Integer) parDTO.getColumnValue("wdb_Unk7")).intValue() != 0) {
      retValue.append("&unk10=");
      retValue.append(((Integer) parDTO.getColumnValue("wdb_Unk7")).intValue());
    } else {
      retValue.append("&unk10=none");
    }

    // position = position
    // Consists of 4 values, eg [quest 61]
    if ((parDTO.getColumnValue("wdb_position1")).toString().equals(".000000") == false) {
      retValue.append("&position=");
      tmpVal = new StringBuffer();
      tmpVal.append((parDTO.getColumnValue("wdb_position0")).toString());
      tmpVal.append(" ");
      tmpVal.append((parDTO.getColumnValue("wdb_position1")).toString());
      tmpVal.append(" ");
      tmpVal.append((parDTO.getColumnValue("wdb_position2")).toString());
      tmpVal.append(" ");
      tmpVal.append((parDTO.getColumnValue("wdb_position3")).toString());
      retValue.append(URLEncoder.encode(tmpVal.toString()));
    } else {
      retValue.append("&position=none");
    }

    // unk2c = unk2
    // 
    if (((Integer) parDTO.getColumnValue("wdb_Unk14")).intValue() != 0) {
      retValue.append("&unk2c=");
      retValue
          .append(((Integer) parDTO.getColumnValue("wdb_Unk14")).intValue());
    } else {
      retValue.append("&unk2c=none");
    }

    // faction = wdb_Unk8
    //
    if (((Integer) parDTO.getColumnValue("wdb_Unk8")).intValue() != 0) {
      retValue.append("&faction=");
      retValue.append(((Integer) parDTO.getColumnValue("wdb_Unk8")).intValue());
    } else {
      retValue.append("&faction=none");
    }

    // reputation = wdb_Unk9
    // end = end
    if (((Integer) parDTO.getColumnValue("wdb_Unk9")).intValue() != 0) {
      retValue.append("&reputation=");
      retValue.append(((Integer) parDTO.getColumnValue("wdb_Unk9")).intValue());
    } else {
      retValue.append("&reputation=none");
    }
    
    // not stored inside DWB
    // end
    retValue.append("&end=none");


    // tID = translatorID
    retValue.append("&tID=");
    retValue.append(URLEncoder.encode(parTranslatorID));
    // tpw = translator's password
    retValue.append("&tpw=");
    retValue.append(URLEncoder.encode(parTranslatorPass));
    // translateversion = translateversion
    retValue.append("&translateversion=");
    retValue.append(URLEncoder.encode(parTranlateVersion));

    return retValue.toString();
  }// createURLfromDTO
}// TranslatorHelper.java
