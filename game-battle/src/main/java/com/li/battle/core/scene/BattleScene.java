package com.li.battle.core.scene;

import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.unit.FightUnit;

import java.util.Collection;

/**
 * 战斗场景对外操作接口
 * @author li-yuanwen
 * @date 2021/10/16
 */
public interface BattleScene {

    /**
     * 获取场景唯一id
     * @return 场景唯一id
     */
    long getSceneId();

    /**
     * 获取回合间隔时长(毫秒)
     * @return 回合间隔时长(毫秒)
     */
    int getRoundPeriod();

    /**
     * 获取场景当前回合数
     * @return 场景当前回合数
     */
    long getSceneRound();

    /**
     * 进入场景
     * @param unit 战斗单元
     * @return true 进入场景成功
     */
    boolean enterScene(FightUnit unit);

    /**
     * 获取指定的战斗单元
     * @param unitId 战斗单元标识
     * @return 战斗单元
     */
    FightUnit getFightUnit(long unitId);

    /**
     * 获取场景内所有战斗单元
     * @return 场景内所有战斗单元
     */
    Collection<FightUnit> getUnits();

    /**
     * 离开场景
     * @param unitId 战斗单元唯一标识
     */
    void leaveScene(long unitId);

    /**
     * 检查是否销毁场景,即场景将不再使用
     * @return true 需要销毁场景
     */
    boolean checkDestroy();

    /**
     * 开始运行场景逻辑
     */
    void start();

    /**
     * 获取配置获取实例
     * @return 配置获取实例
     */
    BattleSceneHelper getBattleSceneHelper();

}
