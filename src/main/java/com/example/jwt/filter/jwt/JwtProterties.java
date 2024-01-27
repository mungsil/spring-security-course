package com.example.jwt.filter.jwt;

public interface JwtProterties {
    String SECRET = "달콤한초콜릿만원에팝니다";
    int EXPIRATION_TIME = 60000 * 100;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
