package com.li.battle.trigger;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.event.*;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.handler.AbstractTriggerHandler;
import lombok.Getter;

/**
 * 触发器
 * @author li-yuanwen
 * @date 2022/5/26
 */
@Getter
public class Trigger implements EventReceiver {

    /** 生成触发器的单位标识 **/
    private final long unitId;
    /** 关联的技能id **/
    private final int skillId;
    /** 关联的BuffId **/
    private final int buffId;
    /** 挂载的trigger单位目标 **/
    private final long parent;
    /** 触发器配置 **/
    private final TriggerConfig config;
    /** 可以触发的回合数(在这之后可触发) **/
    private long nextTriggerRound;
    /** 失效回合数 **/
    private long expireRound;
    /** 关联的战斗场景 **/
    private final BattleScene scene;

    public Trigger(long unitId, long parent, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        this.unitId = unitId;
        this.parent = parent;
        this.skillId = skillId;
        this.buffId = buffId;
        this.config = config;
        this.scene = scene;

        this.nextTriggerRound = scene.getSceneRound();
        if (config.getDuration() > 0) {
            this.expireRound = nextTriggerRound + config.getDuration() / scene.getRoundPeriod();
        }

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
        return expireRound != 0 && curRound >= expireRound;
    }

    @Override
    public long getOwner() {
        return unitId;
    }

    public boolean isManualInvalid() {
        return expireRound < 0;
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
        TriggerType triggerType = config.getParam().getType();
        Class<? extends AbstractTriggerHandler<?, ?>>[] handlersClz = triggerType.getHandlersClz();
        if (ArrayUtil.isNotEmpty(handlersClz)) {
            EventHandlerHolder eventHandlerHolder = scene.battleSceneHelper().eventHandlerHolder();
            EventPipeline eventPipeline = newEventPipeline();
            for (Class<? extends AbstractTriggerHandler<?, ?>> clz : handlersClz) {
                eventPipeline.addHandler(eventHandlerHolder.getEventHandler(clz));
            }

            // 注册
            scene.eventDispatcher().register(eventPipeline);
        }
    }

    public BattleScene battleScene() {
        return scene;
    }
}
