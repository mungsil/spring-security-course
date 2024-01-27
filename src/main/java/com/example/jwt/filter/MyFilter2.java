package com.example.jwt.filter;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
//스프링 시큐리티 필터를 모두 거치고 난 후에 실행됨
@Component
public class MyFilter2 implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("필터2");
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
