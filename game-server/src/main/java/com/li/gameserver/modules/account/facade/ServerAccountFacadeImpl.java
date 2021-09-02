package com.li.gameserver.modules.account.facade;

import com.li.gameremote.modules.account.facade.ServerAccountFacade;
import com.li.gameremote.modules.account.vo.AccountVo;
import com.li.gameserver.modules.account.service.AccountService;
import com.li.gamesocket.protocol.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:52
 **/
@Component
public class ServerAccountFacadeImpl implements ServerAccountFacade {

    @Autowired
    private AccountService accountService;

    @Override
    public Response<AccountVo> getShowVo(long identity) {
        AccountVo vo = accountService.transform(identity);
        return Response.SUCCESS(vo);
    }
}
