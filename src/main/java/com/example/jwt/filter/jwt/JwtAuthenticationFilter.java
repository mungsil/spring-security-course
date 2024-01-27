package com.example.jwt.filter.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.auth.PrincipalDetails;
import com.example.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.util.Date;


// [기존] '/login' 요청하여 username, password을 post로 전송하면 UsernamePasswordAuthenticationFilter가 동작한다.
// [현재] formLogin::disable 해놓았으므로 'login' 요청이 들어와도 해당 필터가 작동하지 않는다.
// [해결] 스프링 시큐리티 필터에 UsernamePasswordAuthenticationFilter 역할을 하는 JwtAuthenticationFilter를 등록하자.

// [why] 다른 필터를 사용하면 안될까?


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager manager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        /**
         * 1.username, password 담아서
         * 2.authenticationManager는 Provider에게 인증 위임
         * 3.Provider는 UserDetailsService의 loadUserByUsername 메소드를 호출
         * 4.UserDetails를 세션에 담음 (세션에 담겨 있어야 권한 관리 가능하다)
         * 5.JWT를 생성 후 응답
         */

        //1
        try {
            //Json 타입 요청 시 객체로 바인딩
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(request.getInputStream(), User.class);
            System.out.println(user);

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            //principalDetailsService의 loadUserByUsername() 함수 실행
            Authentication authentication = manager.authenticate(token);

            // 세션 영역에 저장되어 있는 UserDetails 꺼내오기
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("attemptAuthentication 실행, username : "+principal.getUser().getUsername());

            //authentication객체를 반환해줌으로써 이를 session영역에 저장
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // attemptAuthentication 실행 후에 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨
    // JWT 토큰을 만들어서 request 요청한 사용자에게 response 해주자.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행");

        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();

        //Hash암호 방식
        String jwt = JWT.create()
                .withSubject("jwt")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProterties.EXPIRATION_TIME)) //60000=1분
                .withClaim("id", principal.getUser().getId())
                .withClaim("username", principal.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProterties.SECRET));
        response.addHeader(JwtProterties.HEADER_STRING,JwtProterties.TOKEN_PREFIX+jwt);
    }
}
