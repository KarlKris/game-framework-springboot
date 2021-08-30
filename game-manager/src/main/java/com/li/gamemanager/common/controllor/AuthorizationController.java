package com.li.gamemanager.common.controllor;

import cn.hutool.core.util.IdUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.li.gamemanager.common.annotation.AnonymousDeleteMapping;
import com.li.gamemanager.common.annotation.AnonymousGetMapping;
import com.li.gamemanager.common.annotation.AnonymousPostMapping;
import com.li.gamemanager.common.exception.ManagerBadRequestException;
import com.li.gamemanager.common.model.AuthUserDto;
import com.li.gamemanager.common.properties.LoginProperties;
import com.li.gamemanager.common.properties.RsaProperties;
import com.li.gamemanager.common.properties.SecurityProperties;
import com.li.gamemanager.common.security.TokenProvider;
import com.li.gamemanager.common.security.config.LoginCodeEnum;
import com.li.gamemanager.utils.RsaUtils;
import com.li.gamemanager.utils.SecurityUtils;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author li-yuanwen
 */
@RestController
@Slf4j
@RequestMapping("/auth")
@Api(tags = "系统：系统授权接口")
public class AuthorizationController {


    @Autowired
    private SecurityProperties properties;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    private Cache<String, String> codeCache;

    @PostConstruct
    private void init() {
        codeCache = Caffeine.newBuilder()
                .expireAfterWrite(loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES)
                .build();
    }


    @Resource
    private LoginProperties loginProperties;
    @Resource
    private RsaProperties rsaProperties;

    @ApiOperation("登录授权")
    @AnonymousPostMapping(value = "/login")
    public Mono<ResponseEntity<Object>> login(@Validated @RequestBody AuthUserDto authUser) throws Exception {
        // 密码解密
        String password = RsaUtils.decryptByPrivateKey(rsaProperties.getPrivateKey(), authUser.getPassword());
        // 查询验证码
        String code = codeCache.getIfPresent(authUser.getUuid());
        if (StringUtils.isEmpty(code)) {
            throw new ManagerBadRequestException("验证码不存在或已过期");
        }
        // 清除验证码
        codeCache.invalidate(authUser.getUuid());
        if (StringUtils.isEmpty(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            return Mono.just(ResponseEntity.badRequest().body("验证码错误"));
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);
        Mono<Authentication> authenticationMono = reactiveAuthenticationManager.authenticate(authenticationToken);
        return authenticationMono.flatMap(authentication -> {
            ReactiveSecurityContextHolder.withAuthentication(authentication);
            String token = tokenProvider.createToken(authentication);
            // 返回 token
            return Mono.just(ResponseEntity.ok(Collections.singletonMap("token", properties.getTokenStartWith() + token)));
        });
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public Mono<ResponseEntity<Object>> getUserInfo() {
        return SecurityUtils.getCurrentUser().flatMap(userDetails -> Mono.just(ResponseEntity.ok(userDetails)));
    }

    @ApiOperation("获取验证码")
    @AnonymousGetMapping(value = "/code")
    public Mono<Object> getCode() {
        // 获取运算的结果
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        codeCache.put(uuid, captchaValue);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return Mono.just(ResponseEntity.ok(imgResult));
    }

    @ApiOperation("退出登录")
    @AnonymousDeleteMapping(value = "/logout")
    public Mono<Object> logout(HttpServletRequest request) {
        tokenProvider.kickOut(tokenProvider.getToken(request));
        return Mono.just(new ResponseEntity<>(HttpStatus.OK));
    }
}
