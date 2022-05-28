package com.li.battle.event.core;

import com.li.battle.buff.core.Buff;

/**
 * Buff在实例化之后，生效之前（还未加入到Buff容器中）事件
 * @author li-yuanwen
 * @date 2022/5/26
 */
public class BeforeBuffAwakeEvent extends AbstractBuffEvent {

    public BeforeBuffAwakeEvent(Buff buff) {
        super(buff);
    }

    @Override
    public BattleEventType getType() {
        return BattleEventType.BEFORE_BUFF_AWAKE;
    }
}
