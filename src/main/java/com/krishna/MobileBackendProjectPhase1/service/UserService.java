package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.userRequest.UserRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.userRequest.UserUpdateRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.UserResponse;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface UserService {

    UserResponse createUser(@Valid UserRequest request);

    Page<UserResponse> getAllUsers(int page, int size, String sort);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    Page<UserResponse> searchUsers(String name, int page, int size, String sort);
}