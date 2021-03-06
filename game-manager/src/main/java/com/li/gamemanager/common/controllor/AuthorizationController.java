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
import org.springframework.http.server.reactive.ServerHttpRequest;
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
@Api(tags = "???????????????????????????")
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

    @ApiOperation("????????????")
    @AnonymousPostMapping(value = "/login")
    public Mono<ResponseEntity<Object>> login(@Validated @RequestBody AuthUserDto authUser) throws Exception {
        // ????????????
        String password = RsaUtils.decryptByPrivateKey(rsaProperties.getPrivateKey(), authUser.getPassword());
        // ???????????????
        String code = codeCache.getIfPresent(authUser.getUuid());
        if (StringUtils.isEmpty(code)) {
            throw new ManagerBadRequestException("??????????????????????????????");
        }
        // ???????????????
        codeCache.invalidate(authUser.getUuid());
        if (StringUtils.isEmpty(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            return Mono.just(ResponseEntity.badRequest().body("???????????????"));
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);
        Mono<Authentication> authenticationMono = reactiveAuthenticationManager.authenticate(authenticationToken);
        return authenticationMono.flatMap(authentication -> {
            ReactiveSecurityContextHolder.withAuthentication(authentication);
            String token = tokenProvider.createToken(authentication);
            // ?????? token
            return Mono.just(ResponseEntity.ok(Collections.singletonMap("token", properties.getTokenStartWith() + token)));
        });
    }

    @ApiOperation("??????????????????")
    @GetMapping(value = "/info")
    public Mono<ResponseEntity<Object>> getUserInfo() {
        return SecurityUtils.getCurrentUser().flatMap(userDetails -> Mono.just(ResponseEntity.ok(userDetails)));
    }

    @ApiOperation("???????????????")
    @AnonymousGetMapping(value = "/code")
    public Mono<Object> getCode() {
        // ?????????????????????
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = IdUtil.simpleUUID();
        //????????????????????? arithmetic???????????? >= 2 ??????captcha.text()??????????????????????????????
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // ??????
        codeCache.put(uuid, captchaValue);
        // ???????????????
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return Mono.just(ResponseEntity.ok(imgResult));
    }

    @ApiOperation("????????????")
    @AnonymousDeleteMapping(value = "/logout")
    public Mono<Object> logout(ServerHttpRequest request) {
        tokenProvider.kickOut(tokenProvider.getToken(request));
        return Mono.just(new ResponseEntity<>(HttpStatus.OK));
    }
}
