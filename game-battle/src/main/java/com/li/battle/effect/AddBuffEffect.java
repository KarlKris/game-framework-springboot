package com.li.battle.effect;

import com.li.battle.ConfigHelper;
import com.li.battle.buff.BuffFactory;
import com.li.battle.buff.core.Buff;
import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.FightUnit;
import com.li.battle.core.unit.IPosition;
import com.li.battle.resource.BuffConfig;
import com.li.battle.resource.SelectorConfig;
import com.li.battle.selector.Selector;
import com.li.battle.selector.SelectorResult;
import com.li.battle.skill.BattleSkill;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 添加Buff效果
 * @author li-yuanwen
 * @date 2022/5/25
 */
@Getter
@Slf4j
public class AddBuffEffect implements Effect<Buff> {

    /** buff标识 **/
    private int buffId;

    @Override
    public void onAction(FightUnit unit) {
        BattleScene battleScene = unit.getScene();
        BattleSceneHelper helper = battleScene.getBattleSceneHelper();
        ConfigHelper configHelper = helper.getConfigHelper();
        BuffConfig config = configHelper.getBuffConfigById(buffId);

        BuffFactory buffFactory = helper.getBufferFactory();

        // 存在多个buff选择器时,按顺序选择目标至其一目标集不为空而终
        for (int selectorId : config.getSelectorIds()) {

            SelectorConfig selectorConfig = configHelper.getSelectorConfigById(selectorId);
            Selector selector = helper.getSelectorHolder().getSelectorByType(selectorConfig.getType());
            SelectorResult result = selector.select(unit, selectorConfig);
            List<IPosition> results = result.getResults();
            if (results.isEmpty()) {
                continue;
            }

            for (IPosition position : results) {
                if (!(position instanceof FightUnit)) {
                    log.warn("添加Buff效果,buffId:{} 的选择器结果非FightUnit,检查配置", buffId);
                    continue;
                }

                FightUnit target = (FightUnit) position;
                Buff buff = buffFactory.newInstance(unit, target, config, -1);
                // todo Before_Buff_Awake_Event事件

                if (!buff.isManualExpire()) {
                    // todo Buff容器内添加,同时判断是否往EventDispatcher注册

                }


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
