package com.li.battle.selector;

import com.li.battle.ConfigHelper;
import com.li.battle.resource.SelectorConfig;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.EnumMap;
import java.util.Map;

/**
 * 选择器持有实例
 * @author li-yuanwen
 * @date 2022/5/25
 */
@Component
public class SelectorHolder {

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private ConfigHelper configHelper;

    private final Map<SelectorType, Selector> selectorHolder = new EnumMap<>(SelectorType.class);

    @PostConstruct
    private void initialize() {
        for (Selector selector : applicationContext.getBeansOfType(Selector.class).values()) {
            Selector old = selectorHolder.putIfAbsent(selector.getType(), selector);
            if (old != null) {
                throw new BeanInitializationException("存在多个相同选择器类型:[" + selector.getType().name() + "]的选择器");
            }
        }
    }


    public Selector getSelectorById(int selectorId) {
        SelectorConfig config = configHelper.getSelectorConfigById(selectorId);
        return selectorHolder.get(config.getType());
    }

    public Selector getSelectorByType(SelectorType type)  {
        return selectorHolder.get(type);
    }

}
