package com.li.gamemanager.common.repository;

import com.li.gamemanager.common.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author li-yuanwen
 */
public interface UserRepository extends ReactiveCrudRepository<User, String> {
}
