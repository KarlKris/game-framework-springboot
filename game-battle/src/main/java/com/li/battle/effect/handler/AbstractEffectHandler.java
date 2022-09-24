package com.li.battle.effect.handler;

import com.li.battle.effect.EffectHandler;
import com.li.battle.effect.domain.EffectParam;
import com.li.battle.effect.source.EffectSource;
import com.li.common.utils.TypeParameterMatcher;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于匹配EffectParam和EffectSource的抽象类
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Slf4j
public abstract class AbstractEffectHandler<ES extends EffectSource, EP extends EffectParam> implements EffectHandler {

    /** 效果源类型比对器 **/
    private final TypeParameterMatcher sourceMatcher;
    /** 效果参数类型比对器 **/
    private final TypeParameterMatcher effectParamMatcher;

    public AbstractEffectHandler() {
        this.sourceMatcher = TypeParameterMatcher.find(this, AbstractEffectHandler.class, "ES");
        this.effectParamMatcher = TypeParameterMatcher.find(this, AbstractEffectHandler.class, "EP");
    }

    @Override
    public void execute(EffectSource source, EffectParam effectParam) {
        if (accept(source, effectParam)) {
            ES castSource = (ES) source;
            EP castParam = (EP) effectParam;
            execute0(castSource, castParam);
        } else {
            log.error("配置出现错误,出现效果参数[{}],效果源[{}]和处理器[{}]不匹配的情况", effectParam.getClass().getSimpleName()
                    , source.getClass().getSimpleName(), this.getClass().getSimpleName());
        }
    }

    protected boolean accept(EffectSource source, EffectParam effectParam) {
        return sourceMatcher.match(source) && effectParamMatcher.match(effectParam);
    }

    /**
     * 留给子类去执行的真正的效果
     * @param source EffectSource
     * @param effectParam effectParam
     */
    protected abstract void execute0(ES source, EP effectParam);


}
