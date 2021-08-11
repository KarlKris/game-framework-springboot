package com.li.gameserver.modules.login.facade;

import cn.hutool.core.util.ArrayUtil;
import com.li.gamecommon.common.MultiServerIdGenerator;
import com.li.gameremote.modules.login.game.GameServerLoginFacade;
import com.li.gameremote.modules.login.game.GameServerLoginResultCode;
import com.li.gameserver.common.GameServerSystemConfig;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.service.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 */
@Component
public class GameServerLoginFacadeImpl implements GameServerLoginFacade {

    /** 账号2玩家标识 **/
    private ConcurrentHashMap<String, Long> account2Id = new ConcurrentHashMap<>();

    @Autowired
    private MultiServerIdGenerator idGenerator;
    @Autowired
    private GameServerSystemConfig gameServerSystemConfig;

    @PostConstruct
    private void init() {
        String accountName = "admin.1";
        this.account2Id.put(accountName, idGenerator.nextId());
    }

    @Override
    public Response<Long> create(Session session, String account, int channel) {
        if (!checkChannel(channel)) {
            return Response.ERROR(GameServerLoginResultCode.REJECT);
        }
        String accountName = account + "." + channel;
        long nextId = this.idGenerator.nextId();
        Long old = this.account2Id.putIfAbsent(accountName, nextId);
        if (old != null) {
            return Response.ERROR(GameServerLoginResultCode.CREATE_REPEAT);
        }
        return Response.SUCCESS(nextId);
    }

    @Override
    public Response<Long> login(Session session, String account, int channel) {
        if (!checkChannel(channel)) {
            return Response.ERROR(GameServerLoginResultCode.REJECT);
        }
        String accountName = account + "." + channel;
        Long identity = this.account2Id.get(accountName);
        if (identity == null) {
            return Response.ERROR(GameServerLoginResultCode.ACCOUNT_NOT_FOUND);
        }
        return Response.SUCCESS(identity);
    }

    private boolean checkChannel(int channel) {
        if (channel != this.gameServerSystemConfig.getMainChannel()) {
            return ArrayUtil.contains(this.gameServerSystemConfig.getChannels(), channel);
        }
        return true;
    }
}
