package com.li.battle.util;

import cn.hutool.core.util.RandomUtil;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.MoveUnit;
import com.li.battle.core.unit.Unit;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 操控行为封装---游戏人工智能编程案例精粹---3.4操控行为
 * @author li-yuanwen
 * @date 2022/4/24
 */
public class SteeringBehaviourUtil {


    /**
     * 靠近操控行为返回一个操控目标到达目标位置的力向量
     * @param unit 操控单位
     * @param targetPos 目标位置向量
     * @return 到达目标位置的力向量
     */
    public static Vector2D seek(MoveUnit unit, Vector2D targetPos) {
        // 首先计算预期速度,这个速度在理想化情况下达到目标位置所需的速度.
        // 是从起始位置到目标的向量,大小为最大速度
        Vector2D desiredSpeed = targetPos.subtract(unit.getPosition()).normalize().scalarMultiply(unit.getMaxSpeed());
        // 该方法返回的操控力,当把它加到目标当前速度向量上就得到预期的速度,所以简单的从预期速度中减去目标的当前速度
        return desiredSpeed.subtract(unit.getVelocity());
    }


    /**
     * 离开操控行为返回一个操控目标离开目标位置的力向量
     * @param unit 操控单位
     * @param targetPos 目标位置向量
     * @param panicDistance 恐慌距离 没有则填负数
     * @return 离开目标位置的力向量
     */
    public static Vector2D flee(MoveUnit unit, Vector2D targetPos, int panicDistance) {
        if (panicDistance > 0) {
            // 如果目标在恐慌距离之内,离开,用距离平方计算
            int panicDistanceSq = panicDistance * panicDistance;
            if (unit.getPosition().distanceSq(targetPos) > panicDistanceSq) {
                // 只有在恐慌距离内才会产生力
                return Vector2D.ZERO;
            }
        }
        // 离开和靠近是相反方向的向量
        Vector2D desiredSpeed = unit.getPosition().subtract(targetPos).normalize().scalarMultiply(unit.getMaxSpeed());
        return desiredSpeed.subtract(unit.getVelocity());
    }


    /**
     * 操控目标徐缓地停在目标位置上
     * @param unit 操控单位
     * @param targetPos 目标位置向量
     * @param deceleration 减速级别
     * @return 停在目标位置上的力向量
     */
    public static Vector2D arrive(MoveUnit unit, Vector2D targetPos, Deceleration deceleration) {
        Vector2D toTarget = targetPos.subtract(unit.getPosition());

        // 计算到目标位置的距离
        double distance = toTarget.getNorm();
        if (distance > 0) {
            // 因为枚举Deceleration是int型,所以需要这个值来提供调整减速度
            double decelerationTweaker = 0.3d;
            // 给定预期减速度,计算能达到目标位置所需的速度
            double speed = distance / (decelerationTweaker * deceleration.speedLevel);
            // 确保这个速度不会超过最大速度
            speed = Math.min(speed, unit.getMaxSpeed());

            // 已经得到长度,就不需要标准化
            Vector2D desiredSpeed = toTarget.scalarMultiply(speed / distance);
            return desiredSpeed.subtract(unit.getVelocity());
        }
        return Vector2D.ZERO;
    }


    /**
     * 追逐（拦截一个目标）
     * @param pursuer 追逐者
     * @param evader 逃避者
     * @return 追逐目标位置的力向量
     */
    public static Vector2D pursuit(MoveUnit pursuer, MoveUnit evader) {
        // 提前结束情况:逃避者在前面,几乎面对追逐者(逃避者朝向的反方向和追逐者的朝向在20°内,被认为是面对着的),那么追逐者直接向逃避者当前位置移动即可

        Vector2D toEvader = evader.getPosition().subtract(pursuer.getPosition());

        // 向量点乘物理意义:计算向量a和向量b之间的夹角。从而就可以进一步判断这两个向量是否是同一方向，是否正交(也就是垂直)等方向关系，
        // 具体对应关系为：
        //     a·b>0    方向基本相同，夹角在0°到90°之间
        //     a·b=0    正交，相互垂直
        //     a·b<0    方向基本相反，夹角在90°到180°之间

        // 在前面(距离向量与追逐者朝向在90°内)
        if (toEvader.dotProduct(pursuer.getHeading()) > 0
                && Vector2D.angle(pursuer.getHeading(), evader.getHeading()) > 160) {
            return seek(pursuer, evader.getPosition());
        }

        // 预测逃避者的位置

        // 预测的时间正比于二者的距离;反比于二者速度
        double lookAheadTime = toEvader.getNorm() / ((pursuer.getMaxSpeed() - evader.getVelocity().getNorm()) * 1.0d);

        // 现在靠近逃避者的被预测位置
        return seek(pursuer, evader.getPosition().add(evader.getVelocity()).scalarMultiply(lookAheadTime));

    }


    /**
     * 远离一个目标（除了逃离者预测的位置这一点,几乎跟追逐一样）
     * @param pursuer 追逐者
     * @param evader 逃避者
     * @return 远离目标位置的力向量
     */
    public static Vector2D evade(MoveUnit pursuer, MoveUnit evader) {
        // 没有必要检查方向
        Vector2D toPursuer = pursuer.getPosition().subtract(evader.getPosition());

        // 预测的时间正比于二者的距离;反比于二者速度
        double lookAheadTime = toPursuer.getNorm() / (evader.getMaxSpeed() - pursuer.getSpeed());

        // 现在逃离追逐者预测的位置
        return flee(evader, pursuer.getPosition().add(pursuer.getVelocity()).scalarMultiply(lookAheadTime), 100);

    }


    /**
     * 目标徘徊,返回一个操控力,使目标在环境中随机走动
     * 徘徊解决方案:移动单位前面凸出一个圆圈,目标位置被限制在该圆圈上,然后移向目标位置.这样会没有抖动
     * @param unit 移动目标
     * @param wanderRadius wander圈半径
     * @param wanderDistance wander圈凸出在目标前面的距离,决定了转弯幅度
     * @param wanderJitter 每秒加到目标的随机位移的最大值
     * @return 一个操控力,使目标在环境中随机走动
     */
    public static Vector2D wander(MoveUnit unit, int wanderRadius, int wanderDistance, int wanderJitter) {
        // 首先,加一个小小的随机向量到目标位置
        Vector2D localWander = unit.getLocalWander().add(new Vector2D(RandomUtil.randomInt(-1, 1) * wanderJitter
                , RandomUtil.randomInt(-1, 1) * wanderJitter));
        // 把这个新的向量重新投影到wander圆圈上,即先使长度缩小为1,在增加wander圆周半径长度(位置向量这里从全局转成了局部)
        localWander = localWander.normalize().scalarMultiply(wanderRadius);

        // 更新移动单位的wanderTarget
        unit.updateLocalWander(localWander);

        // 把wanderTarget移动到移动单位前面wanderDistance距离
        localWander = localWander.add(new Vector2D(wanderDistance, 0));

        //  投影 从局部转全局
        Vector2D wanderTarget = pointToWorldSpace(localWander, unit);

        return wanderTarget.subtract(unit.getPosition());

    }


    /**
     * 避开障碍行为--操控移动单位避开路上的障碍
     * 使用检测盒与障碍物做碰撞检测
     * @param unit 移动单位
     * @param scene 场景
     * @return 一个操控力,使目标在环境中避开障碍
     */
    public static Vector2D obstacleAvoidance(MoveUnit unit, BattleScene scene) {
        int minLength = 10;
        // 检测盒长度正比与移动单位速度,检测盒宽度等于单位直径
        double detectionBoxLength = minLength + (unit.getSpeed() / unit.getMaxSpeed()) * minLength;

        // 最近的障碍物
        Unit closestIntersectingObstacle = null;
        // 最近的障碍物对应的距离
        double distToClosestObstacle = Double.MAX_VALUE;
        // 最近障碍物对应的移动目标的局部坐标
        Vector2D localPosOfClosestObstacle = null;
        // 遍历场景内所有单位
        for (Unit otherUnit : scene.getUnits()) {
            if (unit.getId() == otherUnit.getId()) {
                continue;
            }
            Vector2D to = otherUnit.getPosition().subtract(unit.getPosition());
            double range = detectionBoxLength + otherUnit.getRadius();
            // 1.过滤不在检测盒范围内的障碍物
            if (to.getNormSq() > (range * range)) {
                continue;
            }
            // 2.把检测盒范围内的单位转换至移动目标的局部空间,转换后那些x坐标为负值的单位不再考虑(即在移动单位后面)
            Vector2D localPos = pointToLocalSpace(otherUnit.getPosition(), unit);
            if (localPos.getX() < 0) {
                continue;
            }
            // 3.测试检测盒是否和障碍物重叠,使障碍物的包围半径扩大检测盒宽度的一半.
            // 然后测试该障碍物的y值是否小于(检测盒宽度的一半+障碍物的包围半径)
            // 不用担心相切的情况,会擦肩而过
            double expendRadius = otherUnit.getRadius() + unit.getRadius();
            if (Math.abs(localPos.getY()) >= expendRadius) {
                continue;
            }
            // 4.此时,只剩下那些会与检测盒相交的障碍物了,找到离移动单位最近的相交点.
            // 用简单的线-圆周相交测试方法就可以得到被扩大的圈和X轴的相交点

            // 现在做线-圆周相交测试,圆周的中心是(cx,cy)
            // 相交点公式: x=cx-sqrt(r^2-cy^2)
            double sqrtPart = Math.sqrt(Math.pow(expendRadius, 2) - Math.pow(localPos.getY(), 2));
            double x = localPos.getX() - sqrtPart;
            if (x <= 0d) {
                x = localPos.getX() + sqrtPart;
            }

            if (x < distToClosestObstacle) {
                distToClosestObstacle = x;
                closestIntersectingObstacle = otherUnit;
                localPosOfClosestObstacle = localPos;
            }
        }

        // 5.计算操控力
        Vector2D force = null;
        if (closestIntersectingObstacle != null) {
            // 移动目标离障碍物越近,操控力就越强
            double multiplier = 1.0d + (detectionBoxLength - localPosOfClosestObstacle.getX()) / detectionBoxLength;
            // 计算侧向力
            double y = (closestIntersectingObstacle.getRadius() - localPosOfClosestObstacle.getY()) * multiplier;

            // 施加一个制向力,它正比于障碍物到移动目标的距离
            int brakingWeight = 2;
            double x = (closestIntersectingObstacle.getRadius() - localPosOfClosestObstacle.getX()) * brakingWeight;

            force = new Vector2D(x, y);
        } else {
            force = Vector2D.ZERO;
        }


        // 最后将操控向量从局部转换到全局
        return vectorToWorldSpace(force, unit);
    }


    /**
     * 局部位置向量映射到全局位置向量
     * @param local 局部位置向量
     * @param unit 相对目标单位
     * @return 全局位置向量
     */
    public static Vector2D pointToWorldSpace(Vector2D local, MoveUnit unit) {
        // todo
        return local;
    }

    /**
     * 全局位置向量转换成局部位置向量
     * @param point 全局位置变量
     * @param unit 相对目标单位
     * @return 局部位置向量
     */
    public static Vector2D pointToLocalSpace(Vector2D point, MoveUnit unit) {
        // todo
        return point;
    }


    /**
     * 局部向量映射到全局变量
     * @param vector 局部向量
     * @param unit 相对目标单位
     * @return 全局变量
     */
    public static Vector2D vectorToWorldSpace(Vector2D vector, MoveUnit unit) {
        // todo
        return vector;
    }



    /**
     * 减速枚举
     */
    public static enum Deceleration {

        /** 快速减速 **/
        FAST(1),

        /** 普通减速 **/
        NORMAL(2),

        /** 缓慢减速 **/
        SLOW(3),

        ;

        /** 速度级别,越大,减速幅度越小 **/
        private final int speedLevel;

        Deceleration(int speedLevel) {
            this.speedLevel = speedLevel;
        }

    }

}
