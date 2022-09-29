package com.li.battle.buff.core;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.BuffConfig;

/**
 * 普通buff对象
 * @author li-yuanwen
 * @date 2022/5/25
 */
public class NormalBuff extends AbstractBuff {

    public NormalBuff(long id, BuffConfig config, long caster, long parent, int skillId, BattleScene scene) {
        super(id, config, caster, parent, skillId, scene);
    }

}
