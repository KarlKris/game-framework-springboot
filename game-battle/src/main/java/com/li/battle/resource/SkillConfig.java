package com.li.battle.resource;

import com.li.battle.buff.core.Buff;
import com.li.battle.effect.Effect;
import lombok.Getter;

/**
 * 技能基础表
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
public class SkillConfig {

    /** 技能唯一标识 **/
    private int id;
    /** 技能类型 **/
    private byte type;
    /** 技能CD(毫秒) **/
    private int coolDown;
    /** 技能初始化效果(技能类型包含被动技能时会在角色进场景时执行效果) **/
    private Effect<Buff>[] initEffects;


}
