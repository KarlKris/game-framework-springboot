package com.li.gamegateway.modules.login.facade;

import com.li.gameremote.modules.login.gateway.facade.GatewayLoginPush;
import com.li.gamesocket.service.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * @author li-yuanwen
 */
@Slf4j
@Component
public class GatewayLoginPushImpl implements GatewayLoginPush {

    @Autowired
    private SessionManager sessionManager;

    @Override
    public void kickOut(Collection<Long> targetIds) {
        if (CollectionUtils.isEmpty(targetIds)) {
            return;
        }

        targetIds.forEach(sessionManager::kickOut);
    }
}
