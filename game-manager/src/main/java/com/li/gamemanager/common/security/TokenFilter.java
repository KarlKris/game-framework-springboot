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

import com.li.gamemanager.common.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * @author li-yuanwen
 */
@Slf4j
@Component
public class TokenFilter implements HandlerFilterFunction {


    private final TokenProvider tokenProvider;
    private final SecurityProperties properties;

    /**
     * @param tokenProvider     Token
     * @param properties        JWT
     */
    public TokenFilter(TokenProvider tokenProvider, SecurityProperties properties) {
        this.properties = properties;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono filter(ServerRequest serverRequest, HandlerFunction handlerFunction) {
        String token = serverRequest.headers().firstHeader(properties.getHeader());
        if (StringUtils.hasText(token) && token.startsWith(properties.getTokenStartWith())) {
            // 去掉令牌前缀
            token = token.replace(properties.getTokenStartWith(), "");
            if (tokenProvider.checkToken(token) && !tokenProvider.checkTokenExpire(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // Token 续期
                tokenProvider.checkRenewal(token);
            }
        }
        return handlerFunction.handle(serverRequest);
    }

}
