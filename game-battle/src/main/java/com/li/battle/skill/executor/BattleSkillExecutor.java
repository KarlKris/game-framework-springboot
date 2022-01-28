package com.li.battle.skill.executor;

import com.li.battle.core.scene.BattleScene;
import com.li.battle.skill.model.BattleSkillCtx;

/**
 * 战斗单元释放的技能执行器(会放入到技能容器中)
 * @author li-yuanwen
 * @date 2021/10/19
 */
public interface BattleSkillExecutor {

    /**
     * 执行技能效果
     * @param skillCtx 技能上下文
     * @param scene 战斗场景
     * @return true 技能失效，移除容器
     */
    boolean execute(BattleSkillCtx skillCtx, BattleScene scene);

}
