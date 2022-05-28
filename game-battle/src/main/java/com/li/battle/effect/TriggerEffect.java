package com.li.battle.effect;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.IPosition;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.skill.BattleSkill;
import com.li.battle.trigger.TriggerReceiver;
import com.li.battle.trigger.TriggerReceiverFactory;
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
public class TriggerEffect extends EffectAdapter<Buff> {

    /** 触发器标识 **/
    private int triggerId;

    @Override
    public void onAction(FightUnit unit) {
        registerEventReceiverIfNecessary(unit.getScene(), unit.getId(), unit.getId(), 0, 0);
    }

    @Override
    public void onAction(BattleSkill skill) {
        for (IPosition position : skill.getTarget().getResults()) {
            if (!(position instanceof FightUnit)) {
                continue;
            }

            registerEventReceiverIfNecessary(skill.getContext().getScene(), skill.getCaster()
                    , ((FightUnit) position).getId(), skill.getSkillId(), 0);
        }

    }

    @Override
    public void onAction(Buff buff) {
        registerEventReceiverIfNecessary(buff.getContext().getScene(), buff.getCaster(), buff.getParent(), buff.getSkillId(), buff.getSkillId());
    }


    private void registerEventReceiverIfNecessary(BattleScene scene, long unitId, long target, int skillId, int buffId) {
        BattleSceneHelper battleSceneHelper = scene.battleSceneHelper();
        TriggerConfig triggerConfig = battleSceneHelper.configHelper().getTriggerConfigById(triggerId);

        TriggerReceiver triggerReceiver = TriggerReceiverFactory.newInstanceAndRegister(unitId, target, skillId, buffId, triggerConfig, scene);
        scene.triggerManager().addTriggerReceiver(triggerReceiver);
    }

}
