package com.li.battle.skill.processor;

import com.li.battle.skill.model.SkillStage;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.EnumMap;
import java.util.Map;

/**
 * SkillProcessor持有对象
 * @author li-yuanwen
 * @date 2022/5/19
 */
@Component
public class SkillProcessorHolder {


    @Resource
    private ApplicationContext applicationContext;


    private final Map<SkillStage, SkillProcessor<?>> processorHolder = new EnumMap<>(SkillStage.class);

    @PostConstruct
    private void initialize() {
        for (SkillProcessor<?> processor : applicationContext.getBeansOfType(SkillProcessor.class).values()) {
            SkillProcessor<?> old = processorHolder.put(processor.getSkillType(), processor);
            if (old != null) {
                throw new BeanInitializationException("存在多个相同技能阶段效果执行器:" + processor.getSkillType().name());
            }
        }
    }


    public SkillProcessor<?> getSkillProcessor(SkillStage skillStage) {
        return processorHolder.get(skillStage);
    }

}
