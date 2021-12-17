package com.li.gamegateway.modules.chat.facade;

import com.li.gamegateway.modules.account.service.GatewayAccountService;
import com.li.gamegateway.modules.chat.vo.GatewayChatContent;
import com.li.protocol.game.account.vo.AccountVo;
import com.li.protocol.game.chat.protocol.ChatPush;
import com.li.protocol.game.chat.vo.ChatContent;
import com.li.engine.anno.OuterPushInject;
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

    @OuterPushInject
    private GatewayChatPush gatewayChatPush;

    @Override
    public void pushMessage(Collection<Long> pushIds, ChatContent content) {
        AccountVo accountVo = gatewayAccountService.transformById(content.getSenderId());
        gatewayChatPush.pushMessage(pushIds, new GatewayChatContent(accountVo, content.getMsg()));
    }
}
