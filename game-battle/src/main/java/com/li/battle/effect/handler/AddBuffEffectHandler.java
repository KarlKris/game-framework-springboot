package com.li.battle.effect.handler;

import com.li.battle.buff.*;
import com.li.battle.buff.core.*;
import com.li.battle.core.*;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.unit.*;
import com.li.battle.effect.EffectType;
import com.li.battle.effect.domain.AddBuffEffectParam;
import com.li.battle.effect.source.EffectSource;
import com.li.battle.event.EventDispatcher;
import com.li.battle.event.core.BeforeBuffAwakeEvent;
import com.li.battle.resource.BuffConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 添加buff效果
 * @author li-yuanwen
 * @date 2022/9/22
 */
@Slf4j
@Component
public class AddBuffEffectHandler extends AbstractEffectParamHandler<AddBuffEffectParam> {

    @Override
    public EffectType getType() {
        return EffectType.ADD_BUFF;
    }

    @Override
    public void execute0(EffectSource source, AddBuffEffectParam effectParam) {
        BattleScene battleScene = source.battleScene();

        BattleSceneHelper helper = battleScene.battleSceneHelper();
        EventDispatcher eventDispatcher = battleScene.eventDispatcher();
        ConfigHelper configHelper = helper.configHelper();
        BuffConfig config = configHelper.getBuffConfigById(effectParam.getBuffId());

        FightUnit unit = source.getCaster();

        BuffManager buffManager = battleScene.buffManager();
        for (FightUnit target : source.getTargetUnits()) {
            if (buffManager.isImmuneTag(target, unit.getId(), config.getTag())) {
                // 目标身上存在免疫该Buff的相关Buff
                continue;
            }

            Buff buff = new NormalBuff(config, unit.getId(), target.getId(), source.getSkillId(), battleScene);

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
