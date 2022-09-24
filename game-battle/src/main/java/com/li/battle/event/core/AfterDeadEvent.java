package com.li.battle.event.core;

import com.li.battle.effect.source.EffectSource;
import lombok.Getter;

/**
 * 死亡后事件
 * @author li-yuanwen
 * @date 2022/9/24
 */
@Getter
public class AfterDeadEvent implements BattleEvent {

    /** 伤害来源 **/
    private final EffectSource effectSource;
    /** 凶手唯一标识 **/
    private final long killer;
    /** 受害者唯一标识 **/
    private final long target;

    public AfterDeadEvent(EffectSource effectSource, long killer, long target) {
        this.effectSource = effectSource;
        this.killer = killer;
        this.target = target;
    }

    @Override
    public BattleEventType getType() {
        return BattleEventType.AFTER_DEAD;
    }

    @Override
    public long getSource() {
        return killer;
    }
}
