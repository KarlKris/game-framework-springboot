package com.li.gamemanager.common.repository;

import com.li.gamemanager.common.entity.Role;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author li-yuanwen
 */
public interface RoleRepository extends ReactiveCrudRepository<Role, String> {
}
