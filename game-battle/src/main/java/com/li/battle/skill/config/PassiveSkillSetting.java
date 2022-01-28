package com.li.battle.skill.config;

import com.li.battle.skill.model.SkillType;

/**
 * 被动技能配置表
 * @author li-yuanwen
 * @date 2021/10/19
 */
public class PassiveSkillSetting implements SkillSetting {

    /** 技能唯一标识 **/
    protected int id;

    /** 技能初始化执行-效果表#id **/
    protected int initEffectId;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SkillType getType() {
        return SkillType.PASSIVE_ABILITY;
    }

    public int getInitEffectId() {
        return initEffectId;
    }
}
