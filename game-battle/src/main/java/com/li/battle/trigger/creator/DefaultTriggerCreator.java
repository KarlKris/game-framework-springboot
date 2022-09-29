package com.li.battle.trigger.creator;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.*;
import org.springframework.stereotype.Component;

/**
 * 默认触发器创建器
 * @author li-yuanwen
 * @date 2022/9/26
 */
@Component
public class DefaultTriggerCreator implements TriggerCreator {

    @Override
    public TriggerType[] getTypes() {
        return new TriggerType[] {
                TriggerType.DISTANCE
        };
    }

    @Override
    public Trigger newInstance(long unitId, long parent, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        return new Trigger(unitId, parent, skillId, buffId, config, scene);
    }
}
