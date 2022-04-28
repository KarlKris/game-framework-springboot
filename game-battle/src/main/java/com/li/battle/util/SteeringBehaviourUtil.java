package com.li.battle.util;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * 操控行为封装---游戏人工智能编程案例精粹---3.4操控行为
 * @author li-yuanwen
 * @date 2022/4/24
 */
public class SteeringBehaviourUtil {


    /**
     * 靠近操控行为返回一个操控目标到达目标位置的力向量
     * @param sourcePos 当前位置向量
     * @param maxSpeed 最大速度
     * @param velocity 当前速度向量
     * @param targetPos 目标位置向量
     * @return 到达目标位置的力向量
     */
    public static Vector2D seek(Vector2D sourcePos, double maxSpeed, Vector2D velocity, Vector2D targetPos) {
        // 首先计算预期速度,这个速度在理想化情况下达到目标位置所需的速度.
        // 是从起始位置到目标的向量,大小为最大速度
        Vector2D desiredSpeed = targetPos.subtract(sourcePos).normalize().scalarMultiply(maxSpeed);
        // 该方法返回的操控力,当把它加到目标当前速度向量上就得到预期的速度,所以简单的从预期速度中减去目标的当前速度
        return desiredSpeed.subtract(velocity);
    }


    /**
     * 离开操控行为返回一个操控目标离开目标位置的力向量
     * @param sourcePos 当前位置向量
     * @param maxSpeed 最大速度
     * @param velocity 当前速度向量
     * @param targetPos 目标位置向量
     * @param panicDistance 恐慌距离 没有则填负数
     * @return 离开目标位置的力向量
     */
    public static Vector2D flee(Vector2D sourcePos, double maxSpeed, Vector2D velocity, Vector2D targetPos, double panicDistance) {
        if (panicDistance > 0) {
            // 如果目标在恐慌距离之内,离开,用距离平方计算
            double panicDistanceSq = Math.sqrt(panicDistance);
            if (sourcePos.distanceSq(targetPos) > panicDistanceSq) {
                // 只有在恐慌距离内才会产生力
                return Vector2D.ZERO;
            }
        }
        // 离开和靠近是相反方向的向量
        Vector2D desiredSpeed = sourcePos.subtract(targetPos).normalize().scalarMultiply(maxSpeed);
        return desiredSpeed.subtract(velocity);
    }


    /**
     * 操控目标徐缓地停在目标位置上
     * @param sourcePos 当前位置向量
     * @param maxSpeed 最大速度
     * @param velocity 当前速度向量
     * @param targetPos 目标位置向量
     * @param deceleration 减速级别
     * @return 停在目标位置上的力向量
     */
    public static Vector2D arrive(Vector2D sourcePos, double maxSpeed, Vector2D velocity, Vector2D targetPos, Deceleration deceleration) {
        Vector2D toTarget = targetPos.subtract(sourcePos);

        // 计算到目标位置的距离
        double distance = toTarget.getNorm();
        if (distance > 0) {
            // 因为枚举Deceleration是int型,所以需要这个值来提供调整减速度
            double decelerationTweaker = 0.3d;
            // 给定预期减速度,计算能达到目标位置所需的速度
            double speed = distance / (decelerationTweaker * deceleration.speedLevel);
            // 确保这个速度不会超过最大速度
            speed = Math.min(speed, maxSpeed);

            // 已经得到长度,就不需要标准化
            Vector2D desiredSpeed = toTarget.scalarMultiply(speed / distance);
            return desiredSpeed.subtract(velocity);
        }
        return Vector2D.ZERO;
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
        private int speedLevel;

        Deceleration(int speedLevel) {
            this.speedLevel = speedLevel;
        }

    }

}
