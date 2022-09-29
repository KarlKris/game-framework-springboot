package com.li.battle.core.scene;

import com.li.battle.ai.BehaviourTree;
import com.li.battle.ai.starter.FightUnitAiStarter;
import com.li.battle.buff.BuffManager;
import com.li.battle.collision.*;
import com.li.battle.core.*;
import com.li.battle.core.map.SceneMap;
import com.li.battle.core.task.PlayerOperateTask;
import com.li.battle.core.unit.*;
import com.li.battle.effect.EffectExecutor;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.PassiveEffectSource;
import com.li.battle.event.EventDispatcher;
import com.li.battle.projectile.ProjectileManager;
import com.li.battle.resource.SkillConfig;
import com.li.battle.skill.*;
import com.li.battle.trigger.TriggerManager;
import com.li.common.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
    /** 裁判 **/
    private final BattleSceneReferee referee;
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
    /** 子弹容器 **/
    protected final ProjectileManager projectileManager;
    /** 场景内唯一id生成器 **/
    private long idGenerator = 0;

    /** 定时Future **/
    private final ScheduledFuture<?> future;
    /** 战斗场景销毁状态 **/
    protected volatile boolean destroy;

    /** 全局属性加成 **/
    private final Map<Attribute, Long> attributes;
    /** 场景内战斗单位的分布图(用于优化碰撞检测列表) **/
    private final QuadTree<FightUnit> distributed;

    /** ai **/
    private final Map<Long, BehaviourTree> ai = new LinkedHashMap<>();

    public AbstractBattleScene(long sceneId, SceneMap sceneMap
            , ScheduledExecutorService executorService
            , BattleSceneHelper helper) {
        this.sceneId = sceneId;
        this.sceneMap = sceneMap;
        this.fightUnits = new HashMap<>();
        this.executorService = executorService;
        this.helper = helper;
        this.referee = new BattleSceneReferee(this);
        this.eventDispatcher = new EventDispatcher(this);
        this.buffManager = new BuffManager(this);
        this.skillManager = new SkillManager(this);
        this.triggerManager = new TriggerManager(this);
        this.projectileManager = new ProjectileManager(this);
        this.attributes = new HashMap<>();
        this.distributed = new QuadTree<>(0, new Rectangle2D(0, 0, sceneMap.getHorizontalLength(), sceneMap.getVerticalLength()));

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
    public SceneMap sceneMap() {
        return sceneMap;
    }

    @Override
    public CompletableFuture<Boolean> enterScene(FightUnit unit) {
        return addTask(() -> {
            boolean enter =  fightUnits.putIfAbsent(unit.getId(), unit) == null;
            if (!enter) {
                return false;
            }
            unit.enterScene(this);
            ai.put(unit.getId(), FightUnitAiStarter.unitAi(unit));
            distributed().insert(unit);

            // 释放被动技能
            ConfigHelper configHelper = battleSceneHelper().configHelper();
            EffectExecutor effectExecutor = battleSceneHelper().effectExecutor();
            for (Skill skill : unit.getSkills()) {
                SkillConfig config = configHelper.getSkillConfigById(skill.getSkillId());
                if (SkillType.belongTo(config.getType(), SkillType.PASSIVE_SKILL)) {
                    PassiveEffectSource source = new PassiveEffectSource(unit, skill);
                    for (EffectParam effectParam : config.getInitEffects()) {
                        effectExecutor.execute(source, effectParam);
                    }
                    skill.afterSkillExecuted(config, this);
                }
            }
            return true;
        });
    }

    @Override
    public FightUnit getFightUnit(long unitId) {
        return fightUnits.get(unitId);
    }

    @Override
    public CompletableFuture<Void> leaveScene(long unitId) {
        return addTask(() -> {
            FightUnit unit = fightUnits.remove(unitId);
            if (unit != null) {
                unit.leaveScene();
                buffManager.removeBuff(unitId);
                skillManager.removeBattleSkill(unitId);
                triggerManager.removeTriggerReceiver(unitId);
                eventDispatcher.unregister(unitId);

                distributed.remove(unit);
                ai.remove(unitId);
            }
            return null;
        });
    }

    @Override
    public final void destroy() {
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

            if (log.isDebugEnabled()) {
                log.debug("------------------战斗场景开始第[{}]帧逻辑-------------------------", round);
            }

            // 执行ai逻辑
            executeAi();
            // 执行玩家操作
            executePlayerOperates();
            // 战斗单位移动
            getUnits().forEach(MoveUnit::moving);
            // 执行子弹逻辑
            projectileManager.update();
            // 开始执行buff逻辑
            buffManager.update();
            // 执行事件逻辑
            eventDispatcher.update();
            // 执行技能逻辑
            skillManager.update();
            // 执行触发器销毁逻辑
            triggerManager.update();

            if (checkDestroy()) {
                destroy();
            }

        } catch (Exception e) {
            log.error("执行场景逻辑{}出现未知异常", getClass().getSimpleName(), e);
        }
    }

    /** 执行ai **/
    protected void executeAi() {
        for (BehaviourTree tree : ai.values()) {
            tree.start();
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


    @Override
    public Long getGlobalAttribute(Attribute attribute) {
        return attributes.getOrDefault(attribute, 0L);
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

    @Override
    public SkillManager skillManager() {
        return skillManager;
    }

    @Override
    public ProjectileManager projectileManager() {
        return projectileManager;
    }

    @Override
    public BattleSceneReferee battleSceneReferee() {
        return referee;
    }

    @Override
    public QuadTree<FightUnit> distributed() {
        return distributed;
    }

    @Override
    public long getNextId() {
        return ++idGenerator;
    }

    @Override
    public <R> CompletableFuture<R> addTask(PlayerOperateTask<R> task) {
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
