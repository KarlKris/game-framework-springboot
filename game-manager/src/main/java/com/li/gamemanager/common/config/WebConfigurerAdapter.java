package com.li.gamemanager.common.config;

import com.li.gamemanager.common.properties.FileProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author li-yuanwen
 * @date 2021/8/28 14:34
 **/
@Configuration
@EnableWebFlux
public class WebConfigurerAdapter implements WebFluxConfigurer {

    /** 文件配置 */
    private final FileProperties properties;

    public WebConfigurerAdapter(FileProperties properties) {
        this.properties = properties;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        FileProperties.ElPath path = properties.getPath();
        String avatarUtl = "file:" + path.getAvatar().replace("\\","/");
        String pathUtl = "file:" + path.getPath().replace("\\","/");
        CacheControl cacheControl = CacheControl.noCache();
        registry.addResourceHandler("/avatar/**").addResourceLocations(avatarUtl).setCacheControl(cacheControl);
        registry.addResourceHandler("/file/**").addResourceLocations(pathUtl).setCacheControl(cacheControl);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/").setCacheControl(cacheControl);
    }


}
