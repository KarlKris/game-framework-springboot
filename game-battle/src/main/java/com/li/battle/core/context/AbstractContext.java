package com.li.battle.core.context;

import com.li.battle.core.scene.BattleScene;
import lombok.Getter;

/**
 * 所有战斗上下文基类
 * @author li-yuanwen
 */
@Getter
public abstract class AbstractContext {

    private final BattleScene scene;

    public AbstractContext(BattleScene scene) {
        this.scene = scene;
    }
}
