package com.li.manager.common.repository;

import com.li.manager.common.entity.Role;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author li-yuanwen
 */
public interface RoleRepository extends ReactiveCrudRepository<Role, String> {
}
