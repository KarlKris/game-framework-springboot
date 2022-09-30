package com.li.battle.ai.action.move;

import com.li.battle.ai.Status;
import com.li.battle.ai.action.AbstractAction;
import com.li.battle.ai.blackboard.BlackBoard;
import com.li.battle.core.scene.BattleSceneReferee;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.util.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;

/**
 * 寻路移动Action
 * @author li-yuanwen
 * @date 2022/9/30
 */
public class MovingAction extends AbstractAction {

    /** 单次寻路最大距离 **/
    static final int MAX_DISTANCE = 5 * 1000;

    @Override
    public Status update(BlackBoard board) {
        FightUnit unit = board.getUnit();
        Vector2D position = unit.getPosition();
        if (board.isWayEmpty()) {
            Vector2D targetPosition = unit.getMoveTargetPosition();

            Vector2D vector2D = targetPosition.subtract(position);
            double distance = vector2D.getNorm();
            if (distance > MAX_DISTANCE) {
                targetPosition = vector2D.scalarMultiply(MAX_DISTANCE);
            }

            List<Vector2D> ways = unit.getScene().sceneMap().findWayByAStar(position.getX(), position.getY()
                    , targetPosition.getX(), targetPosition.getY());
            board.setWays(ways);
        }

        // 最大速度
        int maxSpeed = unit.getMaxSpeed();
        int wayIndex = board.getWayIndex();
        List<Vector2D> ways = board.getWays();
        int size = ways.size();
        double distance = 0;
        Vector2D velocity = unit.getVelocity();
        Vector2D finalPosition = position;
        while (CollisionUtil.isNotEqualWithDouble(distance, maxSpeed) && wayIndex < size) {
            Vector2D oldPos = finalPosition;
            Vector2D target = ways.get(wayIndex);
            velocity = SteeringBehaviourUtil.arrive(oldPos, target, maxSpeed - distance);
            finalPosition = velocity.add(finalPosition);

            distance += (Vector2D.distance(finalPosition, oldPos));
            if (CollisionUtil.isSimilarBetween(target, finalPosition)) {
                wayIndex = board.incrementWayIndex();
            }
        }

        BattleSceneReferee battleSceneReferee = unit.getScene().battleSceneReferee();
        battleSceneReferee.updatePosition(unit.getId(), finalPosition, velocity, wayIndex >= size);

        return Status.SUCCESS;
    }

}
