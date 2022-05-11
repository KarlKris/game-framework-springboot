package com.li.protocol.gateway.login.protocol;


import com.li.network.anno.InBody;
import com.li.network.anno.Session;
import com.li.network.anno.SocketController;
import com.li.network.anno.SocketMethod;
import com.li.network.session.PlayerSession;
import com.li.protocol.gateway.login.vo.ReqGatewayCreateAccount;
import com.li.protocol.gateway.login.vo.ReqGatewayLoginAccount;

/**
 * @author li-yuanwen
 * 网关服登录
 */
@SocketController(module = GatewayLoginModule.MODULE)
public interface GatewayLoginController {


    /**
     * 创建账号
     * @param session Session
     * @param reqGatewayCreateAccount 相关参数
     * @return 玩家唯一标识
     */
    @SocketMethod(id = GatewayLoginModule.CREATE_ACCOUNT, isSyncMethod = false)
    void create(@Session PlayerSession session
            , @InBody ReqGatewayCreateAccount reqGatewayCreateAccount);


    /**
     * 账号登录
     * @param session Session
     * @param reqGatewayLoginAccount 相关参数
     * @return 玩家唯一标识
     */
    @SocketMethod(id = GatewayLoginModule.LOGIN_ACCOUNT, isSyncMethod = false)
    void login(@Session PlayerSession session
            , @InBody ReqGatewayLoginAccount reqGatewayLoginAccount);




}
