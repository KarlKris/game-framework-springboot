package com.li.client.handler;

import com.li.network.message.SocketProtocol;

/**
 * 协议响应处理器
 * @author li-yuanwen
 * @date 2022/5/10
 */
public interface ProtocolResponseBodyHandler<T> {


    /**
     * 负责的协议
     * @return 协议
     */
    SocketProtocol[] getSocketProtocol();

    /**
     * 协议响应体处理
     * @param protocol 请求协议
     * @param responseProtocol 响应的协议(协议报错时,协议不同)
     * @param responseBody 协议体
     */
    void handle(SocketProtocol protocol, SocketProtocol responseProtocol, T responseBody);


}
