package com.li.gamemanager.common.service.impl;

import com.li.gamemanager.common.aop.QueryLimit;
import com.li.gamemanager.common.entity.Channel;
import com.li.gamemanager.common.model.ChannelVo;
import com.li.gamemanager.common.repository.ChannelRepository;
import com.li.gamemanager.common.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li-yuanwen
 */
@Service
@Slf4j
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ChannelRepository channelRepository;

    @Override
    public List<ChannelVo> info(String userName) {
        return doQuery(userName, new Criteria());
    }


    @Override
    public void addChannel(Channel channel) {
        channelRepository.insert(channel);
    }

    @Override
    public void modifyChannel(Channel channel) {
        channelRepository.save(channel);
    }

    @Override
    public void delChannel(int channelId) {
        channelRepository.deleteById(channelId);
    }

    @QueryLimit(entityClass = Channel.class, userName = "#{userName)")
    private List<ChannelVo> doQuery(String userName, Criteria criteria) {
        List<Channel> channels = mongoTemplate.find(new Query(criteria), Channel.class);

        List<ChannelVo> operatorVos = new ArrayList<>(channels.size());
        channels.forEach(k->operatorVos.add(new ChannelVo(k)));

        return operatorVos;
    }
}
