package com.li.battle.effect;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.event.EventPipeline;
import com.li.battle.event.core.BattleEventType;
import com.li.battle.skill.BattleSkill;
import com.li.battle.trigger.Trigger;
import com.li.battle.trigger.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 触发类Effect基类
 * @author li-yuanwen
 * @date 2022/5/24
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TriggerEffect extends EffectAdapter<Buff> implements Trigger {

    /** 触发器类 **/
    private Trigger trigger;
    /** 实际效果 **/
    private Effect[] effects;

    @Override
    public void onAction(FightUnit unit) {
        registerEventReceiverIfNecessary();
    }

    @Override
    public void onAction(BattleSkill skill) {
        registerEventReceiverIfNecessary();
    }

    @Override
    public void onAction(Buff buff) {
        registerEventReceiverIfNecessary();
    }

    @Override
    public TriggerType getType() {
        return trigger.getType();
    }

    @Override
    public boolean tryTrigger() {
        return trigger.tryTrigger();
    }

    @Override
    public boolean isTimeOut() {
        return trigger.isTimeOut();
    }

    @Override
    public Trigger copy() {
        return new TriggerEffect(trigger.copy(), effects);
    }

    @Override
    public void registerEventReceiverIfNecessary() {
        TriggerType triggerType = trigger.getType();
        BattleEventType type = triggerType.getEventType();
        // TODO 根据事件类型,添加责任链

        EventPipeline pipeline = eventPipeline();
    }

    @Override
    public boolean isValid(long curRound) {
        return trigger.isTimeOut();
    }
}
