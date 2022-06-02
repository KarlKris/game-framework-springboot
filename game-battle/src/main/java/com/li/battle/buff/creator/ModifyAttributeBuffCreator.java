package com.li.battle.buff.creator;

import com.li.battle.buff.BuffType;
import com.li.battle.buff.core.Buff;
import com.li.battle.buff.core.impl.NormalBuffModifier;
import com.li.battle.core.context.DefaultAlterContext;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.BuffConfig;

/**
 * 修改属性的Buff
 * @author li-yuanwen
 * @date 2022/5/25
 */
public class ModifyAttributeBuffCreator implements BuffCreator {

    @Override
    public BuffType getType() {
        return BuffType.MODIFY_ATTRIBUTE;
    }

    @Override
    public Buff newInstance(FightUnit caster, FightUnit target, BuffConfig buffConfig, int skillId) {
        return new NormalBuffModifier(buffConfig, caster == null ? 0L : caster.getId()
                , target.getId(), skillId, new DefaultAlterContext(target.getScene()));
    }
}
