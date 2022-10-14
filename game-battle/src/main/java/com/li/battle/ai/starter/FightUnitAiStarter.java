package com.li.battle.ai.starter;

import com.li.battle.ai.*;
import com.li.battle.ai.action.impl.*;
import com.li.battle.ai.action.move.MovingAction;
import com.li.battle.ai.composite.*;
import com.li.battle.ai.condition.impl.*;
import com.li.battle.ai.condition.move.ConditionIsMovingState;
import com.li.battle.core.unit.FightUnit;

/**
 * @author li-yuanwen
 * @date 2022/9/28
 */
public class FightUnitAiStarter {


    public static BehaviourTree unitAi(FightUnit unit) {
        BehaviourTreeBuilder builder = new BehaviourTreeBuilder();
        builder.unit(unit);
        builder.root(new SelectorBehaviour());

        builder.addBehaviour(new SequenceBehaviour())
                    .addBehaviour(new ConditionIsMovingState())
                        .back()
                    .addBehaviour(new MovingAction())
                        .back()
                    .back()
                .addBehaviour(new SequenceBehaviour())
                    .addBehaviour(new ConditionIsWaitState())
                        .back()
                    .addBehaviour(new SuccessAction())
                        .back()
                    .back()
//                .addBehaviour(new SequenceBehaviour())
//                    .addBehaviour(new ConditionIsHasMoveTarget())
//                        .back()
//                    .addBehaviour(new MoveToTargetAction())
//                        .back()
//                    .back()
                .addBehaviour(new SequenceBehaviour())
                    .addBehaviour(new ConditionIsFreedSkill())
                        .back()
                    .addBehaviour(new ConditionIsNotCoolDown())
                        .back()
                    .addBehaviour(new SelectEnemyAction())
                        .back()
                    .addBehaviour(new ConditionIsEnemyMatchDistance())
                        .back()
                    .addBehaviour(new UseSkillAction())
                        .back()
                    .back()
                .addBehaviour(new SelectNextSkillAction());

        return builder.build();
    }


}
