package com.li.battle.effect;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.skill.BattleSkill;

/**
 * 效果
 * @author li-yuanwen
 * @date 2022/5/17
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ModifyAttributeEffect.class, name = Effect.MODIFY_ATTRIBUTE),
        @JsonSubTypes.Type(value = TriggerEffect.class, name = Effect.TRIGGER_EFFECT),
        @JsonSubTypes.Type(value = AddBuffEffect.class, name = Effect.ADD_BUFF),
})
public interface Effect<B extends Buff> {

    String MODIFY_ATTRIBUTE = "MODIFY_ATTRIBUTE";
    String TRIGGER_EFFECT = "TRIGGER_EFFECT";
    String ADD_BUFF = "ADD_BUFF";

    /**
     * 由战斗单元引起的效果触发,例如被动技能效果执行
     * @param unit 战斗单元
     */
    void onAction(FightUnit unit);

    /**
     * 由技能引起的效果触发
     * @param skill 技能
     */
    void onAction(BattleSkill skill);


    /**
     * 由Buff引起的效果触发
     * @param buff buff
     */
    void onAction(B buff);



}
