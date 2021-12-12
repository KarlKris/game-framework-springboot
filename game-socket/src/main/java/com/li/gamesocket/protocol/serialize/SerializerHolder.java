package com.li.gamesocket.protocol.serialize;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author li-yuanwen
 * @date 2021/8/4 22:08
 * 序列化与反序列化工具管理
 **/
@Component
public class SerializerHolder {

    /** 默认序列化类型 **/
    public static Serializer DEFAULT_SERIALIZER;

    @Resource
    private ApplicationContext applicationContext;

    /** 消息体序列化器 **/
    private Map<Byte, Serializer> serializerHolder;

    @PostConstruct
    private void init() {
        serializerHolder = new HashMap<>(2);
        for (Serializer serializer : applicationContext.getBeansOfType(Serializer.class).values()) {
            if (serializerHolder.putIfAbsent(serializer.getSerializerType(), serializer) != null) {
                throw new BeanInitializationException("出现相同类型[" + serializer.getSerializerType() + "]序列化器");
            }
        }

        DEFAULT_SERIALIZER = serializerHolder.get(SerializeType.PROTO_STUFF.getType());
    }

    /** 获取序列化/反序列化工具 **/
    public Serializer getSerializer(byte type) {
        return this.serializerHolder.get(type);
    }


}
