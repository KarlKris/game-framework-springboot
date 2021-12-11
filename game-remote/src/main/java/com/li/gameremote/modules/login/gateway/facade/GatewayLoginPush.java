package com.li.gameremote.modules.login.gateway.facade;

import com.li.gamesocket.anno.PushIds;
import com.li.gamesocket.anno.SocketMethod;
import com.li.gamesocket.anno.SocketController;
import com.li.gamesocket.anno.SocketPush;

import java.util.Collection;

/**
 * @author li-yuanwen
 */
@SocketPush
@SocketController(module = GatewayLoginModule.MODULE)
public interface GatewayLoginPush {


    /**
     * 强退推送
     * @param targetIds 强退目标
     */
    @SocketMethod(command = GatewayLoginModule.KICK_OUT)
    void kickOut(@PushIds Collection<Long> targetIds);

}
