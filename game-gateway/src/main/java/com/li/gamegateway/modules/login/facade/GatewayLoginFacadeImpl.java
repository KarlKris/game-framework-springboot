package com.li.gamegateway.modules.login.facade;

import cn.hutool.crypto.SecureUtil;
import com.li.gamegateway.commom.GatewaySystemConfig;
import com.li.gamegateway.modules.login.service.GatewayLoginService;
import com.li.gameremote.modules.login.gateway.GatewayLoginFacade;
import com.li.gamesocket.protocol.Response;
import com.li.gamesocket.service.session.Session;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author li-yuanwen
 * 网关服登录Facade
 */
@Component
public class GatewayLoginFacadeImpl implements GatewayLoginFacade {

    @Autowired
    private GatewaySystemConfig gatewaySystemConfig;
    @Autowired
    private GatewayLoginService gatewayLoginService;

    @Override
    public Response<Object> create(Session session, String account, int channel, int serverId, int timestamp, String sign) {
        if (StringUtils.isBlank(account)) {
            return Response.ERROR(GatewayLoginResultCode.ACCOUNT_INVALID);
        }
        if (!checkSign(account, timestamp, sign)) {
            return Response.ERROR(GatewayLoginResultCode.SIGN_ERROR);
        }
        gatewayLoginService.create(session, account, channel, serverId);
        return Response.DEFAULT_SUCCESS;
    }

    @Override
    public Response<Object> login(Session session, String account, int channel, int serverId, int timestamp, String sign) {
        if (StringUtils.isBlank(account)) {
            return Response.ERROR(GatewayLoginResultCode.ACCOUNT_INVALID);
        }
        if (!checkSign(account, timestamp, sign)) {
            return Response.ERROR(GatewayLoginResultCode.SIGN_ERROR);
        }
        gatewayLoginService.login(session, account, channel, serverId);
        return Response.DEFAULT_SUCCESS;
    }

    /** 验签 **/
    private boolean checkSign(String account, int timestamp, String sign) {

        int validTime = gatewaySystemConfig.getValidTime();
        // 校验签名有效时长
        if (validTime >= 0) {
            int now = (int) (System.currentTimeMillis() / 1000);

            if (timestamp > now) {
                return false;
            }

            if (now - timestamp > validTime) {
                return false;
            }
        }

        return SecureUtil.md5(account + timestamp + gatewaySystemConfig.getLoginKey()).equalsIgnoreCase(sign);
    }
}
