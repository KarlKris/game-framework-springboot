package com.li.gamemanager.common.repository;

import com.li.gamemanager.common.entity.DataPermission;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author li-yuanwen
 * @date 2021/6/12 18:53
 **/
public interface DataPermissionRepository extends ReactiveMongoRepository<DataPermission, String> {


}
