package com.li.gameserver.modules.chat.facade;

import com.li.gamesocket.anno.Identity;
import com.li.gamesocket.anno.InBody;
import com.li.gamesocket.anno.SocketCommand;
import com.li.gamesocket.anno.SocketModule;

/**
 * @author li-yuanwen
 */
@SocketModule(module = ChatModule.MODULE)
public interface ChatFacade {


    /**
     * 发送消息
     * @param identity 标识
     * @param msg 消息
     */
    @SocketCommand(command = ChatModule.SEND)
    void send(@Identity long identity
            , @InBody(name = "msg") String msg);

}
