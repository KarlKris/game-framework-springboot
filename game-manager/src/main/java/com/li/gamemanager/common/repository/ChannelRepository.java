package com.li.gamemanager.common.repository;

import com.li.gamemanager.common.entity.Channel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author li-yuanwen
 */
public interface ChannelRepository extends ReactiveMongoRepository<Channel, Integer> {
}
