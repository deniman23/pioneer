package org.example.pioneer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.pioneer.dto.request.LoginRequest;
import org.example.pioneer.dto.response.LoginResponse;
import org.example.pioneer.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.getIdentifier(), req.getPassword());
        return new LoginResponse(token);
    }
}