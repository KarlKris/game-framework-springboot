package com.li.gameremote.modules.login.gateway;

import com.li.gamesocket.anno.PushIds;
import com.li.gamesocket.anno.SocketCommand;
import com.li.gamesocket.anno.SocketModule;
import com.li.gamesocket.anno.SocketPush;

import java.util.Collection;

/**
 * @author li-yuanwen
 */
@SocketPush
@SocketModule(module = GatewayLoginModule.MODULE)
public interface GatewayLoginPush {


    /**
     * 强退推送
     * @param targetIds 强退目标
     */
    @SocketCommand(command = GatewayLoginModule.KICK_OUT)
    void kickOut(@PushIds Collection<Long> targetIds);

}
