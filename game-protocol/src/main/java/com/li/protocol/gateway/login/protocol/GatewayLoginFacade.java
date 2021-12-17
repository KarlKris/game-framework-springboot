package com.li.protocol.gateway.login.protocol;


import com.li.network.anno.InBody;
import com.li.network.anno.SocketController;
import com.li.network.anno.SocketMethod;
import com.li.network.session.PlayerSession;
import com.li.protocol.gateway.login.dto.ReqGatewayCreateAccount;
import com.li.protocol.gateway.login.dto.ReqGatewayLoginAccount;

/**
 * @author li-yuanwen
 * 网关服登录
 */
@SocketController(module = GatewayLoginModule.MODULE)
public interface GatewayLoginFacade {


    /**
     * 创建账号
     * @param session Session
     * @param reqGatewayCreateAccount 相关参数
     */
    @SocketMethod(id = GatewayLoginModule.GAME_SERVER_CREATE)
    Long create(PlayerSession session
            , @InBody ReqGatewayCreateAccount reqGatewayCreateAccount);


    /**
     * 账号登录
     * @param session Session
     * @param reqGatewayLoginAccount 相关参数
     */
    @SocketMethod(id = GatewayLoginModule.GAME_SERVER_LOGIN)
    Long login(PlayerSession session
            , @InBody ReqGatewayLoginAccount reqGatewayLoginAccount);




}
