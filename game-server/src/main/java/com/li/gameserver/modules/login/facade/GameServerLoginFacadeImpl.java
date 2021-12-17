package com.li.gameserver.modules.login.facade;

import cn.hutool.core.util.ArrayUtil;
import com.li.engine.anno.InnerPushInject;
import com.li.engine.service.session.SessionManager;
import com.li.gamecommon.exception.BadRequestException;
import com.li.gameserver.common.GameServerSystemConfig;
import com.li.gameserver.modules.account.service.AccountService;
import com.li.network.session.ISession;
import com.li.network.session.ServerSession;
import com.li.protocol.game.login.dto.ReqGameCreateAccount;
import com.li.protocol.game.login.dto.ReqGameLoginAccount;
import com.li.protocol.game.login.protocol.GameServerLoginFacade;
import com.li.protocol.game.login.protocol.GameServerLoginResultCode;
import com.li.protocol.gateway.login.protocol.GatewayLoginPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

/**
 * @author li-yuanwen
 */
@Component
@Slf4j
public class GameServerLoginFacadeImpl implements GameServerLoginFacade {

    @Resource
    private GameServerSystemConfig gameServerSystemConfig;
    @Resource
    private SessionManager sessionManager;
    @Resource
    private AccountService accountService;

    @InnerPushInject
    private GatewayLoginPush gatewayLoginPush;

    @Override
    public Long create(ServerSession session, ReqGameCreateAccount reqGameCreateAccount) {
        if (!checkChannel(reqGameCreateAccount.getChannel())) {
            throw new BadRequestException(GameServerLoginResultCode.REJECT);

        }
        String accountName = reqGameCreateAccount.getAccount() + "." + reqGameCreateAccount.getChannel();
        long nextId = accountService.createAccount(accountName, reqGameCreateAccount.getChannel());
        sessionManager.bindIdentity(session, nextId);
        return nextId;
    }

    @Override
    public Long login(ServerSession session, ReqGameLoginAccount reqGameLoginAccount) {
        if (!checkChannel(reqGameLoginAccount.getChannel())) {
            throw new BadRequestException(GameServerLoginResultCode.REJECT);
        }
        String accountName = reqGameLoginAccount.getAccount() + "." + reqGameLoginAccount.getChannel();
        long identity = accountService.login(accountName);
        // 先判断玩家是否在线
        ISession oldSession = sessionManager.getIdentitySession(identity);
        if (oldSession != null
                && !Objects.equals(session.getSessionId(), oldSession.getSessionId())) {
            // 推送给网关服,使其断开连接
            gatewayLoginPush.kickOut(Collections.singleton(identity));
        }
        sessionManager.bindIdentity(session, identity);
        return identity;
    }

    private boolean checkChannel(int channel) {
        if (channel != this.gameServerSystemConfig.getMainChannel()) {
            return ArrayUtil.contains(this.gameServerSystemConfig.getChannels(), channel);
        }
        return true;
    }
}
