package com.li.battle.config;

import com.li.battle.effect.Effect;
import com.li.battle.trigger.core.Trigger;
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
    /** 被动技能触发配置 **/
    private Trigger[] trigger;
    /** 技能效果 **/
    private Effect[] effects;

}
