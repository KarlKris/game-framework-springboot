package com.li.battle.trigger.effect;

import com.li.battle.effect.Effect;
import com.li.battle.trigger.core.Trigger;
import com.li.battle.trigger.model.TriggerType;

/**
 * TriggerEffect基类
 * @author li-yuanwen
 * @date 2022/5/18
 */
public abstract class AbstractTriggerEffect implements TriggerEffect {

    /** 来源标识 **/
    private final long source;
    /** 实际触发器 **/
    protected final Trigger trigger;
    /** 触发器触发后执行的效果 **/
    protected final Effect[] effects;

    public AbstractTriggerEffect(long source, Trigger trigger, Effect[] effects) {
        this.source = source;
        this.trigger = trigger;
        this.effects = effects;
    }

    @Override
    public TriggerType getTriggerType() {
        return trigger.getType();
    }
}
