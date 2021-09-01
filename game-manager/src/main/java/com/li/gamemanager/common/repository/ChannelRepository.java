package com.li.gamemanager.common.repository;

import com.li.gamemanager.common.entity.Channel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author li-yuanwen
 */
public interface ChannelRepository extends ReactiveCrudRepository<Channel, Integer> {
}
