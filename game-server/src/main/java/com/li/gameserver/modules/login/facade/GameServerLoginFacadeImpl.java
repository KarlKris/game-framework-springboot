package com.li.gameserver.modules.login.facade;

import cn.hutool.core.util.ArrayUtil;
import com.li.gameremote.modules.login.game.GameServerLoginFacade;
import com.li.gameremote.modules.login.game.GameServerLoginResultCode;
import com.li.gameremote.modules.login.gateway.GatewayLoginPush;
import com.li.gameserver.common.GameServerSystemConfig;
import com.li.gameserver.modules.login.service.AccountService;
import com.li.gamesocket.anno.PushInject;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

/**
 * @author li-yuanwen
 */
@Component
@Slf4j
public class GameServerLoginFacadeImpl implements GameServerLoginFacade {

    @Autowired
    private GameServerSystemConfig gameServerSystemConfig;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private AccountService accountService;

    @PushInject
    private GatewayLoginPush gatewayLoginPush;

    @Override
    public Response<Long> create(Session session, String account, int channel) {
        if (!checkChannel(channel)) {
            return Response.ERROR(GameServerLoginResultCode.REJECT);
        }
        String accountName = account + "." + channel;
        long nextId = accountService.createAccount(accountName, channel);
        sessionManager.bindIdentity(session, nextId, true);
        return Response.SUCCESS(nextId);
    }

    @Override
    public Response<Long> login(Session session, String account, int channel) {
        if (!checkChannel(channel)) {
            return Response.ERROR(GameServerLoginResultCode.REJECT);
        }
        String accountName = account + "." + channel;
        long identity = accountService.login(accountName);
        // 先判断玩家是否在线
        Session oldSession = sessionManager.getIdentitySession(identity);
        if (oldSession != null
                && !Objects.equals(session.getSessionId(), oldSession.getSessionId())) {
            // 推送给网关服,使其断开连接
            gatewayLoginPush.kickOut(Collections.singleton(identity));
        }
        sessionManager.bindIdentity(session, identity, true);
        return Response.SUCCESS(identity);
    }

    private boolean checkChannel(int channel) {
        if (channel != this.gameServerSystemConfig.getMainChannel()) {
            return ArrayUtil.contains(this.gameServerSystemConfig.getChannels(), channel);
        }
        return true;
    }
}
