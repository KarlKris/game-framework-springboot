package com.li.battle.skill.executor;

import com.li.battle.core.unit.FightUnit;
import com.li.battle.resource.SkillConfig;
import com.li.battle.selector.SelectParam;
import com.li.battle.selector.SelectorResult;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillType;
import com.li.battle.skill.handler.SkillHandler;
import com.li.common.resource.anno.ResourceInject;
import com.li.common.resource.storage.ResourceStorage;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 战斗单元释放的技能执行器(会放入到技能容器中)
 * @author li-yuanwen
 * @date 2021/10/19
 */
@Component
public class BattleSkillExecutor {


    @Resource
    private ApplicationContext applicationContext;

    @ResourceInject
    private ResourceStorage<Integer, SkillConfig> storage;

    private final List<SkillHandler> handlerHolder = new LinkedList<>();

    private final Map<Integer, Integer> skillDurationCache = new HashMap<>();

    @PostConstruct
    private void initialize() {
        Map<SkillType, SkillHandler> temp = new HashMap<>(2);
        for (SkillHandler processor : applicationContext.getBeansOfType(SkillHandler.class).values()) {
            SkillHandler old = temp.put(processor.getSkillType(), processor);
            if (old != null) {
                throw new BeanInitializationException("存在多个相同类型技能效果执行器:" + processor.getSkillType().name());
            }
        }
        handlerHolder.addAll(temp.values());
    }


    /**
     * 执行技能效果
     * @param skill 技能上下文
     */
    public void execute(BattleSkill skill) {
        SkillConfig config = storage.getResource(skill.getSkillId());
        for (SkillHandler skillHandler : handlerHolder) {
            if (!SkillType.belongTo(config.getType(), skillHandler.getSkillType())) {
                continue;
            }
            skillHandler.handle(skill);
        }
    }


    /**
     * 技能选择目标
     * @param caster 技能释放方
     * @param config 技能配置
     * @param param 选择参数
     * @return 选择结果
     */
    public SelectorResult select(FightUnit caster, SkillConfig config, SelectParam param) {
        for (SkillHandler skillHandler : handlerHolder) {
            if (!SkillType.belongTo(config.getType(), skillHandler.getSkillType())) {
                continue;
            }
            return skillHandler.select(caster, config, param);
        }
        // 空目标
        return SelectorResult.EMPTY;
    }


    /**
     * 计算技能的持续时间
     * @param config 技能配置
     * @return 持续时间
     */
    public int calculateSkillDuration(SkillConfig config) {
        Integer time = skillDurationCache.get(config.getId());
        if (time != null) {
            return time;
        }
        for (SkillHandler skillHandler : handlerHolder) {
            if (!SkillType.belongTo(config.getType(), skillHandler.getSkillType())) {
                continue;
            }
            time =  skillHandler.calculateDurationTime(config);
            skillDurationCache.put(config.getId(), time);
        }
        return time;
    }


}
