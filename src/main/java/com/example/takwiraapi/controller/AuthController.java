package com.example.takwiraapi.controller;

import com.example.takwiraapi.dto.LoginRequest;
import com.example.takwiraapi.dto.LoginResponse;
import com.example.takwiraapi.entity.User;
import com.example.takwiraapi.service.JWTService;
import com.example.takwiraapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername()));
    }
}