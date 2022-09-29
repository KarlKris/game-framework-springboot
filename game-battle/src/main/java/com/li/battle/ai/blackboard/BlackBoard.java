package com.li.battle.ai.blackboard;

import com.li.battle.core.unit.FightUnit;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 黑板,用于共享数据
 * @author li-yuanwen
 * @date 2022/9/27
 */
@Getter
public class BlackBoard {

    /** 操控单位 **/
    private final FightUnit unit;
    /** 选择的技能下标 **/
    private int skillIndex;
    /** 选择的敌人 **/
    private FightUnit enemy;
    /** 移动坐标 **/
    private Vector2D point;


    public BlackBoard(FightUnit unit) {
        this.unit = unit;
    }


    public void incrementSkillIndex() {
        skillIndex = (skillIndex + 1) % unit.getSkills().size();
    }

    public void setEnemy(FightUnit enemy) {
        this.enemy = enemy;
    }

    public void setPoint(Vector2D point) {
        this.point = point;
    }


}
