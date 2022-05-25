package com.li.battle.core;

import com.li.battle.ConfigHelper;
import com.li.battle.buff.BuffFactory;
import com.li.battle.selector.SelectorHolder;
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
    private BuffFactory bufferFactory;


    public ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public SelectorHolder getSelectorHolder() {
        return selectorHolder;
    }

    public BuffFactory getBufferFactory() {
        return bufferFactory;
    }
}
