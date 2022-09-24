package com.li.battle.core;

import com.li.battle.effect.EffectExecutor;
import com.li.battle.event.EventHandlerHolder;
import com.li.battle.harm.HarmExecutor;
import com.li.battle.projectile.ProjectileCreatorHolder;
import com.li.battle.selector.SelectorHolder;
import com.li.battle.skill.executor.BattleSkillExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 战斗组件容器
 * @author li-yuanwen
 * @date 2022/5/25
 */
@Component
public class BattleSceneHelper {

    @Resource
    private ConfigHelper configHelper;
    @Resource
    private SelectorHolder selectorHolder;
    @Resource
    private EventHandlerHolder eventHandlerHolder;
    @Resource
    private BattleSkillExecutor battleSkillExecutor;
    @Resource
    private ProjectileCreatorHolder projectileCreatorHolder;
    @Resource
    private EffectExecutor effectExecutor;
    @Resource
    private HarmExecutor harmExecutor;


    public ConfigHelper configHelper() {
        return configHelper;
    }

    public SelectorHolder selectorHolder() {
        return selectorHolder;
    }

    public EventHandlerHolder eventHandlerHolder() {
        return eventHandlerHolder;
    }

    public BattleSkillExecutor battleSkillExecutor() {
        return battleSkillExecutor;
    }

    public ProjectileCreatorHolder projectileCreatorHolder() {
        return projectileCreatorHolder;
    }

    public EffectExecutor effectExecutor() {
        return effectExecutor;
    }

    public HarmExecutor harmExecutor() {
        return harmExecutor;
    }
}
