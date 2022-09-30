package com.li.battle.core.scene;

import com.li.battle.core.*;
import com.li.battle.core.map.SceneMap;
import com.li.battle.core.unit.FightUnit;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * 多人对战PVP场景
 * @author li-yuanwen
 * @date 2021/10/16
 */
public class MultipleFightBattleScene extends AbstractBattleScene {


    public MultipleFightBattleScene(long sceneId, SceneMap sceneMap
            , ScheduledExecutorService executorService
            , BattleSceneHelper helper) {
        super(sceneId, sceneMap, executorService, helper);
    }

    @Override
    public int getRoundPeriod() {
        return 50;
    }

    @Override
    public boolean checkDestroy() {
        // 场景内无战斗单元,可销毁
//        return this.fightUnits.isEmpty() || destroy;
        if (destroy) {
            return true;
        }

        List<FightUnit> units = fightUnits.values().stream().filter(unit -> !unit.isDead()).collect(Collectors.toList());
        if (units.isEmpty()) {
            return true;
        }

        CampType type = null;
        boolean sameCamp = true;
        for (FightUnit unit : units) {
            if (type == null) {
                type = unit.getCampType();
            } else if (type != unit.getCampType()) {
                sameCamp = false;
            }
        }
        return sameCamp;
    }

    @Override
    public Collection<FightUnit> getUnits() {
        return fightUnits.values().stream().filter(unit -> !unit.isDead()).collect(Collectors.toList());
    }
}
