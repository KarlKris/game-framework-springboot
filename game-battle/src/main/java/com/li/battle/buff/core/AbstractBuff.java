package com.li.battle.buff.core;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.core.context.AbstractContext;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.event.EventPipeline;
import com.li.battle.event.handler.EventHandler;
import com.li.battle.resource.BuffConfig;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * 所有buff的基类，包含各类成员函数和基本接口
 * @author li-yuanwen
 */
@Getter
public abstract class AbstractBuff implements Buff {

    /** buff配置 **/
    protected final BuffConfig config;
    /** buff施加者 **/
    protected final long caster;
    /** 当前挂载的目标 **/
    protected final long parent;
    /** buff由哪个技能创建 **/
    protected final int skillId;

    /** buff层数 **/
    protected int layer;
    /** buff等级 **/
    protected int level;
    /** 下一次触发间隔效果的回合数 **/
    protected long nextRound;
    /** buff失效回合数=创建时回合数 + (buff时长(毫秒) / 回合执行间隔时长(毫秒)) 0表永久 **/
    protected long expireRound;


    public AbstractBuff(BuffConfig config, long caster, long parent, int skillId, AbstractContext context) {
        this.config = config;
        this.caster = caster;
        this.parent = parent;
        this.skillId = skillId;

        this.layer = 1;
        this.level = 1;
        BattleScene scene = context.getScene();
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
            this.expireRound += (duration / getContext().getScene().getRoundPeriod());
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
    public void expire() {
        expireRound = -1;
    }

    @Override
    public boolean isManualExpire() {
        return expireRound < 0;
    }

    /** buff是否失效 **/
    @Override
    public boolean expire(long curRound) {
        return isExpire0(curRound);
    }

    @Override
    public boolean isValid(long curRound) {
        return isExpire0(curRound);
    }

    @Override
    public long getNextRound() {
        return nextRound;
    }

    @Override
    public void updateNextRound(long nextRound) {
        this.nextRound = Math.min(nextRound, expireRound);
    }

    private boolean isExpire0(long curRound) {
        return expireRound != 0 && curRound >= expireRound;
    }

    @Override
    public void registerEventReceiverIfNecessary() {

        List<EventHandler> handlers = new LinkedList<>();
        // 根据BuffConfig添加Handler
        if (ArrayUtil.isNotEmpty(config.getExecutedEffects())) {
            // todo 注册技能执行事件处理器
        }

        if (ArrayUtil.isNotEmpty(config.getBeforeDamageEffects())) {
            // todo 注册伤害前事件处理器
        }

        if (ArrayUtil.isNotEmpty(config.getAfterDamageEffects())) {
            // todo 注册伤害后事件处理器
        }

        if (ArrayUtil.isNotEmpty(config.getBeforeDeadEffects())) {
            // todo 注册死亡前事件处理器
        }

        if (ArrayUtil.isNotEmpty(config.getAfterDeadEffects())) {
            // todo 注册死亡后事件处理器
        }

        if (ArrayUtil.isNotEmpty(config.getAfterKillEffects())) {
            // todo 注册死亡事件处理器
        }

        if (!handlers.isEmpty()) {
            EventPipeline pipeline = eventPipeline();
            handlers.forEach(pipeline::addHandler);

            // todo 注册自身
        }

    }
}
