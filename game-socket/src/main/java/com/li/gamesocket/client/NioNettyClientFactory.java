package com.li.gamesocket.client;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * NioNettyClient 工厂
 */
@Component
public class NioNettyClientFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 构建客户端
     * @param ip ip地址
     * @param port 端口
     * @return 客户端
     */
    public NioNettyClient newInstance(String ip, int port) {
        return null;
    }
}
