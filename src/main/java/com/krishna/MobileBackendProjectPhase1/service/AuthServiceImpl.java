package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.LoginRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.RegisterRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.LoginResponse;
import com.krishna.MobileBackendProjectPhase1.entity.RefreshToken;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.DuplicateUserException;
import com.krishna.MobileBackendProjectPhase1.exception.InvalidCredentialsException;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;
import com.krishna.MobileBackendProjectPhase1.util.JWTutil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTutil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JWTutil jwtUtil,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException(
                    "Email already registered: "
                            + request.getEmail()
            );
        }

        if (userRepository.existsByMobileNumber(
                request.getMobileNumber())) {

            throw new DuplicateUserException(
                    "Mobile number already registered: "
                            + request.getMobileNumber()
            );
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user.setRole("USER");
        user.setEnabled(true);

        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        try {

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = (User) authentication.getPrincipal();

            String accessToken = jwtUtil.token(user);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return new LoginResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), accessToken, refreshToken.getToken());

        } catch (Exception e) {

            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenService.verifyToken(refreshTokenValue);

        User user = refreshToken.getUser();

        String newAccessToken = jwtUtil.token(user);

        return new LoginResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), newAccessToken, refreshTokenValue);
    }

    @Override
    public void logout(String refreshTokenValue) {
        refreshTokenService.revokeToken(refreshTokenValue);
    }
}