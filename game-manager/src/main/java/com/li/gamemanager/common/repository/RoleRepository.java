package com.li.gamemanager.common.repository;

import com.li.gamemanager.common.entity.Role;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author li-yuanwen
 */
public interface RoleRepository extends ReactiveMongoRepository<Role, String> {
}
