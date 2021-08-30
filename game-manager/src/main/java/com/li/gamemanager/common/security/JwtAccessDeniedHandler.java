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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.gamemanager.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author li-yuanwen
 */
@Component
public class JwtAccessDeniedHandler implements ServerAccessDeniedHandler {

   @Autowired
   private ObjectMapper objectMapper;

   @Override
   public Mono<Void> handle(ServerWebExchange serverWebExchange, AccessDeniedException e) {
      ServerHttpResponse response = serverWebExchange.getResponse();
      return SecurityUtils.writeErrorMessage(objectMapper, response, HttpStatus.FORBIDDEN, e.getMessage());
   }
}
