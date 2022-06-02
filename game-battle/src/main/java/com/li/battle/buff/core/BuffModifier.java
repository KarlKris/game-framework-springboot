package com.li.battle.buff.core;

import com.li.battle.core.UnitState;
import com.li.battle.core.context.AbstractDamageAlterContext;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.Unit;
import com.li.battle.resource.BuffConfig;
import com.li.battle.util.AttributeValueAlter;

/**
 * 继承于AbstractBuff,代表这个Buff是一个修改器,可以用来修改当前目标的各种属性，状态等等
 * @author li-yuanwen
 */
public abstract class BuffModifier extends AbstractBuff {


    public BuffModifier(BuffConfig config, long caster, long parent
            , int skillId, AbstractDamageAlterContext context) {
        super(config, caster, parent, skillId, context);
    }

    /**
     * 修改状态
     * @param unit 单位
     * @param state 修改后的状态
     */
    public void modifyState(Unit unit, UnitState state) {
        unit.modifyState(state);
    }

    /**
     * 修改属性
     * @param unit 战斗单位
     * @param alter 属性变更值
     */
    public void modifyAttribute(FightUnit unit, AttributeValueAlter alter) {
        unit.modifyAttribute(alter.getAttribute(), alter.getValue());
    }

    @Override
    public abstract AbstractDamageAlterContext getContext();
}
