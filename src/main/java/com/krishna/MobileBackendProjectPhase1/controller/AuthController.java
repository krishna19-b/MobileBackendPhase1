package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.request.LoginRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.RegisterRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.LoginResponse;
import com.krishna.MobileBackendProjectPhase1.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "User registered successfully",
                                request.getEmail()
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        response
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestParam String refreshToken) {

        LoginResponse response =
                authService.refreshToken(refreshToken);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Access token refreshed successfully",
                        response
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestParam String refreshToken) {

        authService.logout(refreshToken);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Logout successful",
                        null
                )
        );
    }
}