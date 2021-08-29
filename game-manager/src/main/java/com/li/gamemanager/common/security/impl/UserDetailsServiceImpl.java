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
package com.li.gamemanager.common.security.impl;

import com.li.gamemanager.common.entity.User;
import com.li.gamemanager.common.exception.ManagerBadRequestException;
import com.li.gamemanager.common.model.JwtUserDto;
import com.li.gamemanager.common.properties.LoginProperties;
import com.li.gamemanager.common.service.RoleService;
import com.li.gamemanager.common.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author li-yuanwen
 */
@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserService userService;
    private final RoleService roleService;
    private final LoginProperties loginProperties;


    /**
     * 用户信息缓存
     */
    static Map<String, JwtUserDto> userDtoCache = new ConcurrentHashMap<>();

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        boolean searchDb = true;
        JwtUserDto jwtUserDto = null;
        if (loginProperties.isCacheEnable() && userDtoCache.containsKey(username)) {
            jwtUserDto = userDtoCache.get(username);
            searchDb = false;
        }
        if (searchDb) {
            User user;
            try {
                user = userService.findByName(username);
            } catch (Exception e) {
                // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
                // throw new UsernameNotFoundException("", e);
                return Mono.error(e);
            }
            if (user == null) {
                return Mono.error(new UsernameNotFoundException(username));
            } else {
                if (!user.getEnabled()) {
                    return Mono.error(new ManagerBadRequestException("账号未激活！"));
                }
                Set<String> functionPermissions = roleService.findById(user.getRole()).getFunctionPermissions();
                List<GrantedAuthority> authorities = new ArrayList<>(functionPermissions.size());
                functionPermissions.forEach(k->authorities.add(new SimpleGrantedAuthority(k)));
                jwtUserDto = new JwtUserDto(user.getUserName(), user.getPassword(), functionPermissions
                        , user.getAvatarPath(), user.getAvatarName(), user.getEnabled()
                        , authorities);
                userDtoCache.put(username, jwtUserDto);
            }
        }
        return Mono.just(jwtUserDto);
    }

}
