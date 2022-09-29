package com.li.battle.effect;

import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.EffectSource;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.*;
import java.util.*;

/**
 * 效果执行器
 * @author li-yuanwen
 * @date 2022/9/22
 */
@Component
public class EffectExecutor {

    @Resource
    private ApplicationContext context;

    /** 效果处理器 **/
    private final Map<EffectType, EffectHandler> handlers = new EnumMap<>(EffectType.class);

    @PostConstruct
    private void initialize() {
        for (EffectHandler handler : context.getBeansOfType(EffectHandler.class).values()) {
            EffectHandler old = handlers.put(handler.getType(), handler);
            if (old != null) {
                throw new BeanInitializationException("存在多个相同类型的效果处理器EffectHandler : " + handler.getType().name());
            }
        }
    }

    /**
     * 执行效果
     * @param effectSource 效果源
     * @param effectParam 效果
     */
    public void execute(EffectSource effectSource, EffectParam effectParam) {
        EffectHandler handler = handlers.get(effectParam.getType());
        handler.execute(effectSource, effectParam);
    }


}
