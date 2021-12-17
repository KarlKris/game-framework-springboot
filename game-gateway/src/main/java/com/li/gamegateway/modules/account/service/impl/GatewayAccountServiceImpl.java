package com.li.gamegateway.modules.account.service.impl;

import com.li.gamecore.cache.anno.Cachedable;
import com.li.gamecore.cache.config.CachedType;
import com.li.gamegateway.modules.account.service.GatewayAccountService;
import com.li.protocol.common.cache.CacheNameConstants;
import com.li.protocol.game.account.protocol.ServerAccountFacade;
import com.li.protocol.game.account.vo.AccountVo;
import com.li.engine.service.rpc.IRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:42
 **/
@Slf4j
@Service
public class GatewayAccountServiceImpl implements GatewayAccountService {

    @Autowired
    private IRpcService IRpcService;

    @Override
    @Cachedable(type = CachedType.REMOTE
            , name = CacheNameConstants.IDENTITY_TO_ACCOUNT_VO, key = "#identity")
    public AccountVo transformById(long identity) {
        return IRpcService.getSendProxy(ServerAccountFacade.class, identity)
                .getShowVo(identity);
    }
}
