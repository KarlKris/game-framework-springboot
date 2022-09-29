package com.li.battle.trigger.handler;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.core.unit.*;
import com.li.battle.event.core.*;
import com.li.battle.trigger.core.TargetDetonateTrigger;
import com.li.battle.trigger.domain.DetonateTriggerParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 技能目标引爆类触发器监听技能执行事件
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Slf4j
@Component
public class TargetDetonateTriggerHandler extends AbstractTriggerHandler<TargetDetonateTrigger, BeforeDamageEvent> {

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.BEFORE_DAMAGE;
    }

    @Override
    protected void doHandle(TargetDetonateTrigger receiver, BeforeDamageEvent event) {
        DetonateTriggerParam param = receiver.getConfig().getParam();
        int skillId = event.getEffectSource().getSkillId();
        if (!ArrayUtil.contains(param.getSkillIds(), skillId)) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("单位[{}]触发器[{}]执行逻辑", receiver.getOwner(), this.getClass().getSimpleName());
        }

        FightUnit unit = receiver.getScene().getFightUnit(event.getTarget());
        if (receiver.increment(unit.getId()) >= param.getNum()) {
            receiver.reset();
            executeEffect(receiver, unit);
        }
    }
}
