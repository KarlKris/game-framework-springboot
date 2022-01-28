package com.li.battle.skill.config;

import com.li.battle.skill.model.SkillType;
import lombok.Getter;

/**
 * 开关类技能配置表
 * @author li-yuanwen
 * @date 2021/10/19
 */
@Getter
public class ToggleSkillSetting implements SkillSetting {

    /** 技能唯一标识 **/
    private int id;

    /** 技能开启/关闭时，添加/移除的buff表#id **/
    private int buffId;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SkillType getType() {
        return SkillType.TOGGLE_ABILITY;
    }
}
