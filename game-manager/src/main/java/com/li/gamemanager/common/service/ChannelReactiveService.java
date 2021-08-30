package com.li.gamemanager.common.service;


import com.li.gamemanager.common.entity.Channel;
import com.li.gamemanager.common.model.ChannelVo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author li-yuanwen
 * 渠道相关api
 */
public interface ChannelReactiveService {


    /**
     * 查询用户权限内的运营商信息
     * @param userName 用户账号
     * @return /
     */
    Flux<List<ChannelVo>> info(String userName);


    /**
     * 添加渠道
     * @param channel 新增渠道信息
     */
    Mono<Channel> addChannel(Channel channel);

    /**
     * 删除渠道
     * @param channelId 渠道id
     */
    Mono<Void> delChannel(int channelId);

    /**
     * 修改渠道
     * @param channel 渠道信息
     */
    Mono<Void> modifyChannel(Channel channel);

}
