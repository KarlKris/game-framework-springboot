package com.li.manager.common.repository;

import com.li.manager.common.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author li-yuanwen
 */
public interface UserRepository extends ReactiveCrudRepository<User, String> {
}
