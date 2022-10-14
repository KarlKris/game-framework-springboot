package com.li.battle.service;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.battle.core.BattleSceneHelper;
import com.li.battle.core.map.*;
import com.li.battle.core.scene.*;
import com.li.battle.resource.MapConfig;
import com.li.common.concurrent.*;
import com.li.common.resource.anno.ResourceInject;
import com.li.common.resource.storage.ResourceStorage;
import org.springframework.beans.factory.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author li-yuanwen
 * @date 2022/6/7
 */
@Service
public class BattleServiceImpl implements BattleService, InitializingBean, DisposableBean {

    private final AtomicLong idGenerator = new AtomicLong(0);

    private RunnableLoopGroup group;

    @Resource
    private BattleSceneHelper battleSceneHelper;
    @ResourceInject
    private ResourceStorage<Integer, MapConfig> mapStorage;

    private final Map<Integer, SceneMap> cache = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        group = new MultiThreadRunnableLoopGroup(1, new NamedThreadFactory("战斗线程-", false));
    }

    @Override
    public void destroy() throws Exception {
        group.shutdownGracefully();
    }

    @Override
    public BattleScene createScene(int mapId) {
        SceneMap sceneMap = cache.computeIfAbsent(mapId, k-> {
            MapConfig config = mapStorage.getResource(mapId);
            return new DefaultSceneMap(config);
        });
       return new MultipleFightBattleScene(idGenerator.incrementAndGet(), sceneMap, group.next(), battleSceneHelper);
    }
}
