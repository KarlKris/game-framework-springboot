package com.li.battle.event.core;

import com.li.battle.buff.core.Buff;
import lombok.Getter;

/**
 * 抽象Buff相关类事件
 * @author li-yuanwen
 * @date 2022/5/26
 */
@Getter
public abstract class AbstractBuffEvent implements BattleEvent {

    protected final Buff buff;

    public AbstractBuffEvent(Buff buff) {
        this.buff = buff;
    }


    @Override
    public long getSource() {
        return buff.getCaster();
    }
}
