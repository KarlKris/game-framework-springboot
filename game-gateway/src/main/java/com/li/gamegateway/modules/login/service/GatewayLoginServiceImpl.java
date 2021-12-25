package com.li.gamegateway.modules.login.service;

import com.li.engine.service.rpc.IRpcService;
import com.li.engine.service.session.SessionManager;
import com.li.network.session.ISession;
import com.li.network.session.PlayerSession;
import com.li.protocol.game.login.dto.ReqGameCreateAccount;
import com.li.protocol.game.login.dto.ReqGameLoginAccount;
import com.li.protocol.game.login.protocol.GameServerLoginController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author li-yuanwen
 */
@Service
@Slf4j
public class GatewayLoginServiceImpl implements GatewayLoginService {

    @Resource
    private IRpcService rpcService;
    @Resource
    private SessionManager sessionManager;

    @Override
    public Long create(PlayerSession session, String account, int channel, int serverId) {
        GameServerLoginController sendProxy = rpcService.getSendProxy(GameServerLoginController.class, String.valueOf(serverId));
        Long identity = sendProxy.create(null, new ReqGameCreateAccount(account, channel));
        // 绑定身份
        sessionManager.bindIdentity(session, identity);

        if (log.isDebugEnabled()) {
            log.debug("网关服请求游戏服[{}]创建账号成功,绑定session[{},{}]", serverId, session.getSessionId(), identity);
        }

        return identity;
    }

    @Override
    public Long login(PlayerSession session, String account, int channel, int serverId) {
        GameServerLoginController sendProxy = rpcService.getSendProxy(GameServerLoginController.class, String.valueOf(serverId));
        Long identity = sendProxy.login(null, new ReqGameLoginAccount(account, channel));
        ISession oldSession = sessionManager.bindIdentity(session, identity);
        if (oldSession != null && !Objects.equals(oldSession.getSessionId(), session.getSessionId())) {
            // 断开先前连接
            sessionManager.kickOut(oldSession);
        }

        if (log.isDebugEnabled()) {
            log.debug("网关服请求游戏服[{}]登录账号成功,绑定session[{},{}]", serverId, session.getSessionId(), identity);
        }

        return identity;
    }
}
