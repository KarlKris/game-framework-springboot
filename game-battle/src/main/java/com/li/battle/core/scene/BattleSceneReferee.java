package com.li.battle.core.scene;

import com.li.battle.core.Skill;
import com.li.battle.core.context.DefaultAlterContext;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SkillConfig;
import com.li.battle.selector.SelectParam;
import com.li.battle.selector.SelectorResult;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillType;
import com.li.battle.skill.executor.BattleSkillExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * 战斗场景内的裁判(用于提供对外界的接口)
 * @author li-yuanwen
 * @date 2022/5/31
 */
public class BattleSceneReferee {

    /** 关联的战斗场景 **/
    private final BattleScene scene;

    public BattleSceneReferee(BattleScene scene) {
        this.scene = scene;
    }

    public CompletableFuture<Void> useSkill(long unitId, int skillId, SelectParam param) {
        if (scene.checkDestroy()) {
            throw new RuntimeException("场景已销毁");
        }

        FightUnit fightUnit = scene.getFightUnit(unitId);
        if (fightUnit == null) {
            throw new RuntimeException("战斗单位未在场景中");
        }

        SkillConfig config = scene.battleSceneHelper().configHelper().getSkillConfigById(skillId);
        if (config == null) {
            throw new RuntimeException("战斗单位技能：" + skillId + " 非法");
        }

        if (SkillType.belongTo(config.getType(), SkillType.PASSIVE_SKILL)
                || SkillType.belongTo(config.getType(), SkillType.TOGGLE_SKILL)
                || SkillType.belongTo(config.getType(), SkillType.ACTIVATE_SKILL)) {
            throw new RuntimeException("战斗单位技能：" + skillId + " 是被动/激活类/开关类技能");
        }

        Skill skill = fightUnit.getSkillById(skillId);
        if (skill == null) {
            throw new RuntimeException("战斗单位没有技能：" + skillId);
        }

        if (skill.isCoolDown(scene.getSceneRound())) {
            throw new RuntimeException("战斗单位技能：" + skillId + " 技能正在CD中");
        }

        final DefaultAlterContext context = new DefaultAlterContext(scene);
        return scene.addTask(() -> {
            // 选择目标并验证目标
            BattleSkillExecutor skillExecutor = scene.battleSceneHelper().battleSkillExecutor();
            SelectorResult result = skillExecutor.select(fightUnit, config, param);
            int duration = skillExecutor.calculateSkillDuration(config);
            // 构建BattleSkill
            BattleSkill battleSkill = new BattleSkill(skillId, unitId, result
                    , duration / scene.getRoundPeriod(), param, context);
            scene.skillManager().addBattleSkill(battleSkill);
            return null;
        });

    }
}
