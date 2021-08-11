package com.li.gamecore.common;

/**
 * @author li-yuanwen
 * 分布式ID生成器
 */
public class MultiServerIdGenerator {

    private SnowflakeIdGenerator idGenerator;

    MultiServerIdGenerator(short serverId) {
        this.idGenerator = new SnowflakeIdGenerator(serverId);
    }

    public long nextId() {
        return idGenerator.nextId();
    }

}
