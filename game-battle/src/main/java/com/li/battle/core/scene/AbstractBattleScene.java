package com.li.battle.core.scene;

import com.li.battle.buff.BuffManager;
import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.scene.map.SceneMap;
import com.li.battle.core.task.PlayerOperateTask;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.event.EventDispatcher;
import com.li.battle.skill.SkillManager;
import com.li.battle.trigger.TriggerManager;
import com.li.common.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * 抽象战斗场景基类
 * @author li-yuanwen
 */
@Slf4j
public abstract class AbstractBattleScene implements BattleScene {

    /** 场景唯一id **/
    protected final long sceneId;
    /** 场景地图 **/
    protected final SceneMap sceneMap;
    /** 战斗单元容器 **/
    protected final Map<Long, FightUnit> fightUnits;
    /** 玩家操作队列 **/
    protected final Queue<BattleTask<?>> queue = new ConcurrentLinkedQueue<>();
    /** 场景当前回合数 **/
    private long round;
    /** 战斗组件容器 **/
    private final BattleSceneHelper helper;
    /** 单线程池(定时执行战斗逻辑) **/
    protected final ScheduledExecutorService executorService;
    /** 事件分发器 **/
    protected final EventDispatcher eventDispatcher;
    /** Buff容器 **/
    protected final BuffManager buffManager;
    /** 技能容器 **/
    protected final SkillManager skillManager;
    /** 触发器容器 **/
    protected final TriggerManager triggerManager;

    /** 定时Future **/
    private final ScheduledFuture<?> future;
    /** 战斗场景销毁状态 **/
    private volatile boolean destroy;

    public AbstractBattleScene(long sceneId, SceneMap sceneMap
            , ScheduledExecutorService executorService
            , BattleSceneHelper helper) {
        this.sceneId = sceneId;
        this.sceneMap = sceneMap;
        this.fightUnits = new HashMap<>();
        this.executorService = executorService;
        this.helper = helper;
        this.eventDispatcher = new EventDispatcher(this);
        this.buffManager = new BuffManager(this);
        this.skillManager = new SkillManager(this);
        this.triggerManager = new TriggerManager(this);
        this.future = this.executorService.scheduleAtFixedRate(this::start, getRoundPeriod(), getRoundPeriod(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long getSceneId() {
        return this.sceneId;
    }

    @Override
    public long getSceneRound() {
        return round;
    }

    @Override
    public CompletableFuture<Boolean> enterScene(FightUnit unit) {
        return addTask(() -> fightUnits.putIfAbsent(unit.getId(), unit) == null);
    }

    @Override
    public FightUnit getFightUnit(long unitId) {
        return fightUnits.get(unitId);
    }

    @Override
    public CompletableFuture<Void> leaveScene(long unitId) {
        return addTask(() -> {
            buffManager.removeBuff(unitId);
            skillManager.removeBattleSkill(unitId);
            triggerManager.removeTriggerReceiver(unitId);
            eventDispatcher.unregister(unitId);
            fightUnits.remove(unitId);
            return null;
        });
    }

    @Override
    public void destroy() {
        destroy = true;
        if (future == null || future.isCancelled()) {
            return;
        }
        future.cancel(false);
    }

    @Override
    public final void start() {
        try {
            // 更新回合数
            increaseRound();
            // 执行玩家操作
            executePlayerOperates();
            // 执行事件逻辑
            eventDispatcher.update();
            // 执行触发器销毁逻辑
            triggerManager.update();
            // 开始执行buff逻辑
            buffManager.update();
            // 执行技能逻辑
            skillManager.update();
            // todo 执行战斗单元AI

        } catch (Exception e) {
            log.error("执行场景逻辑{}出现未知异常", getClass().getSimpleName(), e);
        }
    }


    /** 执行玩家操作 **/
    protected void executePlayerOperates() {
        int size = this.queue.size();
        for (int i = 0; i < size; i++) {
            BattleTask<?> task = this.queue.poll();
            if (task != null) {
                task.run();
            }
        }
    }


    /** 更新回合数 **/
    protected void increaseRound() {
        this.round++;
    }


    @Override
    public BattleSceneHelper battleSceneHelper() {
        return helper;
    }

    @Override
    public EventDispatcher eventDispatcher() {
        return eventDispatcher;
    }

    @Override
    public BuffManager buffManager() {
        return buffManager;
    }

    @Override
    public TriggerManager triggerManager() {
        return triggerManager;
    }

    private <R> CompletableFuture<R> addTask(PlayerOperateTask<R> task) {
        if (destroy) {
            // todo "场景已销毁"
            throw new BadRequestException(-1);
        }
        CompletableFuture<R> future = new CompletableFuture<>();
        BattleTask<R> battleTask = new BattleTask<>(future, task);
        queue.offer(battleTask);
        return future;
    }

    private static final class BattleTask<R> implements Runnable {

        private final CompletableFuture<R> future;
        private final PlayerOperateTask<R> task;

        public BattleTask(CompletableFuture<R> future, PlayerOperateTask<R> task) {
            this.future = future;
            this.task = task;
        }

        @Override
        public void run() {
            try {
                R r = task.run();
                future.complete(r);
            } catch (Exception e) {
                log.error("执行玩家操作[{}]出现未知异常", task, e);
                future.completeExceptionally(e);
            }
        }
    }

}
