package com.li.client.handler;

import com.li.client.controller.MessageController;
import com.li.network.message.SocketProtocol;

import javax.annotation.Resource;

/**
 * 抽象协议响应体内容处理
 * @author li-yuanwen
 * @date 2022/5/10
 */
public abstract class AbstractProtocolResponseBodyHandler<T> implements ProtocolResponseBodyHandler<T> {

    @Resource
    protected MessageController messageController;

    @Override
    public void handle(SocketProtocol protocol, SocketProtocol responseProtocol, T responseBody) {
        if (responseProtocol.equals(protocol)) {
            handle0(responseBody);
        } else {
            error((Long) responseBody);
        }
    }

    /**
     * 协议请求成功地响应体处理
     * @param responseBody 响应体
     */
    protected abstract void handle0(T responseBody);

    /**
     * 协议请求失败的响应体处理
     * @param errorCode 错误码
     */
    protected void error(long errorCode) {
        messageController.addErrorMessage(String.valueOf(errorCode));
    }
}
