package com.li.battle.projectile;

import cn.hutool.core.lang.Pair;
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
    /** 创建子弹回合数 **/
    protected final long createRound;
    /** 子弹销毁标识 **/
    protected boolean destroy;


    AbstractProjectile(BattleScene scene, int projectileId, long owner, int skillId, Vector2D position, int speed, Rectangle rectangle) {
        this.scene = scene;
        this.projectileId = projectileId;
        this.owner = owner;
        this.skillId = skillId;
        this.position = position;
        this.speed = speed;
        this.rectangle = rectangle;
        this.createRound = scene.getSceneRound();
    }

    @Override
    public void updatePosition() {
        Pair<Vector2D, Rectangle> pair = calculateNextPositionAndRectangle();
        this.position = pair.getKey();
        // 同步修改rectangle信息
        this.rectangle = pair.getValue();
    }

    /**
     * 获取子弹目标所在位置
     * @return 子弹目标所在位置
     */
    protected abstract Vector2D getTargetPosition();

    /**
     * 计算子弹下一次位置和碰撞模型
     * @return key:下一次位置 value:碰撞模型
     */
    private Pair<Vector2D, Rectangle> calculateNextPositionAndRectangle() {
        Vector2D end = getTargetPosition();
        Vector2D subtract = end.subtract(position);
        double distance = subtract.getNorm();
        double minSpeed = Math.min(speed, distance);
        Vector2D velocity = subtract.scalarMultiply(minSpeed / distance);
        Vector2D nextPosition = this.position.add(velocity);
        // 同步修改rectangle信息
        int length = scene.battleSceneHelper().configHelper().getProjectileConfigById(projectileId).getLength();
        Vector2D rectangleEnd = nextPosition.add(nextPosition.subtract(position).normalize().scalarMultiply(length));
        Rectangle nextRectangle = new Rectangle(nextPosition, rectangleEnd, this.rectangle.getHalfWidth());

        return new Pair<>(nextPosition, nextRectangle);
    }

    @Override
    public void tryHit() {
        Rectangle nextRectangle = calculateNextPositionAndRectangle().getValue();
        // 碰撞体扩大到下一次的位置
        Rectangle collider = new Rectangle(position, nextRectangle.getEnd(), nextRectangle.getHalfWidth());

        QuadTree<FightUnit> distributed = scene.distributed();
        List<FightUnit> units = distributed.retrieve(collider);
        // 碰撞检测
        List<FightUnit> hitUnits = filter(impactCheck(collider, units));
        if (!hitUnits.isEmpty()) {
            // 执行命中效果
            FightUnit caster = scene.getFightUnit(owner);
            GeneralSkillConfig config = scene.battleSceneHelper().configHelper().getGeneralSkillConfigById(skillId);
            for (Effect<Buff> effect : config.getHitEffects()) {
                effect.onAction(caster, hitUnits, this);
            }
            afterExecHitEffect();
        }
    }

    @Override
    public int getProjectileId() {
        return projectileId;
    }

    /**
     * 执行命中效果后逻辑
     */
    protected abstract void afterExecHitEffect();

    /**
     * 发生碰撞的列表过滤
     * @param units 碰撞列表
     * @return 命中对象集
     */
    private List<FightUnit> filter(List<FightUnit> units) {
        if (units.isEmpty()) {
            return units;
        }
        return filter0(units);
    }

    /**
     * 发生碰撞的列表过滤,给子类实现
     * @param units 碰撞列表
     * @return 命中对象集
     */
    protected List<FightUnit> filter0(List<FightUnit> units) {
        return units;
    }

    /**
     * 碰撞检测
     * @param collider 碰撞体
     * @param toBeTestedList 待检测列表
     * @return 发生碰撞的列表
     */
    private List<FightUnit> impactCheck(Rectangle collider, List<FightUnit> toBeTestedList) {
        // todo 圆形和矩形的碰撞检测
        return toBeTestedList;
    }

    @Override
    public boolean isInvalid(long curRound) {
        return checkFinish();
    }

    /**
     * 设置销毁标识
     */
    protected void destroy() {
        this.destroy = true;
    }

    @Override
    public long getOwner() {
        return owner;
    }

    @Override
    public void makeExpire() {
        destroy();
    }

    @Override
    public void registerEventReceiverIfNecessary() {
        // todo 监听移动事件,查看是否有碰撞
    }


}
