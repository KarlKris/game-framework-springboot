package com.li.common.id;

public interface DistributedIdGenerator extends IdGenerator {

    int getWorkerId(long id);

}
