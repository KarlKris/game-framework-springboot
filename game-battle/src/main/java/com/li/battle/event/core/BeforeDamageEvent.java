package com.li.battle.event.core;

import com.li.battle.util.AttributeValueAlter;
import lombok.Getter;

/**
 * 造成伤害前事件
 * @author li-yuanwen
 * @date 2022/5/23
 */
@Getter
public class BeforeDamageEvent implements BattleEvent {

    /** 伤害量 **/
    private final AttributeValueAlter damage;
    /** 伤害制造者唯一标识 **/
    private final long maker;
    /** 伤害目标唯一标识 **/
    private final long target;

    public BeforeDamageEvent(AttributeValueAlter damage, long maker, long target) {
        this.damage = damage;
        this.maker = maker;
        this.target = target;
    }

    @Override
    public BattleEventType getType() {
        return BattleEventType.BEFORE_DAMAGE;
    }

    @Override
    public long getSource() {
        return maker;
    }
}
