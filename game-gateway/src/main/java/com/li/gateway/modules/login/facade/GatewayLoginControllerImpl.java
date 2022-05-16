package com.li.gateway.modules.login.facade;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.li.common.exception.BadRequestException;
import com.li.gateway.commom.GatewaySystemConfig;
import com.li.gateway.modules.login.service.GatewayLoginService;
import com.li.network.session.PlayerSession;
import com.li.protocol.gateway.login.protocol.GatewayLoginController;
import com.li.protocol.gateway.login.vo.ReqGatewayCreateAccount;
import com.li.protocol.gateway.login.vo.ReqGatewayLoginAccount;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author li-yuanwen
 * 网关服登录Facade
 */
@Component
public class GatewayLoginControllerImpl implements GatewayLoginController {

    @Resource
    private GatewaySystemConfig gatewaySystemConfig;
    @Resource
    private GatewayLoginService gatewayLoginService;

    @Override
    public void create(PlayerSession session, ReqGatewayCreateAccount reqGatewayCreateAccount) {
        if (!StringUtils.hasLength(reqGatewayCreateAccount.getAccount())) {
            throw new BadRequestException(GatewayLoginResultCode.ACCOUNT_INVALID);
        }
        if (!checkSign(reqGatewayCreateAccount.getAccount()
                , reqGatewayCreateAccount.getTimestamp(), reqGatewayCreateAccount.getSign())) {
            throw new BadRequestException(GatewayLoginResultCode.SIGN_ERROR);
        }
        gatewayLoginService.create(session
                , reqGatewayCreateAccount.getAccount()
                , reqGatewayCreateAccount.getChannel()
                , reqGatewayCreateAccount.getServerId());
    }

    @Override
    public void login(PlayerSession session, ReqGatewayLoginAccount reqGatewayLoginAccount) {
        if (!StringUtils.hasLength(reqGatewayLoginAccount.getAccount())) {
            throw new BadRequestException(GatewayLoginResultCode.ACCOUNT_INVALID);
        }
        if (!checkSign(reqGatewayLoginAccount.getAccount()
                , reqGatewayLoginAccount.getTimestamp()
                , reqGatewayLoginAccount.getSign())) {
            throw new BadRequestException(GatewayLoginResultCode.SIGN_ERROR);
        }

        gatewayLoginService.login(session, reqGatewayLoginAccount.getAccount()
                , reqGatewayLoginAccount.getChannel()
                , reqGatewayLoginAccount.getServerId());
    }

    /** 验签 **/
    private boolean checkSign(String account, int timestamp, String sign) {

        int validTime = gatewaySystemConfig.getValidTime();
        // 校验签名有效时长
        if (validTime >= 0) {
            int now = DateUtil.thisSecond();

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
