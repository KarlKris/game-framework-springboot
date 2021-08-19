package com.li.gameserver.modules.login.facade;

import cn.hutool.core.util.ArrayUtil;
import com.li.gamecommon.common.MultiServerIdGenerator;
import com.li.gameremote.modules.login.game.GameServerLoginFacade;
import com.li.gameremote.modules.login.game.GameServerLoginResultCode;
import com.li.gameserver.common.GameServerSystemConfig;
import com.li.gameserver.modules.login.service.AccountService;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.service.session.Session;
import com.li.gamesocket.service.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 */
@Component
public class GameServerLoginFacadeImpl implements GameServerLoginFacade {

    @Autowired
    private GameServerSystemConfig gameServerSystemConfig;
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private AccountService accountService;

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
        long identity = accountService.login(accountName, channel);
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
