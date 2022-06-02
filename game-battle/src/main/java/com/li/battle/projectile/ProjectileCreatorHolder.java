package com.li.battle.projectile;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 子弹创建器持有对象
 * @author li-yuanwen
 * @date 2022/6/2
 */
@Component
public class ProjectileCreatorHolder {

    @Resource
    private ApplicationContext applicationContext;

    private Map<ProjectileType, ProjectileCreator> projectileCreatorHolder;


    @PostConstruct
    private void initialize() {
        Collection<ProjectileCreator> creators = applicationContext.getBeansOfType(ProjectileCreator.class).values();
        projectileCreatorHolder = new HashMap<>(creators.size());
        for (ProjectileCreator creator : creators) {
            ProjectileCreator old = projectileCreatorHolder.putIfAbsent(creator.getType(), creator);
            if (old != null) {
                throw new BeanInitializationException("存在多个相同类型子弹创建器:" + creator.getType().name());
            }
        }
    }


    public ProjectileCreator getProjectileCreator(ProjectileType type) {
        return projectileCreatorHolder.get(type);
    }


}
