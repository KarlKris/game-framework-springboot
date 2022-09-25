package com.li.battle.skill.processor;

import com.li.battle.skill.SkillStage;
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


    private final Map<SkillStage, SkillStageProcessor<?>> processorHolder = new EnumMap<>(SkillStage.class);

    @PostConstruct
    private void initialize() {
        for (SkillStageProcessor<?> processor : applicationContext.getBeansOfType(SkillStageProcessor.class).values()) {
            SkillStageProcessor<?> old = processorHolder.put(processor.getSkillSatge(), processor);
            if (old != null) {
                throw new BeanInitializationException("存在多个相同技能阶段效果执行器:" + processor.getSkillSatge().name());
            }
        }
    }


    public SkillStageProcessor<?> getSkillProcessor(SkillStage skillStage) {
        return processorHolder.get(skillStage);
    }

}
