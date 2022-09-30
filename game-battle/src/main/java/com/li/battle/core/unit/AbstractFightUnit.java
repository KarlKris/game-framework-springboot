package com.li.battle.core.unit;

import com.li.battle.buff.*;
import com.li.battle.buff.core.Buff;
import com.li.battle.collision.QuadTree;
import com.li.battle.core.*;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.event.core.UnitMoveEvent;
import com.li.battle.resource.SkillConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽象战斗单元
 * @author li-yuanwen
 * @date 2022/5/6
 */
@Slf4j
public abstract class AbstractFightUnit implements FightUnit {


    /** 唯一标识 **/
    private final long id;
    /** 类型 **/
    private final UnitType type;
    /** 所属阵营 **/
    private final CampType campType;
    /** 范围半径 **/
    private final double radius;
    /** 当前状态 **/
    private UnitState state;
    /** 当前位置 **/
    private Vector2D position;
    /** 目标位置 **/
    private Vector2D moveTargetPosition;

    /** 最高移速(一回合内的移动距离) **/
    private final int maxSpeed;
    /** 当前速度 **/
    private Vector2D velocity;
    /** 上次徘徊的随机点 **/
    private Vector2D localWander;
    /** 路径 **/
    private List<Vector2D> ways;
    /** 路径下标 **/
    private int wayIndex;

    /** 所属场景 **/
    private BattleScene scene;

    // 属性一分为三 个人基础属性,战斗中因各种效果而修改的属性值,场景全局属性变更

    /** 基础属性 **/
    private final Map<Attribute, Long> coreAttributes;
    /** 场景内属性变更值 **/
    private final Map<Attribute, Long> externalAttributes;


    /** 技能信息 **/
    private final List<Skill> skills;
    /** 单位身上的buff **/
    private List<Buff> buffs;

    public AbstractFightUnit(long id, UnitType type, CampType campType, double radius, int maxSpeed
            , Vector2D position, Map<Attribute, Long> coreAttributes, List<Skill> skills) {
        this.id = id;
        this.type = type;
        this.campType = campType;
        this.radius = radius;
        this.maxSpeed = maxSpeed;
        this.position = position;

        this.coreAttributes = coreAttributes;
        this.externalAttributes = new HashMap<>(16);
        this.skills = skills;

        this.state = UnitState.NORMAL;
        this.velocity = Vector2D.ZERO;
    }

    @Override
    public UnitType getUnitType() {
        return type;
    }

    @Override
    public CampType getCampType() {
        return campType;
    }

    @Override
    public long getAttributeValue(Attribute attribute) {
        long coreValue = coreAttributes.getOrDefault(attribute, 0L);
        long externalValue = externalAttributes.getOrDefault(attribute, 0L);
        long globalValue = scene.getGlobalAttribute(attribute);

        long value = coreValue + externalValue + globalValue;
        return value == 0 ? attribute.getDefaultValue() : value;
    }

    @Override
    public void modifyAttribute(Attribute attribute, Long value) {
        this.externalAttributes.merge(attribute, value, Long::sum);
    }

    @Override
    public boolean isDead() {
        return getAttributeValue(Attribute.CUR_HP) <= 0;
    }

    @Override
    public List<Buff> getBuffById(int buffId) {
        if (buffs == null) {
            return null;
        }
        return buffs.stream().filter(buff -> buff.getBuffId() == buffId).collect(Collectors.toList());
    }

    @Override
    public Buff getBuffByIdAndCaster(final long caster, final int buffId) {
        if (buffs == null) {
            return null;
        }
        return buffs.stream().filter(buff -> buff.getBuffId() == buffId && buff.getCaster() == caster).findFirst().orElse(null);
    }

    @Override
    public Collection<Buff> getAllBuffs() {
        if (buffs == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableCollection(buffs);
    }

    @Override
    public void addBuff(Buff buff) {
        if (buffs == null) {
            buffs = new ArrayList<>(4);
        }
        buffs.add(buff);
    }

    @Override
    public void removeBuff(Buff buff) {
        if (buffs == null) {
            return;
        }
        buffs.remove(buff.getBuffId());
    }

    @Override
    public final void onHurt(long dmg) {
        if (buffs == null) {
            modifyAttribute(Attribute.CUR_HP, -dmg);
            return;
        }

        // 扣除护盾
        long decShieldValue = 0;
        for (Buff buff : buffs) {
            if (dmg <= 0) {
                break;
            }
            if (buff.getConfig().getType() != BuffType.SHIELD) {
                continue;
            }
            BuffContext buffContext = buff.buffContext();
            int shieldValue = buffContext.getShieldValue();
            if (shieldValue >= dmg) {
                buffContext.setShieldValue((int) (shieldValue - dmg));
                decShieldValue += dmg;
                dmg = 0;
            } else {
                buffContext.setShieldValue(0);
                decShieldValue += shieldValue;
                dmg -= shieldValue;
            }
        }

        if (decShieldValue > 0) {
            modifyAttribute(Attribute.SHIELD, -decShieldValue);
        }

        if (dmg > 0) {
            modifyAttribute(Attribute.CUR_HP, -dmg);
        }

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
    public void updateVelocity(Vector2D velocity) {
        this.velocity = velocity;
    }

    @Override
    public void updatePosition(Vector2D position) {
        QuadTree<FightUnit> distributed = getScene().distributed();
        distributed.remove(this);

        UnitMoveEvent event = new UnitMoveEvent(id, this.position, position);
        this.position = position;

        distributed.insert(this);

        getScene().eventDispatcher().dispatch(event);
    }

    @Override
    public void setMoveTargetPosition(Vector2D position) {
        this.moveTargetPosition = position;
        modifyState(UnitState.MOVING);
    }

    @Override
    public Vector2D getMoveTargetPosition() {
        return moveTargetPosition;
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

    @Override
    public List<Skill> getSkills() {
        return skills;
    }

    @Override
    public Skill getSkillById(int skillId) {
        return skills.stream().filter(skill -> skill.getSkillId() == skillId).findFirst().orElse(null);
    }

    @Override
    public void coolDownSkill(int skillId) {
        Skill skill = getSkillById(skillId);
        if (skill == null) {
            return;
        }
        BattleScene scene = getScene();
        SkillConfig skillConfig = scene.battleSceneHelper().configHelper().getSkillConfigById(skillId);
        skill.afterSkillExecuted(skillConfig, scene);
    }

    @Override
    public BattleScene getScene() {
        return scene;
    }

    @Override
    public void enterScene(BattleScene scene) {
        this.scene = scene;
    }

    @Override
    public void leaveScene() {
        this.scene = null;
        this.externalAttributes.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFightUnit that = (AbstractFightUnit) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
