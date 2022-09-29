package com.li.battle.effect.handler;

import com.li.battle.core.*;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.domain.AbstractDamageEffectParam;
import com.li.battle.effect.source.EffectSource;
import com.li.battle.event.EventDispatcher;
import com.li.battle.event.core.*;
import com.li.battle.harm.HarmExecutor;
import com.li.battle.util.ValueAlter;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象伤害类效果(决定伤害流程),所有伤害类效果都必须继承该类
 * @author li-yuanwen
 * @date 2022/9/23
 */
@Slf4j
public abstract class AbstractDamageEffectHandler<EP extends AbstractDamageEffectParam> extends AbstractEffectParamHandler<EP> {

    @Override
    protected void execute0(EffectSource source, EP effectParam) {
        FightUnit attacker = source.getCaster();
        for (FightUnit defender : source.getTargetUnits()) {
            processDamage(source, attacker, defender, effectParam);
        }
    }

    /**
     * 执行伤害流程
     * @param source 效果源
     * @param attacker 攻击方
     * @param defender 防守方
     * @param effectParam 伤害效果参数
     */
    protected void processDamage(EffectSource source, FightUnit attacker, FightUnit defender, EP effectParam) {

        if (log.isDebugEnabled()) {
            log.debug("计算单位[{}]对单位[{}]的伤害, 伤害源[{}],伤害参数[{}]"
                    , attacker.getId(), defender.getId(), source.getClass().getSimpleName(), effectParam);
        }

        // 计算伤害
        long damage = calculateDamage(attacker, defender, effectParam);
        // 存储数据
        ValueAlter valueAlter = newValueAlter(damage);
        source.initDamageValue(valueAlter);
        // 事件分发器
        EventDispatcher eventDispatcher = source.battleScene().eventDispatcher();
        // 伤害前事件
        BeforeDamageEvent beforeDamageEvent = new BeforeDamageEvent(source, attacker.getId(), defender.getId());
        eventDispatcher.dispatch(beforeDamageEvent);

        // 扣除血量
        execDamage(attacker, defender, source);

        // 伤害后事件
        AfterDamageEvent afterDamageEvent = new AfterDamageEvent(source, attacker.getId(), defender.getId());
        eventDispatcher.dispatch(afterDamageEvent);

        if (defender.isDead()) {
            // 死亡前事件
            BeforeDeadEvent beforeDeadEvent = new BeforeDeadEvent(source, attacker.getId(), defender.getId());
            eventDispatcher.dispatch(beforeDeadEvent);
        }

        if (defender.isDead()) {
            // 击杀事件
            KillEvent killEvent = new KillEvent(source, attacker.getId(), defender.getId());
            eventDispatcher.dispatch(killEvent);
            // 死亡移除
            source.battleScene().distributed().remove(defender);
        }

    }

    /**
     * 留给子类去new ValueAlter() 例如真实伤害(无视护盾),普通伤害
     * @param dmg
     * @return
     */
    protected abstract ValueAlter newValueAlter(long dmg);

    /**
     * 执行伤害扣除血量
     * @param attacker 攻击方
     * @param defender 防守方
     * @param source 效果源
     */
    protected void execDamage(FightUnit attacker, FightUnit defender, EffectSource source) {
        long dmg = calculateFinalDamage(attacker, defender, source.getDamageValue());
        // 扣除伤害
        defender.onHurt(dmg);
        if (log.isDebugEnabled()) {
            log.debug("单位[{}]受到单位[{}]的伤害[{}],剩余血量[{}]"
                    , defender.getId(), attacker.getId(), dmg, defender.getAttributeValue(Attribute.CUR_HP));
        }
    }

    /**
     * 计算最终伤害值 默认受增伤和减伤影响
     * @param attacker 攻击方
     * @param defender 防守方
     * @param valueAlter 伤害数据
     * @return 最终伤害值
     */
    protected long calculateFinalDamage(FightUnit attacker, FightUnit defender, ValueAlter valueAlter) {
        // 计算最终伤害值----己方伤害提升百分比和敌方伤害降低百分比 来修改伤害值
        long dmg = valueAlter.getValue();
        long inc = dmg * attacker.getAttributeValue(Attribute.DAMAGE_INC) / Const.TEN_THOUSAND;
        long dec = dmg * defender.getAttributeValue(Attribute.DAMAGE_DEC) / Const.TEN_THOUSAND;
        dmg += (inc - dec);

        // 敌方护盾抵扣
        long shieldValue = defender.getAttributeValue(Attribute.SHIELD);
        if (shieldValue > dmg) {
            defender.modifyAttribute(Attribute.SHIELD, -dmg);
        }

        return dmg;
    }


    /**
     * 根据伤害效果参数去计算伤害值
     * @param attacker 攻击方
     * @param defender 防守方
     * @param param 伤害参数
     * @return 伤害值
     */
    private long calculateDamage(FightUnit attacker, FightUnit defender, EP param) {
        HarmExecutor executor = attacker.getScene().battleSceneHelper().harmExecutor();
        return executor.calculate(attacker, defender, param);
    }

}
