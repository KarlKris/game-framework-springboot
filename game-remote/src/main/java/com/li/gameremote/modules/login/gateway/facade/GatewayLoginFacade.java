package com.li.gameremote.modules.login.gateway.facade;

import com.li.gamesocket.anno.InBody;
import com.li.gamesocket.anno.SocketMethod;
import com.li.gamesocket.anno.SocketController;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.service.session.PlayerSession;

/**
 * @author li-yuanwen
 * 网关服登录
 */
@SocketController(module = GatewayLoginModule.MODULE)
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
    @SocketMethod(command = GatewayLoginModule.GAME_SERVER_CREATE)
    Response<Object> create(PlayerSession session
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
    @SocketMethod(command = GatewayLoginModule.GAME_SERVER_LOGIN)
    Response<Object> login(PlayerSession session
            , @InBody(name = "account") String account
            , @InBody(name = "channel") int channel
            , @InBody(name = "serverId") int serverId
            , @InBody(name = "timestamp") int timestamp
            , @InBody(name = "sign") String sign);




}
