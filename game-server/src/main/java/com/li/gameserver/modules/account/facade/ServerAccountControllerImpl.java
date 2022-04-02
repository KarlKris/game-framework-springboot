package com.li.gameserver.modules.account.facade;

import com.li.gameserver.modules.account.service.AccountService;
import com.li.protocol.game.account.protocol.ServerAccountController;
import com.li.protocol.game.account.vo.AccountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:52
 **/
@Slf4j
@Component
public class ServerAccountControllerImpl implements ServerAccountController {

    @Resource
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
