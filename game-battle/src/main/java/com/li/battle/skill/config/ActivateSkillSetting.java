package com.li.battle.skill.config;

import com.li.battle.skill.model.SkillType;
import lombok.Getter;

/**
 * 激活类技能配置表
 * @author li-yuanwen
 * @date 2021/10/19
 */
@Getter
public class ActivateSkillSetting implements SkillSetting {

    /** 技能唯一标识 **/
    private int id;

    /** 技能激活/关闭时，添加/移除的buff表#id **/
    private int buffId;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SkillType getType() {
        return SkillType.ACTIVATE_ABILITY;
    }
}
