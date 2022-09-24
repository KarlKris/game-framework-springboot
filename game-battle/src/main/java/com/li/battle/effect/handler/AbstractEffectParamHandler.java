package com.li.battle.effect.handler;

import com.li.battle.effect.*;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.EffectSource;
import com.li.common.utils.TypeParameterMatcher;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于匹配EffectParam的抽象类
 * @author li-yuanwen
 * @date 2022/9/22
 */
@Slf4j
public abstract class AbstractEffectParamHandler<EP extends EffectParam> implements EffectHandler {

    /** 效果参数类型比对器 **/
    private final TypeParameterMatcher effectParamMatcher;

    public AbstractEffectParamHandler() {
        this.effectParamMatcher = TypeParameterMatcher.find(this, AbstractEffectParamHandler.class, "EP");
    }

    @Override
    public void execute(EffectSource source, EffectParam effectParam) {
        if (accept(effectParam)) {
            EP castEffect = (EP) effectParam;
            execute0(source, castEffect);
        } else {
            log.error("配置出现错误,出现效果参数[{}]和处理器[{}]不匹配的情况", effectParam.getClass().getSimpleName()
                    , this.getClass().getSimpleName());
        }
    }


    /**
     * 留给子类去执行的真正的效果
     * @param source EffectSource
     * @param effectParam effectParam
     */
    protected abstract void execute0(EffectSource source, EP effectParam);

    protected boolean accept(EffectParam effectParam) {
        return effectParamMatcher.match(effectParam);
    }

}
