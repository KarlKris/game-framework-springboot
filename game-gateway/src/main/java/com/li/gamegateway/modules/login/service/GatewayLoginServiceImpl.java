package com.li.gamegateway.modules.login.service;

import com.li.gameremote.modules.login.game.GameServerLoginFacade;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.service.rpc.RpcService;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author li-yuanwen
 */
@Service
@Slf4j
public class GatewayLoginServiceImpl implements GatewayLoginService {

    @Autowired
    private RpcService rpcService;
    @Autowired
    private SessionManager sessionManager;

    @Override
    public void create(Session session, String account, int channel, int serverId) {
        GameServerLoginFacade sendProxy = rpcService.getSendProxy(GameServerLoginFacade.class, String.valueOf(serverId));
        Response<Long> response = sendProxy.create(null, account, channel);
        Long identity = response.getContent();
        // 绑定身份
        sessionManager.bindIdentity(session, identity, false);

        if (log.isDebugEnabled()) {
            log.debug("网关服请求游戏服[{}]创建账号成功,绑定session[{},{}]", serverId, session.getSessionId(), identity);
        }
    }

    @Override
    public void login(Session session, String account, int channel, int serverId) {
        GameServerLoginFacade sendProxy = rpcService.getSendProxy(GameServerLoginFacade.class, String.valueOf(serverId));
        Response<Long> response = sendProxy.login(null, account, channel);
        Long identity = response.getContent();
        // 绑定身份
        sessionManager.bindIdentity(session, identity, false);

        if (log.isDebugEnabled()) {
            log.debug("网关服请求游戏服[{}]登录账号成功,绑定session[{},{}]", serverId, session.getSessionId(), identity);
        }
    }
}
