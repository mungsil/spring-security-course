package com.example.jwt.controller;

import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JwtApiController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/home")
    public String home() {
        return "집집";
    }

    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest) {
        User user = User.builder()
                .roles("ROLE_USER")
                .username(joinRequest.getUsername())
                .password(passwordEncoder.encode(joinRequest.getPassword())).build();
        userRepository.save(user);
        return "회원가입 완료";
    }

    @GetMapping("/api/v1/manager")
    public String manager() {
        return "manager";
    }

    //manager, admin 권한만 접근 가능
    @GetMapping("/api/v1/user")
    public String user() {
        return "user";
    }

    //admin만 접근 가능
    @GetMapping("/api/v1/admin")
    public String admin() {
        return "admin";
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class JoinRequest {
        String username;
        String password;
    }
}
