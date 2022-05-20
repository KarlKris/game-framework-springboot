package com.li.battle.core.scene.impl;

import com.li.battle.core.scene.AbstractBattleScene;
import com.li.battle.core.scene.map.SceneMap;
import com.li.battle.core.unit.Unit;
import com.li.battle.skill.executor.BattleSkillExecutor;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 多人对战PVP场景
 * @author li-yuanwen
 * @date 2021/10/16
 */
public class MultipleFightBattleScene extends AbstractBattleScene {


    public MultipleFightBattleScene(long sceneId, SceneMap sceneMap
            , ScheduledExecutorService executorService
            , BattleSkillExecutor battleSkillExecutor) {
        super(sceneId, sceneMap, executorService, battleSkillExecutor);
    }

    @Override
    public int getRoundPeriod() {
        return 50;
    }

    @Override
    public boolean checkDestroy() {
        // 场景内无战斗单元,可销毁
        return this.fightUnits.isEmpty();
    }

    @Override
    public Collection<Unit> getUnits() {
        return null;
    }
}
