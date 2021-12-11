package com.li.gamesocket.service.push;

import com.li.gamesocket.protocol.PushResponse;
import com.li.gamesocket.service.protocol.SocketProtocol;
import com.li.gamesocket.service.session.ISession;

/**
 * @author li-yuanwen
 * 收到推送逻辑处理
 */
public interface IPushExecutor {

    /**
     * 推送至外部
     * @param pushResponse 推送内容
     * @param protocol 命令
     */
    void pushToOuter(PushResponse pushResponse, SocketProtocol protocol);

    /**
     * 内网推送
     * @param session 推送目标
     * @param pushResponse 推送内容
     * @param protocol 推送命令
     */
    void pushToInner(ISession session, PushResponse pushResponse, SocketProtocol protocol);

}
