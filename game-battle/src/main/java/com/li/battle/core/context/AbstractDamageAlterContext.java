package com.li.battle.core.context;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.util.AttributeValueAlter;
import com.li.battle.util.ValueAlter;
import lombok.Getter;

/**
 * 携带属性变更值和伤害的战斗上下文
 * @author li-yuanwen
 * @date 2022/5/24
 */
@Getter
public abstract class AbstractDamageAlterContext extends AbstractContext {

    /** 属性变更值 **/
    private AttributeValueAlter attributeValue;
    /** 伤害值 **/
    private ValueAlter damageValue;

    public AbstractDamageAlterContext(BattleScene scene) {
        super(scene);
    }

    public void initDamageValue(ValueAlter damageValue) {
        this.damageValue = damageValue;
    }

    public void initAttributeValue(AttributeValueAlter attributeValue) {
        this.attributeValue = attributeValue;
    }
}
