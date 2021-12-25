package com.li.gameserver.modules.account.facade;

import com.li.protocol.game.account.protocol.ServerAccountController;
import com.li.protocol.game.account.vo.AccountVo;
import com.li.gameserver.modules.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:52
 **/
@Component
public class ServerAccountControllerImpl implements ServerAccountController {

    @Autowired
    private AccountService accountService;

    @Override
    public AccountVo getShowVo(long identity) {
        return accountService.transform(identity);
    }

    @Override
    public void levelUp(long identity) {
        accountService.levelUp(identity);
    }
}
