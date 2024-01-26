package com.example.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 내 서버가 응답 시 json을 javascript에서 처리할 수 있게 할지를 결정
        config.addAllowedOrigin("*");// 모든 ip에 응답 허용
        config.addAllowedMethod("*");// post, get, put, patch,,,등 모든 요청을 허용하겠다.
        config.addAllowedHeader("*");// 모든 header에 응답 허용

        source.registerCorsConfiguration("/api/*", config); // 커스텀 해놓은 config를 사용

        return new CorsFilter(source);
    }
}
