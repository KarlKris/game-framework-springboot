package com.li.engine.service.rpc;

/**
 * @author li-yuanwen
 * 远程调用接口
 */
public interface IRpcService {

    /**
     * 获取远程代理
     * @param tClass 目标对象
     * @param identity 身份标识
     * @param <T> 类型
     * @return /
     */
    <T> T getSendProxy(Class<T> tClass, long identity);

    /**
     * 获取远程代理
     * @param tClass 目标对象
     * @param serverId 服标识
     * @param <T> 类型
     * @return /
     */
    <T> T getSendProxy(Class<T> tClass, String serverId);

}
