package com.example.jwt.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
//스프링 시큐리티 필터를 모두 거치고 난 후에 실행됨
@Component
public class MyFilter1 implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("필터1");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getMethod().equals("POST")) {
            System.out.println("POST 요청");
            String headerAuth = request.getHeader("Authorization");
            System.out.println(headerAuth);

            // 토큰:Bearer를 만들어줘야한다. id,pw가 정상적으로 들어와 로그인이 완료되면 토큰을 만들어주고 해당 토큰을 반환해준다.
            // 요청을 할 때마다 헤더에 Authorization 값으로 토큰을 가지고 오므로
            // 검증 로직만 구현해주면 된다. -> RSA, HS256으로.

            if (headerAuth.contains("Bearer")) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                System.out.println("바보");
            }
        }
    }
}
