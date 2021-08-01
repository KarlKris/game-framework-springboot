package com.li.gamesocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author li-yuanwen
 * @date 2021/8/1 10:08
 * 消息序号管理
 **/
@Component
@Slf4j
public class SerialNumberManager {

    /** 消息序号生成器 **/
    private final AtomicLong snGenerator = new AtomicLong(0);

    /** 获取下一个消息序号 **/
    public long nextSn() {
        return snGenerator.incrementAndGet();
    }

}
