package com.li.common.id;

/**
 * 分布式ID生成器
 * @author li-yuanwen
 */
public class MultiServerIdGenerator {

    private final SnowflakeIdGenerator idGenerator;

    public MultiServerIdGenerator(short serverId) {
        this.idGenerator = new SnowflakeIdGenerator(serverId);
    }

    public long nextId() {
        return idGenerator.nextId();
    }

}
