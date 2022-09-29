package com.li.battle.harm;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.domain.*;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.*;
import java.util.*;

/**
 * 伤害计算
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Component
public class HarmExecutor {

    @Resource
    private ApplicationContext context;

    /** 伤害类型计算器 **/
    private final Map<HarmType, HarmCalculator<AbstractDamageEffectParam>> calculators = new EnumMap<>(HarmType.class);

    @PostConstruct
    private void initialize() {
        for (HarmCalculator<AbstractDamageEffectParam> calculator : context.getBeansOfType(HarmCalculator.class).values()) {
            HarmCalculator<AbstractDamageEffectParam> old = calculators.putIfAbsent(calculator.getType(), calculator);
            if (old != null) {
                throw new BeanInitializationException("存在多个相同类型的HarmCalculator: " + old.getType().name());
            }
        }
    }

    /**
     * 计算伤害
     * @param attacker 攻击方
     * @param defender 防守方
     * @param effectParam 伤害参数
     * @return 伤害值
     */
    public long calculate(FightUnit attacker, FightUnit defender, AbstractDamageEffectParam effectParam) {
        HarmCalculator<AbstractDamageEffectParam> harmCalculator = calculators.get(effectParam.getHarmType());
        return harmCalculator.calculate(attacker, defender, effectParam);
    }

}
