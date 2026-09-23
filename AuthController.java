package com.example.secureapp.controller;

import com.example.secureapp.dto.AuthResponse;
import com.example.secureapp.dto.LoginRequest;
import com.example.secureapp.dto.RegisterRequest;
import com.example.secureapp.dto.UserResponse;
import com.example.secureapp.security.LoginRateLimiter;
import com.example.secureapp.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final LoginRateLimiter rateLimiter;

    public AuthController(AuthService authService, LoginRateLimiter rateLimiter) {
        this.authService = authService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiter.allowed(ip)) {
            return ResponseEntity.status(429).build();
        }

        return ResponseEntity.ok(authService.login(request));
    }
}
