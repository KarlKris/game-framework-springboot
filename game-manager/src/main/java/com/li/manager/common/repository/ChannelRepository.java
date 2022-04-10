package com.li.manager.common.repository;

import com.li.manager.common.entity.Channel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author li-yuanwen
 */
public interface ChannelRepository extends ReactiveCrudRepository<Channel, Integer> {
}
