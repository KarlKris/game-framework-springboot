/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.li.gamemanager.common.security;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.li.gamemanager.common.model.JwtUserDto;
import com.li.gamemanager.common.properties.SecurityProperties;
import com.li.gamemanager.common.security.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author /
 */
@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private final SecurityProperties properties;
    public static final String AUTHORITIES_KEY = "user";
    private JwtParser jwtParser;
    private JwtBuilder jwtBuilder;
    private Cache<String, Long> tokenExpireMap;

    public TokenProvider(SecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getBase64Secret());
        Key key = Keys.hmacShaKeyFor(keyBytes);

        jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        jwtBuilder = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512);

        tokenExpireMap = Caffeine.newBuilder()
                .expireAfterAccess(properties.getTokenValidityInSeconds(), TimeUnit.SECONDS)
                .build();

    }


    /**
     * ??????Token
     *
     * @param authentication /
     * @return /
     */
    public String createToken(Authentication authentication) {
        // token ????????????
        DateTime expireDate = DateUtil.offset(new Date(), DateField.SECOND, properties.getTokenValidityInSeconds());
        String token = jwtBuilder
                // ??????ID??????????????? Token ????????????
                .setId(UUID.randomUUID().toString())
                .claim(AUTHORITIES_KEY, authentication.getName())
                .setSubject(authentication.getName())
                .setExpiration(expireDate)
                .compact();

        tokenExpireMap.put(token, expireDate.getTime());

        return token;
    }

    public boolean checkToken(String token) {
        try{
            getClaims(token);
            return true;
        }catch (Exception e) {
            return false;
        }

    }

    /**
     * ??????Token ??????????????????
     *
     * @param token /
     * @return /
     */
    Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String userName = claims.getSubject();
        JwtUserDto userDto = UserDetailsServiceImpl.userDtoCache.getIfPresent(userName);
        if (userDto == null) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(userDto, token, userDto.getAuthorities());
    }

    public Claims getClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * @param token ???????????????token
     */
    public void checkRenewal(String token) {
        // ??????????????????token,??????token???????????????
        Long expireTime = tokenExpireMap.getIfPresent(token);
        if (expireTime == null) {
            return;
        }
        // ?????????????????????????????????????????????
        long differ = expireTime - System.currentTimeMillis();
        // ?????????????????????????????????????????????
        if (differ <= properties.getDetect()) {
            long renew = expireTime + properties.getRenew();
            tokenExpireMap.put(token, renew);
        }

    }

    public boolean checkTokenExpire(String token) {
        Long temp;
        return (temp = tokenExpireMap.getIfPresent(token)) == null || System.currentTimeMillis() > temp;
    }

    public void kickOut(String token) {
        if (!StringUtils.isEmpty(token)) {
            this.tokenExpireMap.invalidate(token);
        }
    }

    public String getToken(ServerHttpRequest request) {
        final String requestHeader = request.getHeaders().getFirst(properties.getHeader());
        if (requestHeader != null && requestHeader.startsWith(properties.getTokenStartWith())) {
            return requestHeader.substring(properties.getTokenStartWith().length());
        }
        return null;
    }
}
