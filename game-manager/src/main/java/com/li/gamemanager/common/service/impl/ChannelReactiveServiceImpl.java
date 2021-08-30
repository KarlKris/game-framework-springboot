package com.li.gamemanager.common.service.impl;

import com.li.gamemanager.common.aop.QueryLimit;
import com.li.gamemanager.common.entity.Channel;
import com.li.gamemanager.common.model.ChannelVo;
import com.li.gamemanager.common.repository.ChannelRepository;
import com.li.gamemanager.common.service.ChannelReactiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li-yuanwen
 */
@Service
@Slf4j
public class ChannelReactiveServiceImpl implements ChannelReactiveService {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private ChannelRepository channelRepository;

    @Override
    public Flux<List<ChannelVo>> info(String userName) {
        return doQuery(userName, new Criteria());
    }


    @Override
    public Mono<Channel> addChannel(Channel channel) {
        return channelRepository.insert(channel);
    }

    @Override
    public Mono<Void> modifyChannel(Channel channel) {
        return channelRepository.save(channel).flatMap(c -> Mono.empty());
    }

    @Override
    public Mono<Void> delChannel(int channelId) {
        return channelRepository.deleteById(channelId);
    }

    @QueryLimit(entityClass = Channel.class, userName = "#{userName)")
    private Flux<List<ChannelVo>> doQuery(String userName, Criteria criteria) {
        return reactiveMongoTemplate.find(new Query(criteria), Channel.class).buffer().flatMap(channels -> {
            List<ChannelVo> channelVos = new ArrayList<>(channels.size());
            channels.forEach(c -> channelVos.add(new ChannelVo(c)));
            return Mono.just(channelVos);
        });
    }
}
