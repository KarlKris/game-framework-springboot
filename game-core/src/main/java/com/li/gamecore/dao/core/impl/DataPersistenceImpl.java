package com.li.gamecore.dao.core.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import com.li.gamecommon.event.DataBaseCloseEvent;
import com.li.gamecommon.thread.MonitoredScheduledThreadPoolExecutor;
import com.li.gamecore.dao.AbstractEntity;
import com.li.gamecore.dao.core.DataAccessor;
import com.li.gamecore.dao.core.DataPersistence;
import com.li.gamecore.dao.model.DataStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 数据库回写实现
 * @author li-yuanwen
 * @date 2022/1/25
 */
@Slf4j
@Component
@ConditionalOnBean(DataAccessor.class)
public class DataPersistenceImpl implements DataPersistence, ApplicationListener<DataBaseCloseEvent> {

    /** 待回写队列 **/
    private final LinkedBlockingQueue<AbstractEntity<?>> queue = new LinkedBlockingQueue<>();
    /** 回写线程池 **/
    private final ScheduledFuture<?> scheduledFuture;
    /** 数据库访问 **/
    private final DataAccessor dataAccessor;

    public DataPersistenceImpl(@Autowired DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
        this.scheduledFuture = new MonitoredScheduledThreadPoolExecutor(1
                , new NamedThreadFactory("数据库回写线程", false))
                .scheduleWithFixedDelay(this::consume, 5, 1, TimeUnit.SECONDS);
    }

    @Override
    public <PK extends Comparable<PK> & Serializable> void commit(AbstractEntity<PK> entity) {
        if (entity.isDeleteStatus() || entity.commit()) {
            queue.offer(entity);
        }
    }

    /** 消费队列 **/
    private void consume() {
        int size = this.queue.size();
        for (int i = 0; i < size; i++) {
            AbstractEntity<?> entity = queue.poll();
            if (entity == null) {
                continue;
            }
            if (entity.isDeleteStatus()) {
                dataAccessor.remove(entity);
            } else if (entity.swap(DataStatus.MODIFY.getCode(), DataStatus.INIT.getCode())) {
                dataAccessor.update(entity);
            }
        }
    }

    @Override
    public void onApplicationEvent(DataBaseCloseEvent event) {
        log.warn("准备停止数据库回写线程,做最后的回写");
        this.scheduledFuture.cancel(false);
        consume();
        log.warn("数据库回写线程停止");
    }
}
