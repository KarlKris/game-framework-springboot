package com.li.battle.effect.source;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;

import java.util.*;

/**
 * buff监听事件后执行效果
 * @author li-yuanwen
 * @date 2022/9/26
 */
public class BuffEventEffectSource extends BuffEffectSource {

    /** 事件 **/
    private final EffectSource eventSource;

    public BuffEventEffectSource(Buff buff, EffectSource eventSource) {
        super(buff);
        this.eventSource = eventSource;
    }

    @Override
    public FightUnit getCaster() {
        return battleScene().getFightUnit(buff.getOwner());
    }

    @Override
    public List<IPosition> getTargets() {
        return Collections.singletonList(eventSource.getCaster());
    }

    @Override
    public List<FightUnit> getTargetUnits() {
        return Collections.singletonList(eventSource.getCaster());
    }

    @Override
    public BattleScene battleScene() {
        return buff.battleScene();
    }

    @Override
    public int getSkillId() {
        return buff.getSkillId();
    }

    @Override
    public int getBuffId() {
        return buff.getBuffId();
    }
}
