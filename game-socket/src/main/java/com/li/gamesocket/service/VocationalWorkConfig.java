package com.li.gamesocket.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author li-yuanwen
 * @date 2021/8/1 10:28
 * 业务相关参数
 **/
@Configuration
@Getter
public class VocationalWorkConfig {

    /** 单个单线程池队列最大长度 **/
    @Value("${netty.server.single.threadPool.maxQueueLength:5000}")
    private int maxQueueLength;
    /** 消息体进行压缩的长度值 **/
    @Value("${netty.server.body.zip.length:5000}")
    private int bodyZipLength;

}
