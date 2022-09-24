package com.li.battle.effect.source;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.trigger.TriggerReceiver;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * 触发器效果
 * @author li-yuanwen
 * @date 2022/9/22
 */
public class TriggerReceiverEffectSource extends AbstractEffectSource {

    /** 触发器 **/
    private final TriggerReceiver receiver;
    /** 触发者 **/
    private final FightUnit unit;

    public TriggerReceiverEffectSource(TriggerReceiver receiver, @Nullable FightUnit unit) {
        this.receiver = receiver;
        this.unit = unit;
    }

    @Override
    public FightUnit getCaster() {
        return receiver.battleScene().getFightUnit(receiver.getOwner());
    }

    @Override
    public List<IPosition> getTargets() {
        if (unit == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(unit);
    }

    @Override
    public List<FightUnit> getTargetUnits() {
        if (unit == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(unit);
    }

    @Override
    public BattleScene battleScene() {
        return receiver.battleScene();
    }

    @Override
    public int getSkillId() {
        return receiver.getSkillId();
    }

    @Override
    public int getBuffId() {
        return receiver.getBuffId();
    }

}

