package com.li.battle.core.unit;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.Attribute;
import com.li.battle.core.Skill;
import com.li.battle.core.scene.BattleScene;

import java.util.List;

/**
 * 战斗单元对外接口
 * @author li-yuanwen
 */
public interface FightUnit extends MoveUnit {


    /**
     * 进入场景
     * @param scene 场景
     */
    void enterScene(BattleScene scene);


    /**
     * 离开场景
     */
    void leaveScene();


    /**
     * 获取属性值
     * @param attribute 属性类型
     * @return 属性值
     */
    long getAttributeValue(Attribute attribute);

    /**
     * 修改属性
     * @param attribute 属性类型
     * @param value 变更值
     */
    void modifyAttribute(Attribute attribute, Long value);


    /**
     * 获取单元所有技能信息
     * @return 单元所有技能信息
     */
    List<Skill> getSkills();


    /**
     * 获取特定的技能信息
     * @param skillId 技能id
     * @return 技能信息
     */
    Skill getSkillById(int skillId);


    /**
     * 技能执行后进CD
     * @param skillId 技能id
     */
    void coolDownSkill(int skillId);


    /**
     * 添加buff
     * @param buff buff
     */
    void addBuff(Buff buff);

    /**
     * 移除身上的buff
     * @param buff
     */
    void removeBuff(Buff buff);

}
