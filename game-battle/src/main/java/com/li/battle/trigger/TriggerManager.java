package com.li.battle.trigger;

import com.li.battle.trigger.effect.TriggerEffect;
import com.li.battle.trigger.model.TriggerType;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 触发器管理
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class TriggerManager {


    /** 场景内所有的触发器效果集 **/
    private final Map<TriggerType, List<TriggerEffect>> triggerEffects = new EnumMap<>(TriggerType.class);


    public void addTriggerEffect(TriggerEffect triggerEffect) {
        List<TriggerEffect> list = this.triggerEffects.computeIfAbsent(triggerEffect.getTriggerType(), k -> new LinkedList<>());
        list.add(triggerEffect);
    }


}
