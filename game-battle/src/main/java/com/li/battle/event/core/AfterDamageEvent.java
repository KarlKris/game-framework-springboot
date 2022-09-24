package com.li.battle.event.core;

import com.li.battle.effect.source.EffectSource;
import lombok.Getter;

/**
 * 造成伤害后事件
 * @author li-yuanwen
 * @date 2022/9/24
 */
@Getter
public class AfterDamageEvent implements BattleEvent {

    /** 伤害来源 **/
    private final EffectSource source;
    /** 伤害制造者唯一标识 **/
    private final long maker;
    /** 伤害目标唯一标识 **/
    private final long target;

    public AfterDamageEvent(EffectSource source, long maker, long target) {
        this.source = source;
        this.maker = maker;
        this.target = target;
    }

    @Override
    public BattleEventType getType() {
        return BattleEventType.AFTER_DAMAGE;
    }

    @Override
    public long getSource() {
        return maker;
    }
}
