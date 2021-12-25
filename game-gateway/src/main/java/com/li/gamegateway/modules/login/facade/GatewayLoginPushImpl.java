package com.li.gamegateway.modules.login.facade;

import com.li.engine.service.session.SessionManager;
import com.li.protocol.gateway.login.protocol.GatewayLoginPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * @author li-yuanwen
 */
@Slf4j
@Component
public class GatewayLoginPushImpl implements GatewayLoginPush {

    @Resource
    private SessionManager sessionManager;

    @Override
    public void kickOut(Collection<Long> targetIds) {
        if (CollectionUtils.isEmpty(targetIds)) {
            return;
        }

        targetIds.forEach(sessionManager::kickOut);
    }
}
