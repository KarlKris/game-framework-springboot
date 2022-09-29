package com.li.battle.trigger.creator;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.*;

/**
 * Trigger实例创建器
 * @author li-yuanwen
 * @date 2022/9/26
 */
public interface TriggerCreator {

    /**
     * 负责的触发器类型
     * @return 触发器类型
     */
    TriggerType[] getTypes();


    /**
     * 创建触发器实例
     * @param unitId 创建者
     * @param parent 挂载者
     * @param skillId 关联的技能id
     * @param buffId 关联的buff id
     * @param config 触发器配置
     * @param scene 战斗场景
     * @return 触发器
     */
    Trigger newInstance(long unitId, long parent, int skillId, int buffId, TriggerConfig config, BattleScene scene);

}
