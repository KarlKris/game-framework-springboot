package com.li.battle.core.unit;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.*;
import com.li.battle.core.scene.BattleScene;

import java.util.*;

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
     * 是否死亡
     * @return true hp<=0
     */
    boolean isDead();

    /**
     * 获取玩家身上的buff
     * @param buffId buff标识
     * @return buff集
     */
    List<Buff> getBuffById(int buffId);

    /**
     * 获取玩家身上的buff(指定施法者的)
     * @param caster 施法者标识
     * @param buffId buff标识
     * @return buff or null
     */
    Buff getBuffByIdAndCaster(long caster, int buffId);

    /**
     * 添加buff
     * @param buff buff
     */
    void addBuff(Buff buff);

    /**
     * 移除buff
     * @param buff buff
     */
    void removeBuff(Buff buff);

    /**
     * 获取玩家身上的所有buff
     * @return buff集
     */
    Collection<Buff> getAllBuffs();

    /**
     * 受到伤害
     * @param dmg 伤害值
     */
    void onHurt(long dmg);

}
