package com.li.gamegateway.modules.login.facade;

import cn.hutool.crypto.SecureUtil;
import com.li.gamecommon.exception.BadRequestException;
import com.li.gamegateway.commom.GatewaySystemConfig;
import com.li.gamegateway.modules.login.service.GatewayLoginService;
import com.li.network.session.PlayerSession;
import com.li.protocol.gateway.login.dto.ReqGatewayCreateAccount;
import com.li.protocol.gateway.login.dto.ReqGatewayLoginAccount;
import com.li.protocol.gateway.login.protocol.GatewayLoginFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * 网关服登录Facade
 */
@Component
public class GatewayLoginFacadeImpl implements GatewayLoginFacade {

    @Resource
    private GatewaySystemConfig gatewaySystemConfig;
    @Resource
    private GatewayLoginService gatewayLoginService;

    @Override
    public Long create(PlayerSession session, ReqGatewayCreateAccount reqGatewayCreateAccount) {
        if (StringUtils.isBlank(reqGatewayCreateAccount.getAccount())) {
            throw new BadRequestException(GatewayLoginResultCode.ACCOUNT_INVALID);
        }
        if (!checkSign(reqGatewayCreateAccount.getAccount()
                , reqGatewayCreateAccount.getTimestamp(), reqGatewayCreateAccount.getSign())) {
            throw new BadRequestException(GatewayLoginResultCode.SIGN_ERROR);
        }
        return gatewayLoginService.create(session
                , reqGatewayCreateAccount.getAccount()
                , reqGatewayCreateAccount.getChannel()
                , reqGatewayCreateAccount.getServerId());
    }

    @Override
    public Long login(PlayerSession session, ReqGatewayLoginAccount reqGatewayLoginAccount) {
        if (StringUtils.isBlank(reqGatewayLoginAccount.getAccount())) {
            throw new BadRequestException(GatewayLoginResultCode.ACCOUNT_INVALID);
        }
        if (!checkSign(reqGatewayLoginAccount.getAccount()
                , reqGatewayLoginAccount.getTimestamp()
                , reqGatewayLoginAccount.getSign())) {
            throw new BadRequestException(GatewayLoginResultCode.SIGN_ERROR);
        }
        return gatewayLoginService.login(session, reqGatewayLoginAccount.getAccount()
                , reqGatewayLoginAccount.getChannel()
                , reqGatewayLoginAccount.getServerId());
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
