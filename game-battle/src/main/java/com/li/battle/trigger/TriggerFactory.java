package com.li.battle.trigger;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.resource.TriggerConfig;
import com.li.battle.trigger.creator.TriggerCreator;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.*;
import java.util.*;

/**
 * TriggerReceiver 构建工厂
 * @author li-yuanwen
 * @date 2022/5/26
 */
@Component
public class TriggerFactory {


    @Resource
    private ApplicationContext context;

    private final Map<TriggerType, TriggerCreator> holder = new EnumMap<>(TriggerType.class);


    @PostConstruct
    private void init() {
        for (TriggerCreator creator : context.getBeansOfType(TriggerCreator.class).values()) {
            for (TriggerType type : creator.getTypes()) {
                TriggerCreator old = holder.putIfAbsent(type, creator);
                if (old != null) {
                    throw new BeanInitializationException("存在多个相同类型的TriggerReceiverCreator: " + type.name());
                }
            }

        }
    }


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
    public Trigger newInstanceAndRegister(long unitId, long target, int skillId, int buffId, TriggerConfig config, BattleScene scene) {
        TriggerCreator creator = holder.get(config.getParam().getType());
        Trigger receiver = creator.newInstance(unitId, target, skillId, buffId, config, scene);
        receiver.registerEventReceiverIfNecessary();
        return receiver;
    }

}
