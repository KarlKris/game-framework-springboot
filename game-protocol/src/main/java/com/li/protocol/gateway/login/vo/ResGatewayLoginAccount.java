package com.li.protocol.gateway.login.vo;

import com.li.network.anno.SocketResponse;
import com.li.protocol.gateway.login.protocol.GatewayLoginModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author li-yuanwen
 * @date 2022/5/11
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SocketResponse(module = GatewayLoginModule.MODULE, id = GatewayLoginModule.LOGIN_ACCOUNT)
public class ResGatewayLoginAccount {

    /** 玩家唯一标识 **/
    private long identity;

}
