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
package com.li.gamemanager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.gamecommon.ApplicationContextHolder;
import com.li.gamemanager.common.exception.ManagerBadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 获取当前登录的用户
 *
 * @author Zheng Jie
 * @date 2019-01-17
 */
@Slf4j
public class SecurityUtils {

    public static final String UN_AUTHORIZED = "Unauthorized";
    public static ReactiveUserDetailsService reactiveUserDetailsService = null;

    /**
     * 获取当前登录的用户
     *
     * @return UserDetails
     */
    public static Mono<UserDetails> getCurrentUser() {
        if (reactiveUserDetailsService == null) {
            reactiveUserDetailsService = ApplicationContextHolder.getBean(ReactiveUserDetailsService.class);
        }
        return getCurrentUsername().flatMap(userName-> reactiveUserDetailsService.findByUsername(userName));
    }

    /**
     * 获取系统用户名称
     *
     * @return 系统用户名称
     */
    public static Mono<String> getCurrentUsername() {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication == null) {
                return Mono.error(new ManagerBadRequestException(HttpStatus.UNAUTHORIZED, "当前登录状态过期"));
            }
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return Mono.just(userDetails.getUsername());
            }
            return Mono.error(new ManagerBadRequestException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息"));
        });

    }

    /**
     * 认证失败响应
     * @param objectMapper jackson序列化工具
     * @param response response
     * @param status 状态
     * @return /
     */
    public static Mono<Void> writeErrorMessage(ObjectMapper objectMapper, ServerHttpResponse response, HttpStatus status, String msg) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = null;
        try {
            body = objectMapper.writeValueAsString(new ResponseEntity(msg, status));
        } catch (JsonProcessingException e) {
            log.error("序列化ResponseEntity出现未知异常", e);
            body = UN_AUTHORIZED;
        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }
}
