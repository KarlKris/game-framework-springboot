package com.li.protocol.gateway.login.protocol;

import com.li.network.anno.PushIds;
import com.li.network.anno.SocketController;
import com.li.network.anno.SocketMethod;
import com.li.network.anno.SocketPush;

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
    @SocketMethod(id = GatewayLoginModule.KICK_OUT)
    void kickOut(@PushIds Collection<Long> targetIds);

}
