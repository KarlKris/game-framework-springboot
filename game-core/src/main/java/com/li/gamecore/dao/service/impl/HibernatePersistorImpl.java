package com.li.gamecore.dao.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.gamecommon.event.DataBaseCloseEvent;
import com.li.gamecommon.thread.MonitoredScheduledThreadPoolExecutor;
import com.li.gamecore.dao.IEntity;
import com.li.gamecore.dao.model.PersistElement;
import com.li.gamecore.dao.model.PersistType;
import com.li.gamecore.dao.service.Accessor;
import com.li.gamecore.dao.service.Persistor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 * 基于Hibernate的持久化接口
 */
@Component
@Slf4j
public class HibernatePersistorImpl implements Persistor, ApplicationListener<DataBaseCloseEvent> {

    /** 队列 **/
    private final LinkedBlockingQueue<PersistElement> queue = new LinkedBlockingQueue<>();
    /** 回写线程池 **/
    private final ScheduledFuture<?> scheduledFuture;
    /** 数据库访问 **/
    private final Accessor accessor;

    @Autowired
    public HibernatePersistorImpl(Accessor accessor) {
        this.accessor = accessor;
        this.scheduledFuture = new MonitoredScheduledThreadPoolExecutor(1
                , new NamedThreadFactory("数据库回写线程", false))
                .scheduleWithFixedDelay(this::persistAll, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public boolean immediatePersist(PersistType type, IEntity entity) {
        return persist(new PersistElement(type, entity));
    }

    @Override
    public void asynPersist(PersistType type, IEntity entity) {
        queue.offer(new PersistElement(type, entity));
    }

    private void persistAll() {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            try {
                PersistElement element = queue.poll();
                if (element == null) {
                    break;
                }
                persist(element);
            } catch (Exception e) {
                log.error("持久化线程发生未知异常", e);
            }
        }
    }

    private boolean persist(PersistElement element) {
        boolean success = false;
        switch (element.getType()) {
            case CREATE: {
                accessor.create(element.getEntity());
                success = true;
                break;
            }
            case REMOVE: {
                accessor.remove(element.getEntity());
                success = true;
                break;
            }
            case UPDATE: {
                accessor.update(element.getEntity());
                success = true;
                break;
            }
            default: {
                log.warn("不支持持久化类型[{}]", element.getType().name());
            }
        }
        return success;
    }

    @Override
    public void onApplicationEvent(DataBaseCloseEvent event) {

        log.warn("准备停止数据库回写线程,做最后的回写");

        this.scheduledFuture.cancel(false);
        persistAll();

        log.warn("数据库回写线程停止");

    }
}
