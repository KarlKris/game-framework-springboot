package com.li.battle.core.scene;

import com.li.battle.buff.BuffManager;
import com.li.battle.collision.QuadTree;
import com.li.battle.core.Attribute;
import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.scene.map.SceneMap;
import com.li.battle.core.task.PlayerOperateTask;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.event.EventDispatcher;
import com.li.battle.projectile.ProjectileManager;
import com.li.battle.skill.SkillManager;
import com.li.battle.trigger.TriggerManager;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

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
     * 获取场景地图
     * @return 场景地图
     */
    SceneMap sceneMap();

    /**
     * 向场景内的队列提交任务(任何修改操作都应该通过该方法实现,保证无并发问题)
     * @param task 任务
     * @param <R> 任务结果类型
     * @return 任务结果future
     */
    <R> CompletableFuture<R> addTask(PlayerOperateTask<R> task);

    /**
     * 进入场景
     * @param unit 战斗单元
     * @return 进入场景结果future
     */
    CompletableFuture<Boolean> enterScene(FightUnit unit);

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
     * @return 离开场景future
     */
    CompletableFuture<Void> leaveScene(long unitId);

    /**
     * 检查是否销毁场景,即场景将不再使用
     * @return true 需要销毁场景
     */
    boolean checkDestroy();

    /**
     * 场景销毁
     */
    void destroy();

    /**
     * 开始运行场景逻辑
     */
    void start();

    /**
     * 战斗场景内的裁判
     * @return 裁判
     */
    BattleSceneReferee battleSceneReferee();

    /**
     * 获取配置获取实例
     * @return 配置获取实例
     */
    BattleSceneHelper battleSceneHelper();

    /**
     * 获取事件分发器
     * @return 事件分发器
     */
    EventDispatcher eventDispatcher();

    /**
     * 获取buff容器
     * @return buff容器
     */
    BuffManager buffManager();

    /**
     * 获取触发器容器
     * @return 触发器容器
     */
    TriggerManager triggerManager();

    /**
     * 获取技能容器
     * @return 技能容器
     */
    SkillManager skillManager();

    /**
     * 获取子弹容器
     * @return 子弹容器
     */
    ProjectileManager projectileManager();


    /**
     * 获取场景内全局加成的属性值
     * @param attribute 属性
     * @return 属性值
     */
    Long getGlobalAttribute(Attribute attribute);


    /**
     * 获取场景内战斗单位的分布信息
     * @return 场景内战斗单位的分布信息
     */
    QuadTree<FightUnit> distributed();


}
