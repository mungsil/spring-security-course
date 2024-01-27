package com.example.jwt.config;

import com.example.jwt.filter.MyFilter1;
import com.example.jwt.filter.MyFilter2;
import com.example.jwt.filter.jwt.JwtAuthenticationFilter;
import com.example.jwt.filter.jwt.JwtAuthorizationFilter;
import com.example.jwt.repository.UserRepository;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final CorsFilter corsFilter;
    //우리가 구현한 PrincipalDetailService 주입
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        sharedObject.userDetailsService(this.userDetailsService);
        AuthenticationManager authenticationManager = sharedObject.build();
        http.authenticationManager(authenticationManager);

        http
                // 커스텀 필터를 굳이 시큐리티 필터에 걸어줄 필요는 없어요. MyFilter2처럼 필터로 등록해주는 방법도 있답니다.
                // 스프링 시큐리티 필터 실행 이전, 즉 가장 먼저 거칠 필터로 설정해주고 싶다면 SecurityContextPersistenceFilter 이전에 추가하자.
                //.addFilterBefore(new MyFilter1(),BasicAuthenticationFilter.class)

                .addFilter(corsFilter)
                .addFilter(new JwtAuthenticationFilter(authenticationManager))
                .addFilter(new JwtAuthorizationFilter(authenticationManager,userRepository))
                .csrf((csrf) -> csrf.disable())

                //세션 방식 안쓸래요.
                .sessionManagement(httpSecuritySessionManagementConfigurer
                        -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //formLogin 방식도 안쓸래요.
                // * 근데 이렇게 되면 /login 요청을 스프링 시큐리티가 낚아채지 않아요. 그래서 위에서 JwtAuthenticationFilter필터를 추가해줬어요!
                .formLogin(AbstractHttpConfigurer::disable)

                //Authorization header에 ''토큰''을 입력받는 Bearer 방식을 사용할거예요. 그래서 Basic방식은 안쓸겁니다.
                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/api/v1/user/**").hasAnyRole("ADMIN", "USER", "MANAGER")
                        .requestMatchers("/api/v1/manager/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll());

        return http.build();
    }
}
