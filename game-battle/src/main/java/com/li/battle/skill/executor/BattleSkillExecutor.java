package com.li.battle.skill.executor;

import com.li.battle.resource.SkillConfig;
import com.li.battle.skill.handler.SkillHandler;
import com.li.battle.skill.BattleSkill;
import com.li.battle.skill.SkillType;
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

    @PostConstruct
    private void initialize() {
        Map<SkillType, SkillHandler> temp = new HashMap<>();
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
     * @return true 技能失效，移除容器
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

}
