package com.li.battle.trigger.effect;

import com.li.battle.trigger.model.TriggerType;

/**
 * 触发器和效果封装整合
 * @author li-yuanwen
 * @date 2022/5/18
 */
public interface TriggerEffect {


    /**
     * 获取触发器类型
     * @return 触发器类型
     */
    TriggerType getTriggerType();


}
