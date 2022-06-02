package com.li.battle;

import com.li.battle.resource.*;
import com.li.common.resource.anno.ResourceInject;
import com.li.common.resource.storage.ResourceStorage;
import org.springframework.stereotype.Component;

/**
 * 战斗相关配置类
 * @author li-yuanwen
 * @date 2022/5/24
 */
@Component
public class ConfigHelper {

    @ResourceInject
    private ResourceStorage<Integer, ActivateSkillConfig> activateSkillStorage;
    @ResourceInject
    private ResourceStorage<Integer, ChannelSkillConfig> channelSkillStorage;
    @ResourceInject
    private ResourceStorage<Integer, GeneralSkillConfig> generalSkillStorage;
    @ResourceInject
    private ResourceStorage<Integer, SkillConfig> skillStorage;
    @ResourceInject
    private ResourceStorage<Integer, ToggleSkillConfig> toggleSkillStorage;
    @ResourceInject
    private ResourceStorage<Integer, BuffConfig> buffStorage;
    @ResourceInject
    private ResourceStorage<Integer, SelectorConfig> selectorStorage;
    @ResourceInject
    private ResourceStorage<Integer, TriggerConfig> triggerStorage;
    @ResourceInject
    private ResourceStorage<Integer, ProjectileConfig> projectileStorage;


    public ActivateSkillConfig getActivateSkillConfigById(Integer id) {
        return activateSkillStorage.getResource(id);
    }

    public ChannelSkillConfig getChannelSkillConfigById(Integer id) {
        return channelSkillStorage.getResource(id);
    }

    public GeneralSkillConfig getGeneralSkillConfigById(Integer id) {
        return generalSkillStorage.getResource(id);
    }

    public SkillConfig getSkillConfigById(Integer id) {
        return skillStorage.getResource(id);
    }

    public ToggleSkillConfig getToggleSkillConfigById(Integer id) {
        return toggleSkillStorage.getResource(id);
    }

    public BuffConfig getBuffConfigById(Integer id) {
        return buffStorage.getResource(id);
    }

    public SelectorConfig getSelectorConfigById(Integer id) {
        return selectorStorage.getResource(id);
    }

    public TriggerConfig getTriggerConfigById(Integer id) {
        return triggerStorage.getResource(id);
    }

    public ProjectileConfig getProjectileConfigById(Integer id) {
        return projectileStorage.getResource(id);
    }


}
