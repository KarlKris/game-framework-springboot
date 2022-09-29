package com.li.battle.trigger.handler;

import com.li.battle.event.core.*;
import com.li.battle.trigger.Trigger;
import org.springframework.stereotype.Component;

/**
 * 间距触发器处理事件
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class DistanceTriggerHandler extends AbstractTriggerHandler<Trigger, UnitMoveEvent> {

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.MOVE;
    }

    @Override
    protected void doHandle(Trigger receiver, UnitMoveEvent event) {
        // todo 以我方朝向为中心点,间距参数为半径的,角度为X的扇形内的单位做判断
    }
}
