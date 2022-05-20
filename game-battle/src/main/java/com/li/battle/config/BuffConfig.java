package com.li.battle.config;

import com.li.battle.buff.model.BuffMergeRule;
import com.li.battle.buff.model.BuffType;
import com.li.battle.effect.Effect;

/**
 * buff配置
 * @author li-yuanwen
 * @date 2022/5/18
 */
public class BuffConfig {

    /** 唯一标识 **/
    private int id;
    /** buff类型 **/
    private BuffType type;
    /** buff种类 **/
    private byte tag;
    /** 免疫buff种类 **/
    private byte immuneTag;
    /** buff时长(毫秒) **/
    private int duration;
    /** buff生效效果 **/
    private Effect[] startEffects;
    /** buff刷新合并规则（更新Buff层数，等级，持续时间等数据） **/
    private BuffMergeRule mergeRule;
    /** buff触发间隔(毫秒) **/
    private int thinkInterval;
    /** buff间隔持续效果 **/
    private Effect[] thinkEffects;
    /** buff移除效果 **/
    private Effect[] removeEffects;
    /** buff销毁效果 **/
    private Effect[] destroyEffects;
    /** buff改变运动状态时效果 **/
    private Effect[] motionUpdateEffects;
    /** buff打断运动时效果 **/
    private Effect[] motionInterruptEffects;
}
