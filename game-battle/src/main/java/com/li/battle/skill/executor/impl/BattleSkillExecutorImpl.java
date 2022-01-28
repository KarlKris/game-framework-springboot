package com.li.battle.skill.executor.impl;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.skill.config.SkillStorage;
import com.li.battle.skill.executor.BattleSkillExecutor;
import com.li.battle.skill.model.BattleSkillCtx;
import com.li.battle.skill.processor.SkillAbilityProcessor;
import org.apache.commons.collections4.map.MultiValueMap;

/**
 * 技能执行器
 * @author li-yuanwen
 * @date 2021/10/20
 */
public class BattleSkillExecutorImpl implements BattleSkillExecutor {

    /** 技能库 **/
    private SkillStorage skillStorage;
    /** 技能执行逻辑处理器 **/
    private final MultiValueMap<Integer, SkillAbilityProcessor> executorHolder = new MultiValueMap<>();


    @Override
    public boolean execute(BattleSkillCtx skillCtx, BattleScene scene) {
        int skillId = skillCtx.getSkill().getSkillId();
        for (SkillAbilityProcessor processor : executorHolder.getCollection(skillId)) {
            processor.spell();
        }
        return false;
    }
}
