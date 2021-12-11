package com.li.gamesocket.channelhandler.common;

import io.netty.channel.ChannelHandler;

/**
 * @author li-yuanwen
 * 过滤器
 */
public interface NioNettyFilter extends ChannelHandler {

    /**
     * 过滤器名称
     * @return /
     */
    String getName();

}
