package com.li.gamecluster.rpc;

import com.li.gamecluster.zookeeper.discovery.ZkDiscoveryService;
import com.li.gamecluster.zookeeper.model.ServerType;
import com.li.gamecluster.zookeeper.model.ServiceDiscoveryNode;
import com.li.gamecommon.rpc.RemoteServerSeekService;
import com.li.gamecommon.rpc.model.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author li-yuanwen
 * @date 2021/8/8 16:19
 * 远程服务查询接口
 **/
@Service
@Slf4j
public class RemoteServerSeekServiceImpl implements RemoteServerSeekService {

    @Autowired
    private ZkDiscoveryService service;


    @Override
    public String seekServiceNameByModule(short module) {
        try {
            ServerType type = service.getServerTypeByModule(module);
            if (type == null) {
                return null;
            }
            return type.name();
        } catch (Exception e) {
            log.error("远程服务查询模块号[{}]对应服务出现未知异常", module, e);
            return null;
        }
    }

    @Override
    public Address seekApplicationAddressByModule(short module, long identity) {
        try {
            ServerType type = service.getServerTypeByModule(module);
            if (type == null) {
                return null;
            }

            ServiceDiscoveryNode node = service.checkAndGetServiceDiscoveryNode(type);
            return node.selectAddress(identity);
        } catch (Exception e) {
            log.error("远程服务查询模块号[{}]对应服务地址出现未知异常", module, e);
            return null;
        }
    }

    @Override
    public Address seekApplicationAddressById(short module, String id) {
        try {
            ServerType type = service.getServerTypeByModule(module);
            if (type == null) {
                return null;
            }

            ServiceDiscoveryNode node = service.checkAndGetServiceDiscoveryNode(type);
            return node.selectAddressById(id);
        } catch (Exception e) {
            log.error("远程服务查询模块号[{}]服务标识[{}]对应服务地址出现未知异常", module, id, e);
            return null;
        }
    }
}
