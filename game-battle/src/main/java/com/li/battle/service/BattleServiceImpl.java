package com.li.battle.service;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.scene.BattleScene;
import com.li.battle.core.scene.impl.MultipleFightBattleScene;
import com.li.battle.core.scene.map.DefaultSceneMap;
import com.li.battle.core.scene.map.SceneMap;
import com.li.battle.resource.MapConfig;
import com.li.common.resource.anno.ResourceInject;
import com.li.common.resource.storage.ResourceStorage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author li-yuanwen
 * @date 2022/6/7
 */
@Service
public class BattleServiceImpl implements BattleService, InitializingBean, DisposableBean {

    private final AtomicLong idGenerator = new AtomicLong(0);

    private ScheduledExecutorService executorService;

    @Resource
    private BattleSceneHelper battleSceneHelper;
    @ResourceInject
    private ResourceStorage<Integer, MapConfig> mapStorage;

    private final Map<Integer, SceneMap> cache = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("战斗线程-", false));
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }

    @Override
    public BattleScene createScene(int mapId) {
        SceneMap sceneMap = cache.computeIfAbsent(mapId, k-> {
            MapConfig config = mapStorage.getResource(mapId);
            return new DefaultSceneMap(config);
        });
       return new MultipleFightBattleScene(idGenerator.incrementAndGet(), sceneMap, executorService, battleSceneHelper);
    }
}
