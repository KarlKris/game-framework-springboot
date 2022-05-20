﻿package com.li.battle.config;

import com.li.battle.effect.Effect;
import lombok.Getter;

/**
 * 激活类技能配置
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
public class ActivateSkillConfig {

    /** 技能id **/
    private int id;
    /** 激活技能时效果 **/
    private Effect[] activateEffects;
    /** 注销技能时效果 **/
    private Effect[] deactivateEffects;

}
