package com.li.battle.core.unit;

import com.li.battle.core.Attribute;
import com.li.battle.core.UnitState;
import com.li.battle.core.UnitType;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Map;

/**
 * 抽象战斗单元
 * @author li-yuanwen
 * @date 2022/5/6
 */
public abstract class AbstractFightUnit implements FightUnit {


    /** 唯一标识 **/
    private final long id;
    /** 类型 **/
    private final UnitType type;
    /** 范围半径 **/
    private final double radius;
    /** 当前状态 **/
    private UnitState state;
    /** 当前位置 **/
    private Vector2D position;

    /** 最高移速 **/
    private final int maxSpeed;
    /** 当前速度 **/
    private Vector2D velocity;
    /** 上次徘徊的随机点 **/
    private Vector2D localWander;

    /** 战斗属性 **/
    private final Map<Attribute, Long> attributes;



    public AbstractFightUnit(long id, UnitType type, double radius, int maxSpeed
            , Vector2D position, Map<Attribute, Long> attributes) {
        this.id = id;
        this.type = type;
        this.radius = radius;
        this.maxSpeed = maxSpeed;
        this.attributes = attributes;
        this.position = position;

        this.state = UnitState.NORMAL;
        this.velocity = Vector2D.ZERO;
    }

    @Override
    public UnitType getUnitType() {
        return type;
    }

    @Override
    public long getAttributeValue(Attribute attribute) {
        return attributes.getOrDefault(attribute, 0L);
    }

    @Override
    public void modifyAttribute(Attribute attribute, Long value) {
        this.attributes.put(attribute, value);
    }

    @Override
    public int getMaxSpeed() {
        return maxSpeed;
    }

    @Override
    public Vector2D getVelocity() {
        return velocity;
    }

    @Override
    public double getSpeed() {
        return getVelocity().getNorm();
    }

    @Override
    public Vector2D getHeading() {
        return getVelocity().normalize();
    }

    @Override
    public Vector2D getLocalWander() {
        return localWander;
    }

    @Override
    public void updateLocalWander(Vector2D localWander) {
        this.localWander = localWander;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public UnitState getState() {
        return state;
    }

    @Override
    public void modifyState(UnitState state) {
        this.state = state;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public double getRadius() {
        return radius;
    }
}
