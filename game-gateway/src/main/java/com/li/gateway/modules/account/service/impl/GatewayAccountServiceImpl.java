package com.li.gateway.modules.account.service.impl;

import com.li.core.cache.anno.Cachedable;
import com.li.core.cache.config.CachedType;
import com.li.engine.service.rpc.IRpcService;
import com.li.gateway.modules.account.service.GatewayAccountService;
import com.li.protocol.common.cache.CacheNameConstants;
import com.li.protocol.game.account.protocol.ServerAccountController;
import com.li.protocol.game.account.vo.AccountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:42
 **/
@Slf4j
@Service
public class GatewayAccountServiceImpl implements GatewayAccountService {

    @Resource
    private IRpcService rpcService;

    @Override
    @Cachedable(type = CachedType.REMOTE
            , name = CacheNameConstants.IDENTITY_TO_ACCOUNT_VO, key = "#identity")
    public AccountVo transformById(long identity) {
        return rpcService.getSendProxy(ServerAccountController.class, identity)
                .getShowVo(identity);
    }
}
