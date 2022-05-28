package com.li.battle.trigger;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.TriggerConfig;

/**
 * TriggerReceiver 构建工厂
 * @author li-yuanwen
 * @date 2022/5/26
 */
public class TriggerReceiverFactory {


    /**
     * 实例化TriggerReceiver并向EventDispatcher注册
     * @param unitId 触发器制造者标识
     * @param target 目标标识
     * @param skillId 关联技能id
     * @param buffId 关联buffId
     * @param config 触发器配置
     * @param scene 所属战斗场景
     * @return TriggerReceiver实例
     */
    public static TriggerReceiver newInstanceAndRegister(long unitId, long target, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        TriggerReceiver receiver = new TriggerReceiver(unitId, target, skillId, buffId, config, scene);
        receiver.registerEventReceiverIfNecessary();
        return receiver;
    }

}
