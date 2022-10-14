package com.li.battle.buff.core;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.buff.*;
import com.li.battle.buff.handler.*;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.effect.EffectExecutor;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.BuffEffectSource;
import com.li.battle.event.*;
import com.li.battle.event.handler.EventHandler;
import com.li.battle.resource.BuffConfig;
import lombok.Getter;

import java.util.*;

/**
 * 所有buff的基类，包含各类成员函数和基本接口
 * @author li-yuanwen
 */
@Getter
public abstract class AbstractBuff implements Buff {

    /** 唯一id **/
    protected final long id;
    /** buff配置 **/
    protected final BuffConfig config;
    /** buff施加者 **/
    protected final long caster;
    /** 当前挂载的目标 **/
    protected final long parent;
    /** buff由哪个技能创建 **/
    protected final int skillId;
    /** 关联的战斗场景 **/
    protected final BattleScene scene;

    /** buff层数 **/
    protected int layer;
    /** buff等级 **/
    protected int level;
    /** 下一次触发间隔效果的回合数 **/
    protected long nextRound;
    /** buff失效回合数=创建时回合数 + (buff时长(毫秒) / 回合执行间隔时长(毫秒)) 0表永久 **/
    protected long expireRound;
    /** 上下文数据 **/
    protected BuffContext buffContext;

    public AbstractBuff(long id, BuffConfig config, long caster, long parent, int skillId, BattleScene scene) {
        this.id = id;
        this.config = config;
        this.caster = caster;
        this.parent = parent;
        this.skillId = skillId;
        this.scene = scene;

        this.layer = 1;
        this.level = 1;
        this.nextRound = scene.getSceneRound() + config.getThinkInterval() / scene.getRoundPeriod();
        int duration = config.getDuration();
        if (duration > 0) {
            this.expireRound = scene.getSceneRound() + duration / scene.getRoundPeriod();
        }

    }

    @Override
    public void onBuffRefresh(Buff other) {
        config.getMergeRule().merge(this, other);
    }

    @Override
    public long getOwner() {
        return caster;
    }

    @Override
    public void increaseLayer(int addLayer) {
        this.layer += addLayer;
    }

    @Override
    public void increaseLevel(int addLevel) {
        this.level += addLevel;
    }

    @Override
    public void increaseDuration(int duration) {
        if (duration <= 0) {
            // 改为永久
            this.expireRound = 0;
        } else if (this.expireRound > 0){
            this.expireRound += (duration / scene.getRoundPeriod());
        }
    }


    @Override
    public int getDuration() {
        return config.getDuration();
    }

    @Override
    public int getBuffId() {
        return config.getId();
    }

    @Override
    public void markExpire() {
        expireRound = -1;
    }

    @Override
    public void expire() {
        markExpire();

        // 移除buff
        BuffManager buffManager = scene.buffManager();
        buffManager.removeBuff(this);

        EffectExecutor effectExecutor = scene.battleSceneHelper().effectExecutor();
        if (ArrayUtil.isNotEmpty(config.getRemoveEffects())) {
            // 执行移除效果
            BuffEffectSource source = new BuffEffectSource(this);
            for (EffectParam effectParam : config.getRemoveEffects()) {
                effectExecutor.execute(source, effectParam);
            }
        }

    }

    @Override
    public boolean isExpire() {
        return isExpire0(scene.getSceneRound());
    }

    @Override
    public long getNextRound() {
        return nextRound;
    }

    @Override
    public void updateNextRound(long nextRound) {
        if (expireRound == 0) {
            this.nextRound = nextRound;
        } else {
            this.nextRound = Math.min(nextRound, expireRound);
        }
    }

    private boolean isExpire0(long curRound) {
        return expireRound != 0 && curRound >= expireRound;
    }

    @Override
    public BattleScene battleScene() {
        return scene;
    }

    @Override
    public BuffContext buffContext() {
        return buffContext;
    }

    @Override
    public void registerEventReceiverIfNecessary() {
        // 根据BuffConfig添加Handler
        List<EventHandler> handlers = new LinkedList<>();

        EventHandlerHolder eventHandlerHolder = scene.battleSceneHelper().eventHandlerHolder();

        if (ArrayUtil.isNotEmpty(config.getExecutedEffects())) {
            //  注册技能执行事件处理器
            handlers.add(eventHandlerHolder.getEventHandler(SkillExecutedBuffEventHandler.class));
        }

        if (ArrayUtil.isNotEmpty(config.getAwakeEffects())) {
            // 注册buff生效前事件处理器
            handlers.add(eventHandlerHolder.getEventHandler(BeforeBuffAwakeEventBuffHandler.class));
        }

        if (ArrayUtil.isNotEmpty(config.getBeforeDamageEffects()) || ArrayUtil.isNotEmpty(config.getBeforeTakeDamageEffects())) {
            // 注册伤害前事件处理器
            handlers.add(eventHandlerHolder.getEventHandler(BeforeDamageEventBuffHandler.class));
        }

        if (ArrayUtil.isNotEmpty(config.getAfterDamageEffects()) || ArrayUtil.isNotEmpty(config.getAfterTakeDamageEffects())) {
            // 注册伤害后事件处理器
            handlers.add(eventHandlerHolder.getEventHandler(AfterDamageEventBuffHandler.class));
        }

        if (ArrayUtil.isNotEmpty(config.getBeforeDeadEffects())) {
            //  注册死亡前事件处理器
            handlers.add(eventHandlerHolder.getEventHandler(BeforeDeadEventBuffHandler.class));
        }

        if (ArrayUtil.isNotEmpty(config.getAfterDeadEffects()) || (ArrayUtil.isNotEmpty(config.getKillEffects()))) {
            // 注册死亡后事件处理器
            handlers.add(eventHandlerHolder.getEventHandler(KillEventBuffHandler.class));
        }

        if (!handlers.isEmpty()) {
            EventPipeline pipeline = newEventPipeline();
            handlers.forEach(pipeline::addHandler);

            //  注册自身
            scene.eventDispatcher().register(pipeline);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBuff that = (AbstractBuff) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
