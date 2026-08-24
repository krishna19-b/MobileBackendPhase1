package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.LoginRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.LoginResponse;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.InvalidCredentialsException;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (!user.getPassword()
                .equals(request.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        return new LoginResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                "Login successful"
        );
    }
}