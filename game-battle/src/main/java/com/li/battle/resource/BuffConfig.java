package com.li.battle.resource;

import com.li.battle.buff.BuffMerge;
import com.li.battle.buff.BuffMonitorSkillCasterType;
import com.li.battle.buff.BuffType;
import com.li.battle.buff.core.Buff;
import com.li.battle.effect.Effect;
import com.li.common.resource.anno.ResourceId;
import com.li.common.resource.anno.ResourceObj;
import lombok.Getter;

/**
 * buff配置
 * @author li-yuanwen
 * @date 2022/5/18
 */
@Getter
@ResourceObj
public class BuffConfig {

    /** 唯一标识 **/
    @ResourceId
    private int id;
    /** buff类型 **/
    private BuffType type;
    /** buff种类 **/
    private byte tag;
    /** 免疫buff种类 **/
    private byte immuneTag;
    /** buff时长(毫秒) 0表永久 **/
    private int duration;
    /** buff刷新合并规则（更新Buff层数，等级，持续时间等数据） **/
    private BuffMerge mergeRule;
    /** buff生效效果 **/
    private Effect<Buff>[] awakeEffects;

    /** buff触发间隔(毫秒) **/
    private int thinkInterval;
    /** buff间隔持续效果 **/
    private Effect<Buff>[] thinkEffects;

    /** buff移除效果 **/
    private Effect<Buff>[] removeEffects;
    /** buff销毁效果 **/
    private Effect<Buff>[] destroyEffects;

    /** buff改变运动状态时效果 **/
    private Effect<Buff>[] motionUpdateEffects;
    /** buff打断运动时效果 **/
    private Effect<Buff>[] motionInterruptEffects;

    /** 某个主动技能执行成功时效果 **/
    private Effect<Buff>[] executedEffects;
    /** 监听的主动技能id **/
    private int[] skillIds;
    /** 技能施法人类型 **/
    private BuffMonitorSkillCasterType monitorSkillCasterType;

    /** 我方给目标造成伤害前触发效果 **/
    private Effect<Buff>[] beforeDamageEffects;
    /** 我方给目标造成伤害后触发效果 **/
    private Effect<Buff>[] afterDamageEffects;

    /** 我方受到伤害前触发效果 **/
    private Effect<Buff>[] beforeTakeDamageEffects;
    /** 我方受到伤害后触发效果 **/
    private Effect<Buff>[] afterTakeDamageEffects;

    /** 我方死亡前触发效果 **/
    private Effect<Buff>[] beforeDeadEffects;
    /** 我方死亡后触发效果 **/
    private Effect<Buff>[] afterDeadEffects;

    /** 我方击杀目标后触发 **/
    private Effect<Buff>[] afterKillEffects;
}
