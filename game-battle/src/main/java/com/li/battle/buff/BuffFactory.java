package com.li.battle.buff;

import com.li.battle.buff.core.Buff;
import com.li.battle.buff.creator.BuffCreator;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.BuffConfig;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.EnumMap;
import java.util.Map;

/**
 * Buff创建工厂
 * @author li-yuanwen
 * @date 2022/5/25
 */
@Component
public class BuffFactory {

    @Resource
    private ApplicationContext applicationContext;

    private final Map<BuffType, BuffCreator> creatorHolder = new EnumMap<>(BuffType.class);

    @PostConstruct
    private void initialize() {
        for (BuffCreator creator : applicationContext.getBeansOfType(BuffCreator.class).values()) {
            BuffCreator old = creatorHolder.putIfAbsent(creator.getType(), creator);
            if (old != null) {
                throw new BeanInitializationException("存在多个相同buff类型:[" + old.getType().name() + "]的构建器");
            }
        }
    }



    /**
     * 创建buff
     * @param caster buff施加者
     * @param target buff挂载者
     * @param config buff配置
     * @param skillId buff由哪个技能创建,<=0代表非是由技能创建
     * @return buff实例
     */
    public Buff newInstance(FightUnit caster, FightUnit target, BuffConfig config, int skillId) {
        BuffCreator creator = creatorHolder.get(config.getType());
        return creator.newInstance(caster, target, config, skillId);
    }




}
