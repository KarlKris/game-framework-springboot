package com.li.battle.effect.source;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.util.*;

import java.util.List;

/**
 * 单个效果执行的来源接口
 * @author li-yuanwen
 * @date 2022/9/22
 */
public interface EffectSource {

    /**
     * 施加效果的战斗单位
     * @return 战斗单位
     */
    FightUnit getCaster();

    /**
     * 返回效果的目标信息
     * @return 目标信息
     */
    List<IPosition> getTargets();

    /**
     * 返回效果的目标战斗单位
     * 在#getTargets()方法返回上做进一步筛选
     * @return 目标战斗单位
     */
    List<FightUnit> getTargetUnits();

    /**
     * 战斗场景
     * @return 战斗场景
     */
    BattleScene battleScene();

    /**
     * 关联的技能id
     * @return 技能id
     */
    int getSkillId();

    /**
     * 关联的buffId
     * @return buffId or 0
     */
    int getBuffId();

    /**
     * 存储效果所造成的伤害数据
     * @param damageValue 伤害数据
     */
    void initDamageValue(ValueAlter damageValue);

    /**
     * 获取存储的伤害数据
     * @return 伤害数据
     */
    ValueAlter getDamageValue();

    /**
     * 存储效果所造成的属性变更数据
     * @param attributeValue 属性变更数据
     */
    void initAttributeValue(AttributeValueAlter attributeValue);

    /**
     * 获取存储的属性变更数据
     * @return 属性变更数据
     */
    AttributeValueAlter getAttributeValueAlter();

}
