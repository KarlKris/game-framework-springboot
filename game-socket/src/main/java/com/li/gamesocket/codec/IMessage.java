package com.li.gamesocket.codec;

/**
 * @author li-yuanwen
 * 自定义协议消息接口
 */
public interface IMessage {

    /**
     * 返回协议头标识
     * @return /
     */
    short getProtocolHeaderIdentity();

}
