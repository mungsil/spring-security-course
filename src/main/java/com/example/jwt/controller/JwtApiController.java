package com.example.jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtApiController {

    @GetMapping("/home")
    public String home() {
        return "집집";
    }
}
