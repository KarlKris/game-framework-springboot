package com.li.battle.event.core;

import com.li.battle.effect.source.EffectSource;
import lombok.Getter;

/**
 * 死亡前事件
 * @author li-yuanwen
 * @date 2022/9/24
 */
@Getter
public class BeforeDeadEvent implements BattleEvent {

    /** 伤害来源 **/
    private final EffectSource effectSource;
    /** 凶手唯一标识 **/
    private final long killer;
    /** 受害者唯一标识 **/
    private final long target;

    public BeforeDeadEvent(EffectSource source, long killer, long target) {
        this.effectSource = source;
        this.killer = killer;
        this.target = target;
    }

    @Override
    public BattleEventType getType() {
        return BattleEventType.BEFORE_DEAD;
    }

    @Override
    public long getSource() {
        return killer;
    }
}
