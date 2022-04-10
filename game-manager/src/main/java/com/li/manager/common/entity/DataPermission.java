package com.li.manager.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * 数据权限
 * @author li-yuanwen
 * @date 2021/6/12 18:47
 **/
@Document
@Slf4j
@NoArgsConstructor
@Getter
public class DataPermission {

    /** 自定义id **/
    @Id
    private String id;

    /** 权限描述 **/
    private String desc;

    /** 表名 **/
    private String entityName;

    /** 具体权限限制 **/
    private Map<String, Object> limit;

}
