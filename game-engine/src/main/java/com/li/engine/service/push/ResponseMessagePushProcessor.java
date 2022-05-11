package com.li.engine.service.push;

import com.li.network.message.SocketProtocol;
import com.li.network.session.ISession;

/**
 * 消息响应执行器
 * @author li-yuanwen
 * @date 2022/5/11
 */
public interface ResponseMessagePushProcessor<S extends ISession> {


    /**
     * 消息响应
     * @param session 响应目标
     * @param messageSn 请求消息序号
     * @param protocol 消息协议
     * @param responseBody 消息体内容
     */
    void response(S session, long messageSn, SocketProtocol protocol, Object responseBody);

}
