package com.li.battle.service;

import com.li.battle.core.scene.BattleScene;

/**
 * 战斗模块Service层接口
 * @author li-yuanwen
 * @date 2022/5/30
 */
public interface BattleService {


    /**
     * 创建场景
     * @param mapId 地图标识
     * @return 场景
     */
    BattleScene createScene(int mapId);


}
