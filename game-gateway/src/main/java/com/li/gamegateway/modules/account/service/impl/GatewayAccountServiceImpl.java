package com.li.gamegateway.modules.account.service.impl;

import com.li.gamecore.cache.anno.Cachedable;
import com.li.gamecore.cache.config.CachedType;
import com.li.gamegateway.modules.account.service.GatewayAccountService;
import com.li.gameremote.modules.account.facade.ServerAccountFacade;
import com.li.gameremote.modules.account.vo.AccountVo;
import com.li.gamesocket.service.rpc.RpcService;
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
    private RpcService rpcService;

    @Override
    @Cachedable(type = CachedType.REMOTE, name = "identity2accountVo", key = "#identity")
    public AccountVo transformById(long identity) {
        return rpcService.getSendProxy(ServerAccountFacade.class, identity)
                .getShowVo(identity)
                .getContent();
    }
}
