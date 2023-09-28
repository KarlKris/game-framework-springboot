package com.li.battle.core.scene;

import com.li.battle.core.*;
import com.li.battle.core.map.SceneMap;
import com.li.battle.core.unit.FightUnit;
import com.li.common.concurrent.RunnableLoop;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 多人对战PVP场景
 * @author li-yuanwen
 * @date 2021/10/16
 */
public class MultipleFightBattleScene extends AbstractBattleScene {


    public MultipleFightBattleScene(long sceneId, SceneMap sceneMap
            , RunnableLoop runnableLoop
            , BattleSceneHelper helper) {
        super(sceneId, sceneMap, runnableLoop, helper);
    }

    @Override
    public int getRoundPeriod() {
        return 50;
    }

    @Override
    public boolean checkDestroy() {
        // 场景内无战斗单元,可销毁
        if (destroy) {
            return true;
        }

        List<FightUnit> units = fightUnits.values().stream().filter(unit -> !unit.isDead()).collect(Collectors.toList());
        if (units.isEmpty()) {
            return true;
        }

        CampType type = null;
        for (FightUnit unit : units) {
            if (type == null) {
                type = unit.getCampType();
            } else if (type != unit.getCampType()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Collection<FightUnit> getUnits() {
        return fightUnits.values().stream().filter(unit -> !unit.isDead()).collect(Collectors.toList());
    }
}
