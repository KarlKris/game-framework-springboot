package com.li.gameserver.modules.login.manager;

import com.li.gamecore.dao.service.EntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 */
@Component
@Slf4j
public class AccountManager {

    @Autowired
    private EntityService entityService;


}
