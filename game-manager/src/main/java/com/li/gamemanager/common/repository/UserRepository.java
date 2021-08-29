package com.li.gamemanager.common.repository;

import com.li.gamemanager.common.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author li-yuanwen
 */
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
