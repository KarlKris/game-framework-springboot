package com.li.gamecore.rpc;

import com.li.gamecore.rpc.model.Address;

/**
 * @author li-yuanwen
 * 远程服务器查找接口
 */
public interface RemoteServerSeekService {


    /**
     * 查询指定命令所在的服务器地址
     * @param module 模块号
     * @param command 命令号
     * @return ip地址
     */
    Address seekApplicationAddressByCommand(short module, byte command);


}
