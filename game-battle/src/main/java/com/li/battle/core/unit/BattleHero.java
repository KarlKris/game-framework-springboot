package com.li.battle.core.unit;

import com.li.battle.core.Attribute;
import com.li.battle.core.CampType;
import com.li.battle.core.Skill;
import com.li.battle.core.UnitType;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;
import java.util.Map;

/**
 * 英雄
 * @author li-yuanwen
 * @date 2022/7/23
 */
public abstract class BattleHero extends AbstractFightUnit {

    public BattleHero(long id, UnitType type, CampType campType, double radius, int maxSpeed
            , Vector2D position, Map<Attribute, Long> baseAttributes, List<Skill> skills) {
        super(id, type, campType, radius, maxSpeed, position, baseAttributes, skills);
    }





}
