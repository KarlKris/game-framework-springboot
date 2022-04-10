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
package com.li.manager.common.security;

import com.li.manager.common.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author li-yuanwen
 */
@Slf4j
@Component
public class TokenFilter implements WebFilter {


    private final TokenProvider tokenProvider;
    private final SecurityProperties properties;

    /**
     * @param tokenProvider Token
     * @param properties    JWT
     */
    public TokenFilter(TokenProvider tokenProvider, SecurityProperties properties) {
        this.properties = properties;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = tokenProvider.getToken(request);
        if (StringUtils.hasText(token)) {
            // 去掉令牌前缀
            token = token.replace(properties.getTokenStartWith(), "");
            Authentication authentication;
            if (tokenProvider.checkToken(token) && !tokenProvider.checkTokenExpire(token)
                    && (authentication = tokenProvider.getAuthentication(token)) != null) {
                tokenProvider.checkRenewal(token);
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

            }
        }
        return chain.filter(exchange);
    }

}
