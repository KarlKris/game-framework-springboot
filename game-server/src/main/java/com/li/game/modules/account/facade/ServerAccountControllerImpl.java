package com.li.game.modules.account.facade;

import com.li.game.modules.account.service.AccountService;
import com.li.protocol.game.account.protocol.ServerAccountController;
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
    public void levelUp(long identity) {
        accountService.levelUp(identity);
    }
}
