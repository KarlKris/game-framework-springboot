package com.li.battle.projectile;

import com.li.battle.buff.core.Buff;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.effect.Effect;
import com.li.battle.resource.GeneralSkillConfig;
import com.li.battle.util.QuadTree;
import com.li.battle.util.Rectangle;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;

/**
 * 抽象子弹基类
 * @author li-yuanwen
 */
public abstract class AbstractProjectile implements Projectile {

    /** 关联的场景 **/
    protected final BattleScene scene;
    /** 子弹相关配置Id **/
    protected final int projectileId;
    /** 子弹创建者 **/
    protected final long owner;
    /** 子弹关联的技能 **/
    protected final int skillId;
    /** 子弹的飞行速率 **/
    protected final int speed;
    /** 子弹当前位置 **/
    protected Vector2D position;
    /** 子弹碰撞范围,同步子弹位置变更 **/
    protected Rectangle rectangle;



    AbstractProjectile(BattleScene scene, int projectileId, long owner, int skillId, Vector2D position, int speed, Rectangle rectangle) {
        this.scene = scene;
        this.projectileId = projectileId;
        this.owner = owner;
        this.skillId = skillId;
        this.position = position;
        this.speed = speed;
        this.rectangle = rectangle;
    }

    @Override
    public void updatePosition() {
        Vector2D end = getTargetPosition();
        Vector2D subtract = end.subtract(position);
        double distance = subtract.getNorm();
        double minSpeed = Math.min(speed, distance);
        Vector2D velocity = subtract.scalarMultiply(minSpeed / distance);
        this.position.add(velocity);
        // 同步修改rectangle信息
        int length = scene.battleSceneHelper().configHelper().getProjectileConfigById(projectileId).getLength();
        Vector2D rectangleEnd = subtract.scalarMultiply(length / distance);
        this.rectangle = new Rectangle(position, rectangleEnd, rectangle.getHalfWidth());
    }

    /**
     * 获取子弹目标所在位置
     * @return 子弹目标所在位置
     */
    protected abstract Vector2D getTargetPosition();

    @Override
    public void tryHit() {
        QuadTree<FightUnit> distributed = scene.distributed();
        List<FightUnit> units = distributed.retrieve(rectangle);
        // 碰撞检测
        List<FightUnit> hitUnits = tryHit0(units);
        if (!hitUnits.isEmpty()) {
            // 执行命中效果
            FightUnit caster = scene.getFightUnit(owner);
            GeneralSkillConfig config = scene.battleSceneHelper().configHelper().getGeneralSkillConfigById(skillId);
            for (FightUnit target : hitUnits) {
                for (Effect<Buff> effect : config.getHitEffects()) {
                    effect.onAction(caster, target, null);
                }
            }
            afterExecHitEffect();
        }
    }

    /**
     * 执行命中效果后逻辑
     */
    protected abstract void afterExecHitEffect();

    /**
     * 碰撞检测
     * @param units 检测检测目标
     * @return 命中对象集
     */
    protected abstract List<FightUnit> tryHit0(List<FightUnit> units);

    @Override
    public boolean isInvalid(long curRound) {
        return checkFinish();
    }

    @Override
    public void registerEventReceiverIfNecessary() {
        // todo 监听移动事件,查看是否有碰撞
    }


}
