package com.li.battle.trigger;

import com.li.battle.event.core.BattleEventType;
import lombok.Getter;

/**
 * 触发器类型
 * @author li-yuanwen
 * @date 2022/5/20
 */
@Getter
public enum TriggerType {

    ;

    private BattleEventType eventType;

    TriggerType(BattleEventType eventType) {
        this.eventType = eventType;
    }
}
