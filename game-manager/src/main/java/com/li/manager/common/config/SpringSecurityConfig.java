package com.li.manager.common.config;

import cn.hutool.core.util.ArrayUtil;
import com.li.manager.common.annotation.AnonymousAccess;
import com.li.manager.common.security.TokenFilter;
import com.li.manager.utils.RequestMethodEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author li-yuanwen
 * @date 2021/8/28 15:25
 **/
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SpringSecurityConfig {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ServerAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private ServerAccessDeniedHandler jwtAccessDeniedHandler;
    @Autowired
    private TokenFilter tokenFilter;

    /** 密码加密方式 **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // 去除 ROLE_ 前缀
        return new GrantedAuthorityDefaults("");
    }

    /** 跨域 **/
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://allowed-origin.com"));
        corsConfiguration.setMaxAge(8000L);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(corsConfigurationSource);
    }


    /** 提供用于获取UserDetails的Service **/
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService reactiveUserDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager
                = new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        // 设置密码加密方式
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http
            , ReactiveAuthenticationManager reactiveAuthenticationManager) {
        RequestMappingHandlerMapping handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        Map<String, Set<String>> anonymousUrl = getAnonymousUrl(handlerMethods);

        ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec = http
                // 禁用CSRF
                .csrf().disable()
                //
                .addFilterBefore(corsWebFilter(), SecurityWebFiltersOrder.CORS)
                .addFilterBefore(tokenFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authenticationManager(reactiveAuthenticationManager)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                //  防止iframe 造成跨域
                .and().headers().frameOptions().disable()
                // 路径认证配置
                .and().authorizeExchange()
                // 静态资源
                .pathMatchers(HttpMethod.GET
                        , "/*.html"
                        , "/**/*.html"
                        , "/**/*.css"
                        , "/**/*.js"
                        , "/webSocket/**").permitAll()
                // swagger文档
                .pathMatchers("/swagger-ui.html"
                        , "/swagger-resources/**"
                        , "/webjars/**"
                        , "/*/api-docs").permitAll()
                // 文件
                .pathMatchers("/avatar/**", "/file/**").permitAll()
                // 放行OPTIONS请求
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll();
        // 自定义匿名访问所有url放行：允许匿名和带Token访问，细腻化到每个 Request 类型
        // GET
        String[] getPatterns = anonymousUrl.get(RequestMethodEnum.GET.getType()).toArray(new String[0]);
        if (!ArrayUtil.isEmpty(getPatterns)) {
            authorizeExchangeSpec.pathMatchers(HttpMethod.GET, getPatterns).permitAll();
        }
        // POST
        String[] postPatterns = anonymousUrl.get(RequestMethodEnum.POST.getType()).toArray(new String[0]);
        if (!ArrayUtil.isEmpty(postPatterns)) {
            authorizeExchangeSpec.pathMatchers(HttpMethod.POST, postPatterns).permitAll();
        }
        // PUT
        String[] putPatterns = anonymousUrl.get(RequestMethodEnum.PUT.getType()).toArray(new String[0]);
        if (!ArrayUtil.isEmpty(putPatterns)) {
            authorizeExchangeSpec.pathMatchers(HttpMethod.PUT, putPatterns).permitAll();
        }
        // PATCH
        String[] patchPatterns = anonymousUrl.get(RequestMethodEnum.PATCH.getType()).toArray(new String[0]);
        if (!ArrayUtil.isEmpty(patchPatterns)) {
            authorizeExchangeSpec.pathMatchers(HttpMethod.PATCH, patchPatterns).permitAll();
        }
        // DELETE
        String[] deletePatterns = anonymousUrl.get(RequestMethodEnum.DELETE.getType()).toArray(new String[0]);
        if (!ArrayUtil.isEmpty(deletePatterns)) {
            authorizeExchangeSpec.pathMatchers(HttpMethod.DELETE, deletePatterns).permitAll();
        }
        // 所有类型的接口都放行
        String[] allPatterns = anonymousUrl.get(RequestMethodEnum.ALL.getType()).toArray(new String[0]);
        if (!ArrayUtil.isEmpty(allPatterns)) {
            authorizeExchangeSpec.pathMatchers(allPatterns).permitAll();
        }
        // 所有请求都需要认证
        return authorizeExchangeSpec.anyExchange().authenticated()
                // 登出
                .and().logout().disable().build();
    }

    private Map<String, Set<String>> getAnonymousUrl(Map<RequestMappingInfo, HandlerMethod> handlerMethodMap) {
        Map<String, Set<String>> anonymousUrls = new HashMap<>(6);
        Set<String> get = new HashSet<>();
        Set<String> post = new HashSet<>();
        Set<String> put = new HashSet<>();
        Set<String> patch = new HashSet<>();
        Set<String> delete = new HashSet<>();
        Set<String> all = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
            if (null != anonymousAccess) {
                List<RequestMethod> requestMethods = new ArrayList<>(infoEntry.getKey().getMethodsCondition().getMethods());
                RequestMethodEnum request = RequestMethodEnum.find(requestMethods.size() == 0 ? RequestMethodEnum.ALL.getType() : requestMethods.get(0).name());
                switch (Objects.requireNonNull(request)) {
                    case GET:
                        get.addAll(toPathPatterns(infoEntry.getKey()));
                        break;
                    case POST:
                        post.addAll(toPathPatterns(infoEntry.getKey()));
                        break;
                    case PUT:
                        put.addAll(toPathPatterns(infoEntry.getKey()));
                        break;
                    case PATCH:
                        patch.addAll(toPathPatterns(infoEntry.getKey()));
                        break;
                    case DELETE:
                        delete.addAll(toPathPatterns(infoEntry.getKey()));
                        break;
                    default:
                        all.addAll(toPathPatterns(infoEntry.getKey()));
                        break;
                }
            }
        }
        anonymousUrls.put(RequestMethodEnum.GET.getType(), get);
        anonymousUrls.put(RequestMethodEnum.POST.getType(), post);
        anonymousUrls.put(RequestMethodEnum.PUT.getType(), put);
        anonymousUrls.put(RequestMethodEnum.PATCH.getType(), patch);
        anonymousUrls.put(RequestMethodEnum.DELETE.getType(), delete);
        anonymousUrls.put(RequestMethodEnum.ALL.getType(), all);
        return anonymousUrls;
    }

    private List<String> toPathPatterns(RequestMappingInfo info) {
        return info.getPatternsCondition()
                .getPatterns()
                .stream()
                .map(PathPattern::toString)
                .collect(Collectors.toList());
    }
}
