package com.li.battle.harm;

import com.li.battle.core.Attribute;
import com.li.battle.core.unit.FightUnit;

/**
 * 伤害公式计算上下文
 * @author li-yuanwen
 * @date 2022/9/24
 */
public class HarmContext {

    private final long dmg;
    private final FightUnit attacker;
    private final FightUnit defender;

    public HarmContext(long dmg, FightUnit attacker, FightUnit defender) {
        this.dmg = dmg;
        this.attacker = attacker;
        this.defender = defender;
    }

    public long getAttackerAttr(String attr) {
        return attacker.getAttributeValue(Attribute.from(attr));
    }

    public long getDefenderAttr(String attr) {
        return defender.getAttributeValue(Attribute.from(attr));
    }

    public long getDmg() {
        return dmg;
    }

}
