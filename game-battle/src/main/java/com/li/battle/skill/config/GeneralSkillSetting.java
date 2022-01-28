package com.li.battle.skill.config;

import com.li.battle.skill.model.SkillType;
import lombok.Getter;

/**
 * 主动技能--一次性效果技能表
 * @author li-yuanwen
 * @date 2021/10/19
 */
@Getter
public class GeneralSkillSetting extends AbstractSkillSetting {

    /** 技能施法阶段--效果表#id **/
    protected int spellEffectId;

    @Override
    public SkillType getType() {
        return SkillType.GENERAL_ABILITY;
    }
}
