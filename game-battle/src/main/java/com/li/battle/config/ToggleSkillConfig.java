package com.li.battle.config;

import com.li.battle.effect.Effect;
import lombok.Getter;

/**
 * 开关类技能配置
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
public class ToggleSkillConfig {

    /** 技能id **/
    private int id;
    /** 开启技能时效果 **/
    private Effect[] onEffects;
    /** 关闭技能时效果 **/
    private Effect[] offEffects;

}
