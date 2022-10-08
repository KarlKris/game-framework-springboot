package com.li.battle.ai.blackboard;

import com.li.battle.core.unit.FightUnit;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.springframework.util.CollectionUtils;

import java.util.List;

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


    /** 路径 **/
    private List<Vector2D> ways;
    /** 路径下标 **/
    private int wayIndex;

    /** 移动等待回合数 **/
    private int waitRound;

    public BlackBoard(FightUnit unit) {
        this.unit = unit;
    }


    public void incrementSkillIndex() {
        skillIndex = (skillIndex + 1) % unit.getSkills().size();
    }

    public void setEnemy(FightUnit enemy) {
        this.enemy = enemy;
    }

    public void setWays(List<Vector2D> ways) {
        this.ways = ways;
        this.wayIndex = 1;
    }

    public int incrementWayIndex() {
        return ++this.wayIndex;
    }

    public boolean isWayEmpty() {
        return CollectionUtils.isEmpty(ways);
    }

    public void clearWays() {
        this.ways = null;
        this.wayIndex = 0;
    }

    public int incrementWaitRound() {
        return ++waitRound;
    }

    public void resetWaitRound() {
        this.waitRound = 0;
    }
}
