package com.li.battle.buff.handler;

import cn.hutool.core.util.ArrayUtil;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.unit.*;
import com.li.battle.effect.*;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.BuffEffectSource;
import com.li.battle.event.EventHandlerContext;
import com.li.battle.event.core.*;
import com.li.battle.resource.BuffConfig;
import org.springframework.stereotype.Component;

/**
 * 监听某主动技能执行事件的buff处理器
 * @author li-yuanwen
 * @date 2022/5/26
 */
@Component
public class SkillExecutedBuffEventHandler extends AbstractBuffHandler<Buff, SkillExecutedEvent> {

    @Override
    protected void handle0(EventHandlerContext context, Buff receiver, SkillExecutedEvent event) {
        BuffConfig config = receiver.getConfig();
        if (!ArrayUtil.contains(config.getSkillIds(), event.getSkill().getSkillId())) {
            return;
        }
        // buff挂载人
        boolean sameParent = false;
        for (IPosition position : event.getSkill().getFinalTargets()) {
            if (!(position instanceof FightUnit)) {
                continue;
            }
            FightUnit unit = (FightUnit) position;
            if (unit.getId() == receiver.getParent()) {
                sameParent = true;
                break;
            }
        }
        if (!sameParent) {
            return;
        }
        // 判断技能的施法人
        if (config.getMonitorSkillCasterType().isMatchCasterCondition(receiver, event.getSkill())) {
            return;
        }
        // 执行效果
        EffectExecutor effectExecutor = receiver.battleScene().battleSceneHelper().effectExecutor();
        BuffEffectSource source = new BuffEffectSource(receiver);
        for (EffectParam effectParam : config.getExecutedEffects()) {
            effectExecutor.execute(source, effectParam);
        }

    }

    @Override
    public BattleEventType getEventType() {
        return BattleEventType.SKILL_EXECUTED;
    }
}
