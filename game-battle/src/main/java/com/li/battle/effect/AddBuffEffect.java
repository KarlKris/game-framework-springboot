package com.li.battle.effect;

import com.li.battle.buff.*;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.*;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.event.EventDispatcher;
import com.li.battle.event.core.BeforeBuffAwakeEvent;
import com.li.battle.resource.BuffConfig;
import com.li.battle.skill.BattleSkill;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 添加Buff效果
 * @author li-yuanwen
 * @date 2022/5/25
 */
@Getter
@Slf4j
public class AddBuffEffect extends EffectAdapter<Buff> {

    /** buff标识 **/
    private int buffId;

    @Override
    public void onAction(FightUnit unit, Skill skill) {
        BattleScene battleScene = unit.getScene();
        BattleSceneHelper helper = battleScene.battleSceneHelper();
        ConfigHelper configHelper = helper.configHelper();
        BuffConfig config = configHelper.getBuffConfigById(buffId);

        BuffManager buffManager = battleScene.buffManager();

        BuffFactory buffFactory = helper.buffFactory();
        Buff buff = buffFactory.newInstance(unit, unit, config, skill.getSkillId());

        //  Buff容器内添加,同时判断是否往EventDispatcher注册
        if (buffManager.addBuff(buff)) {
            buff.registerEventReceiverIfNecessary();
        }


    }

    @Override
    public void onAction(BattleSkill skill) {
        BattleScene battleScene = skill.getContext().getScene();
        BattleSceneHelper helper = battleScene.battleSceneHelper();
        EventDispatcher eventDispatcher = battleScene.eventDispatcher();
        ConfigHelper configHelper = helper.configHelper();
        BuffConfig config = configHelper.getBuffConfigById(buffId);

        BuffFactory buffFactory = helper.buffFactory();

        FightUnit unit = battleScene.getFightUnit(skill.getCaster());

        BuffManager buffManager = battleScene.buffManager();
        for (IPosition position : skill.getFinalTargets()) {
            if (!(position instanceof FightUnit)) {
                log.warn("添加Buff效果,buffId:{} 的选择器结果非FightUnit,检查配置", buffId);
                continue;
            }

            FightUnit target = (FightUnit) position;
            if (buffManager.isImmuneTag(target, unit.getId(), config.getTag())) {
                // 目标身上存在免疫该Buff的相关Buff
                continue;
            }

            Buff buff = buffFactory.newInstance(unit, target, config, skill.getSkillId());

            // Before_Buff_Awake_Event事件
            BeforeBuffAwakeEvent event = new BeforeBuffAwakeEvent(buff);
            eventDispatcher.dispatch(event, 0);

            //  Buff容器内添加,同时判断是否往EventDispatcher注册
            if (!buff.isManualExpire() && buffManager.addBuff(buff)) {
                buff.registerEventReceiverIfNecessary();
            }
        }
    }
}
