package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.request.LoginRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.LoginResponse;
import com.krishna.MobileBackendProjectPhase1.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse loginResponse =
                authService.login(request);

        ApiResponse<LoginResponse> response =
                new ApiResponse<>(
                        true,
                        "Login successful",
                        loginResponse
                );

        return ResponseEntity.ok(response);
    }
}