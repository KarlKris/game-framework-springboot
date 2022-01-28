package com.li.battle.skill.config;

import java.util.Map;

/**
 * 技能仓库
 * @author li-yuanwen
 * @date 2021/10/20
 */
public class SkillStorage {

    /** 一次性技能库 **/
    private Map<Integer, GeneralSkillSetting> generalSkills;
    /** 持续性技能库 **/
    private Map<Integer, ChannelSkillSetting> channelSkills;
    /** 被动技能库 **/
    private Map<Integer, PassiveSkillSetting> passiveSkills;
    /** 开关类技能库 **/
    private Map<Integer, ToggleSkillSetting> toggleSkills;
    /** 激活类技能库 **/
    private Map<Integer, ActivateSkillSetting> activateSkills;

    /** 技能id2技能总类型 **/
    private Map<Integer, Byte> skillId2Type;


    public GeneralSkillSetting getGeneralSkillSetting(int id) {
        return generalSkills.get(id);
    }

    public ChannelSkillSetting getChannelSkillSetting(int id) {
        return channelSkills.get(id);
    }

    public PassiveSkillSetting getPassiveSkillSetting(int id) {
        return passiveSkills.get(id);
    }

    public ToggleSkillSetting getToggleSkillSetting(int id) {
        return toggleSkills.get(id);
    }

    public ActivateSkillSetting getActivateSkillSetting(int id) {
        return activateSkills.get(id);
    }

    public Byte getSkillType(int skillId) {
        return skillId2Type.get(skillId);
    }
}
