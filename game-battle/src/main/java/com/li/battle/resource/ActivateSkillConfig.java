package com.li.battle.resource;

import com.li.battle.buff.core.Buff;
import com.li.battle.effect.Effect;
import com.li.common.resource.anno.ResourceId;
import com.li.common.resource.anno.ResourceObj;
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
    private Effect<Buff>[] activateEffects;
    /** 注销技能时效果 **/
    private Effect<Buff>[] deactivateEffects;

}
