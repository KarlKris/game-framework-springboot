package com.li.battle.effect.handler;

import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.EffectType;
import com.li.battle.effect.domain.TriggerEffectParam;
import com.li.battle.effect.source.EffectSource;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 创建触发器效果
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Component
public class TriggerEffectHandler extends AbstractEffectParamHandler<TriggerEffectParam> {

    @Override
    public EffectType getType() {
        return EffectType.TRIGGER;
    }

    @Override
    protected void execute0(EffectSource source, TriggerEffectParam effectParam) {
        BattleScene scene = source.battleScene();
        BattleSceneHelper battleSceneHelper = scene.battleSceneHelper();
        TriggerConfig triggerConfig = battleSceneHelper.configHelper().getTriggerConfigById(effectParam.getTriggerId());

        TriggerFactory triggerFactory = battleSceneHelper.triggerFactory();

        FightUnit caster = source.getCaster();
        List<FightUnit> results = source.getTargetUnits();
        if (results.isEmpty()) {
            Trigger trigger = triggerFactory.newInstanceAndRegister(caster.getId(), 0, source.getSkillId()
                    , source.getBuffId(), triggerConfig, scene);
            scene.triggerManager().addTriggerReceiver(trigger);
        } else {
            for (FightUnit unit : results) {
                Trigger trigger = triggerFactory.newInstanceAndRegister(caster.getId(), unit.getId()
                        , source.getSkillId(), source.getBuffId(), triggerConfig, scene);
                scene.triggerManager().addTriggerReceiver(trigger);
            }
        }

    }
}
