package com.li.battle.effect;

import com.li.battle.buff.core.BuffModifier;
import com.li.battle.core.Attribute;
import com.li.battle.core.context.AbstractContext;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.IPosition;
import com.li.battle.skill.BattleSkill;
import com.li.battle.util.AttributeValueAlter;
import lombok.Getter;

/**
 * 修改属性效果
 * @author li-yuanwen
 * @date 2022/5/23
 */
@Getter
public class ModifyAttributeEffect extends EffectAdapter<BuffModifier> {

    /** 属性值 **/
    private Attribute attribute;
    /** 变更值 **/
    private long value;

    @Override
    public void onAction(FightUnit unit) {
        unit.modifyAttribute(attribute
                , unit.getAttributeValue(attribute) + value);
    }

    @Override
    public void onAction(BattleSkill skill) {
        for (IPosition unit : skill.getTarget().getResults()) {
            if (unit instanceof FightUnit) {
                FightUnit u = (FightUnit) unit;
                u.modifyAttribute(attribute
                        , u.getAttributeValue(attribute) + value);
            }

        }
    }

    @Override
    public void onAction(BuffModifier buff) {
        AbstractContext context = buff.getContext();
        BattleScene scene = context.getScene();
        FightUnit unit = scene.getFightUnit(buff.getParent());

        buff.modifyAttribute(unit, new AttributeValueAlter(attribute, value));
    }

}
