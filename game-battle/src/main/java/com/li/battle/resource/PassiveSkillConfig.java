package com.li.battle.resource;

import com.li.battle.effect.Effect;
import lombok.Getter;

/**
 * 被动技能配置表
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
public class PassiveSkillConfig {

    /** 技能id **/
    private int id;
    /** 技能效果 **/
    private Effect[] effects;

}
