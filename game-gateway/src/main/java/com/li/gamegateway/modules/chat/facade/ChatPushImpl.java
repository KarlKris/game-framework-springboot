package com.li.gamegateway.modules.chat.facade;

import com.li.gamegateway.modules.account.service.GatewayAccountService;
import com.li.gamegateway.modules.chat.vo.GatewayChatContent;
import com.li.gameremote.modules.account.vo.AccountVo;
import com.li.gameremote.modules.chat.facade.ChatPush;
import com.li.gameremote.modules.chat.vo.ChatContent;
import com.li.gamesocket.anno.PushInject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author li-yuanwen
 * @date 2021/9/2 21:34
 * 聊天消息推送数据处理
 **/
@Component
public class ChatPushImpl implements ChatPush {

    @Autowired
    private GatewayAccountService gatewayAccountService;

    @PushInject
    private GatewayChatPush gatewayChatPush;

    @Override
    public void pushMessage(Collection<Long> pushIds, ChatContent content) {
        AccountVo accountVo = gatewayAccountService.transformById(content.getSenderId());
        gatewayChatPush.pushMessage(pushIds, new GatewayChatContent(accountVo, content.getMsg()));
    }
}
