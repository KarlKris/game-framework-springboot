package com.li.battle.core.unit;

import com.li.battle.core.unit.model.Attribute;
import com.li.battle.core.unit.model.BattleSkill;
import com.li.battle.core.unit.model.UnitState;
import com.li.battle.core.unit.model.FightUnitType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 战斗单元
 * @author li-yuanwen
 * @date 2021/10/19
 */
public class FightUnitImpl implements FightUnit {

    /** 战斗单元唯一标识 **/
    private final long id;
    /** 战斗单元类型 **/
    private final FightUnitType type;
    /** 战斗单元状态 **/
    private UnitState state;
    /** 战斗单元属性 **/
    private final Map<Attribute, Long> attributes;
    /** 战斗技能 **/
    private final List<BattleSkill> skills;

    public FightUnitImpl(long id, FightUnitType type, Map<Attribute, Long> attributes, List<BattleSkill> skills) {
        this.id = id;
        this.type = type;
        this.state = UnitState.NORMAL;
        this.attributes = new EnumMap<Attribute, Long>(attributes);
        this.skills = skills;
    }


    @Override
    public long getId() {
        return id;
    }

    @Override
    public FightUnitType getUnitType() {
        return type;
    }

    @Override
    public long getAttributeValue(Attribute attribute) {
        return attributes.getOrDefault(attribute, 0L);
    }

    @Override
    public void modifyAttribute(Attribute attribute, long value) {
        attributes.put(attribute, value);
    }

    @Override
    public UnitState getState() {
        return state;
    }

    @Override
    public void modifyState(UnitState state) {
        this.state = state;
    }
}
