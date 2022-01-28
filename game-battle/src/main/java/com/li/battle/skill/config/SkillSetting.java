package com.li.battle.skill.config;

import com.li.battle.skill.model.SkillType;

/**
 * 技能配置接口
 * @author li-yuanwen
 * @date 2021/10/19
 */
public interface SkillSetting {


    /**
     * 获取技能id
     * @return 技能id
     */
    int getId();

    /**
     * 获取技能类型
     * @return 技能类型
     */
    SkillType getType();

}
