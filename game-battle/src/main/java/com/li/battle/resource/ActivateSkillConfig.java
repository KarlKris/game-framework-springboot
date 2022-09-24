package com.li.battle.resource;

import com.li.battle.effect.domain.EffectParam;
import com.li.common.resource.anno.*;
import lombok.Getter;

/**
 * 激活类技能配置
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
@ResourceObj
public class ActivateSkillConfig {

    /** 技能id **/
    @ResourceId
    private int id;
    /** 激活技能时效果 **/
    private EffectParam[] activateEffects;
    /** 注销技能时效果 **/
    private EffectParam[] deactivateEffects;

}
