package com.li.gameremote.modules.login.gateway;

import com.li.gamesocket.anno.InBody;
import com.li.gamesocket.anno.SocketCommand;
import com.li.gamesocket.anno.SocketModule;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.service.session.Session;

/**
 * @author li-yuanwen
 * 网关服登录
 */
@SocketModule(module = GatewayLoginModule.MODULE)
public interface GatewayLoginFacade {


    /**
     * 创建账号
     * @param session Session
     * @param account 账号
     * @param channel 渠道标识
     * @param serverId 服标识
     * @param timestamp 时间戳(精确到秒)
     * @param sign 签名
     */
    @SocketCommand(command = GatewayLoginModule.GAME_SERVER_CREATE)
    Response<Object> create(Session session
            , @InBody(name = "account") String account
            , @InBody(name = "channel") int channel
            , @InBody(name = "serverId") int serverId
            , @InBody(name = "timestamp") int timestamp
            , @InBody(name = "sign") String sign);


    /**
     * 账号登录
     * @param session Session
     * @param account 账号
     * @param channel 渠道标识
     * @param serverId 服标识
     * @param timestamp 时间戳(精确到秒)
     * @param sign 签名
     */
    @SocketCommand(command = GatewayLoginModule.GAME_SERVER_LOGIN)
    Response<Object> login(Session session
            , @InBody(name = "account") String account
            , @InBody(name = "channel") int channel
            , @InBody(name = "serverId") int serverId
            , @InBody(name = "timestamp") int timestamp
            , @InBody(name = "sign") String sign);




}
