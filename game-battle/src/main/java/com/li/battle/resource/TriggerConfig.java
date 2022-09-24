package com.li.battle.resource;

import com.li.battle.effect.domain.EffectParam;
import com.li.battle.trigger.core.Trigger;
import com.li.common.resource.anno.*;
import lombok.Getter;

/**
 * 触发器配置
 * @author li-yuanwen
 * @date 2022/5/26
 */
@Getter
@ResourceObj
public class TriggerConfig {

    /** 触发器标识 **/
    @ResourceId
    private int id;
    /** 触发器构建参数 **/
    private Trigger trigger;
    /** 触发器时长(毫秒 0表永久) **/
    private int duration;
    /** 触发器CD,即触发一次后等待x毫秒才能再次触发 **/
    private int coolDown;
    /** 触发器触发后执行的效果 **/
    private EffectParam[] triggerEffects;
    /** 触发器未触发后过时时执行的效果 **/
    private EffectParam[] destroyEffects;



}
