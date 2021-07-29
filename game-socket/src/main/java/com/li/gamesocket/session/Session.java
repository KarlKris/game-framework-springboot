package com.li.gamesocket.session;

import io.netty.channel.Channel;

/**
 * @author li-yuanwen
 * 连接Session封装
 */
public class Session {

    /** session标识 **/
    private int sessionId;
    /** 身份标识 **/
    private long identity;
    /** channel **/
    private Channel channel;

}
