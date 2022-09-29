package com.li.battle.trigger.creator;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.*;
import com.li.battle.trigger.core.FixCasterDetonateTrigger;
import org.springframework.stereotype.Component;

/**
 * 技能施法方固定引爆类触发器
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class FixCasterDetonateTriggerCreator implements TriggerCreator {

    @Override
    public TriggerType[]  getTypes() {
        return new TriggerType[] {
                TriggerType.FIX_CASTER_DETONATE
        };
    }

    @Override
    public Trigger newInstance(long unitId, long parent, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        return new FixCasterDetonateTrigger(unitId, parent, skillId, buffId, config, scene);
    }
}
