package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.LoginRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}