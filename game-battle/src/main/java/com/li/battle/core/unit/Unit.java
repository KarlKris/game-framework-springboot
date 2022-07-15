package com.li.battle.core.unit;

import com.li.battle.collision.CollisionDetector;
import com.li.battle.core.CampType;
import com.li.battle.core.UnitState;
import com.li.battle.core.UnitType;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.collision.Shape;
import com.li.battle.util.CollisionUtil;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 单位接口
 * @author li-yuanwen
 * @date 2022/4/24
 */
public interface Unit extends IPosition, Shape {

    /**
     * 获取单元唯一标识
     * @return 单元id
     */
    long getId();

    /**
     * 获取单元当前状态
     * @return 单元当前状态
     */
    UnitState getState();

    /**
     * 修改状态
     * @param state 新状态
     */
    void modifyState(UnitState state);

    /**
     * 获取单位半径(即把单位看作半径为radius的圆柱体)
     * @return 单位半径
     */
    double getRadius();

    /**
     * 获取单元类型
     * @return 单元类型
     */
    UnitType getUnitType();

    /**
     * 获取所在阵营
     * @return 阵营
     */
    CampType getCampType();

    /**
     * 获取单位关联的战斗场景
     * @return 战斗场景
     */
    BattleScene getScene();

    /**
     * 战斗单元形状是圆形,获取横坐标最小值
     * @return 横坐标最小值
     */
    @Override
    default double getLeft() {
        return getPosition().getX() - getRadius();
    }

    /**
     * 战斗单元形状是圆形,获取横坐标最大值
     * @return 横坐标最大值
     */
    @Override
    default double getRight() {
        return getPosition().getX() + getRadius();
    }

    /**
     * 战斗单元形状是圆形,获取纵坐标最大值
     * @return 纵坐标最大值
     */
    @Override
    default double getTop() {
        return getPosition().getY() + getRadius();
    }

    /**
     * 战斗单元形状是圆形,获取纵坐标最小值
     * @return 纵坐标最小值
     */
    @Override
    default double getBottom() {
        return getPosition().getY() - getRadius();
    }

    @Override
    default List<Vector2D> getAllProtectionAxis(CollisionDetector detector) {
        // 圆的投影轴只需要以圆心与多边形顶点中最近的一点的连线
        double minLength = Double.MAX_VALUE;
        Vector2D nearestPoint = null;
        for (Vector2D point : detector.getAllPoints()) {
            double distance = Vector2D.distance(getPosition(), point);
            if (distance < minLength) {
                minLength = distance;
                nearestPoint = point.subtract(getPosition());
            }
        }

        return nearestPoint == null ? Collections.emptyList() : Collections.singletonList(nearestPoint.normalize());
    }

    @Override
    default List<Vector2D> getAllPoints() {
        List<Vector2D> points = new LinkedList<>();
        // 上下左右 四个点
        points.add(new Vector2D(getPosition().getX(), getTop()));
        points.add(new Vector2D(getPosition().getX(), getBottom()));
        points.add(new Vector2D(getLeft(), getPosition().getY()));
        points.add(new Vector2D(getRight(), getPosition().getY()));

        return points;
    }

    @Override
    default boolean isCollision(CollisionDetector detector) {
        // 2个圆只需要比较圆心距
        if (!(detector instanceof Unit)) {
            return Shape.super.isCollision(detector);
        }
        return CollisionUtil.isCollisionBetweenUnit(this, (Unit) detector);
    }
}
