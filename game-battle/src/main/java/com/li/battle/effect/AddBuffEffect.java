package com.li.battle.effect;

import com.li.battle.core.ConfigHelper;
import com.li.battle.buff.BuffFactory;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.Skill;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.IPosition;
import com.li.battle.resource.BuffConfig;
import com.li.battle.resource.SelectorConfig;
import com.li.battle.selector.SelectParam;
import com.li.battle.selector.Selector;
import com.li.battle.selector.SelectorResult;
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

        BuffFactory buffFactory = helper.buffFactory();

        SelectorConfig selectorConfig = configHelper.getSelectorConfigById(config.getSelectorId());
        Selector selector = helper.selectorHolder().getSelectorByType(selectorConfig.getType());
        SelectorResult result = selector.select(unit, selectorConfig, SelectParam.EMPTY, 0);
        for (IPosition position : result.getResults()) {
            if (!(position instanceof FightUnit)) {
                log.warn("添加Buff效果,buffId:{} 的选择器结果非FightUnit,检查配置", buffId);
                continue;
            }

            FightUnit target = (FightUnit) position;
            Buff buff = buffFactory.newInstance(unit, target, config, skill.getSkillId());
            // todo Before_Buff_Awake_Event事件

            //  Buff容器内添加,同时判断是否往EventDispatcher注册
            if (!buff.isManualExpire() && battleScene.buffManager().addBuff(buff)) {
                buff.registerEventReceiverIfNecessary();
            }
        }


    }

    @Override
    public void onAction(BattleSkill skill) {
        // todo
    }

    @Override
    public void onAction(Buff buff) {
        // todo
    }
}
