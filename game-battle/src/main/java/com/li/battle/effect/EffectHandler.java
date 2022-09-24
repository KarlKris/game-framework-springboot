package com.li.battle.effect;

import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.EffectSource;

/**
 * 效果处理器
 * @author li-yuanwen
 * @date 2022/9/22
 */
public interface EffectHandler {

    /**
     * 负责的效果类型
     * @return 效果类型
     */
    EffectType getType();

    /**
     * 执行效果
     * @param source 效果源
     * @param effectParam 效果内容
     */
    void execute(EffectSource source, EffectParam effectParam);

}
