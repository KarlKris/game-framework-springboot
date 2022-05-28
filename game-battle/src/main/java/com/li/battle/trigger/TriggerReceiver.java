package com.li.battle.trigger;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.event.EventHandlerHolder;
import com.li.battle.event.EventPipeline;
import com.li.battle.event.EventReceiver;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.core.Trigger;
import com.li.battle.trigger.handler.AbstractTriggerHandler;
import lombok.Getter;

/**
 * 触发器事件接收者
 * @author li-yuanwen
 * @date 2022/5/26
 */
@Getter
public class TriggerReceiver implements EventReceiver {

    /** 生成触发器的单位标识 **/
    private final long unitId;
    /** 关联的技能id **/
    private final int skillId;
    /** 关联的BuffId **/
    private final int buffId;
    /** 关联的trigger单位目标，若==unitId，则随意单位触发,否则只能指定单位触发 **/
    private final long target;
    /** 触发器配置 **/
    private final TriggerConfig config;
    /** 触发器 **/
    private final Trigger trigger;
    /** 可以触发的回合数(在这之后可触发) **/
    private long nextTriggerRound;
    /** 失效回合数 **/
    private long expireRound;
    /** 关联的战斗场景 **/
    private final BattleScene scene;

    public TriggerReceiver(long unitId, long target, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        this.unitId = unitId;
        this.target = target;
        this.skillId = skillId;
        this.buffId = buffId;
        this.config = config;
        this.scene = scene;

        this.trigger = config.getTrigger().copy();
        this.nextTriggerRound = scene.getSceneRound();
        this.expireRound = nextTriggerRound + config.getDuration() / scene.getRoundPeriod();
    }

    public void afterExecuteEffect() {
        this.nextTriggerRound = scene.getSceneRound()
                + config.getCoolDown() / scene.getRoundPeriod();
    }

    @Override
    public void makeExpire() {
        expireRound = -1;
    }

    @Override
    public boolean isInvalid(long curRound) {
        return curRound >= expireRound;
    }

    @Override
    public long getOwner() {
        return unitId;
    }

    public boolean isManualInvalid() {
        return expireRound < 0;
    }

    public <T extends Trigger> T getTrigger() {
        return (T) trigger;
    }

    public TriggerConfig getConfig() {
        return config;
    }

    public boolean isInCoolDown() {
        return scene.getSceneRound() < nextTriggerRound;
    }

    public long getExpireRound() {
        return expireRound;
    }

    @Override
    public void registerEventReceiverIfNecessary() {
        TriggerType triggerType = trigger.getType();
        Class<? extends AbstractTriggerHandler<?>>[] handlersClz = triggerType.getHandlersClz();
        if (ArrayUtil.isNotEmpty(handlersClz)) {
            EventHandlerHolder eventHandlerHolder = scene.battleSceneHelper().eventHandlerHolder();
            EventPipeline eventPipeline = eventPipeline();
            for (Class<? extends AbstractTriggerHandler<?>> clz : handlersClz) {
                eventPipeline.addHandler(eventHandlerHolder.getEventHandler(clz));
            }

            // 注册
            scene.eventDispatcher().register(this);
        }

    }
}
