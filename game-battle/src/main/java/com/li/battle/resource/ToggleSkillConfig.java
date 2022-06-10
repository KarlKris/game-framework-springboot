package com.li.battle.resource;

import com.li.battle.buff.core.Buff;
import com.li.battle.effect.Effect;
import com.li.common.resource.anno.ResourceId;
import com.li.common.resource.anno.ResourceObj;
import lombok.Getter;

/**
 * 开关类技能配置
 * @author li-yuanwen
 * @date 2022/5/17
 */
@Getter
@ResourceObj
public class ToggleSkillConfig {

    /** 技能id **/
    @ResourceId
    private int id;
    /** 开启技能时效果 **/
    private Effect<Buff>[] onEffects;
    /** 关闭技能时效果 **/
    private Effect<Buff>[] offEffects;

}
