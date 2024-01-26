package com.example.jwt.config;

import com.example.jwt.filter.MyFilter1;
import com.example.jwt.filter.MyFilter2;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CorsFilter corsFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 커스텀 필터를 굳이 시큐리티 필터에 걸어줄 필요는 없다.
                // 스프링 시큐리티 필터 실행 이전, 즉 가장 먼저 거칠 필터로 설정해주고 싶다면 SecurityContextPersistenceFilter 이전에 추가하자.
                .addFilterBefore(new MyFilter1(),BasicAuthenticationFilter.class)
//                .addFilter(corsFilter)

                .csrf((csrf) -> csrf.disable())
                //세션 방식 안쓸래요.
                .sessionManagement(httpSecuritySessionManagementConfigurer
                        -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //formLogin 방식도 안쓸래요.
                .formLogin(AbstractHttpConfigurer::disable)
                //Authorization header에 토큰을 입력받는 Bearer 방식을 사용할거예요. 그래서 Basic방식은 안쓸겁니다.
                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/v1/user/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                        .requestMatchers("/api/v1/manager/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll());

        return http.build();
    }
}
