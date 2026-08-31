package com.krishna.MobileBackendProjectPhase1.controller.userContoller;

import com.krishna.MobileBackendProjectPhase1.dto.request.userRequest.UserRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.userRequest.UserUpdateRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.PageResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.UserResponse;
import com.krishna.MobileBackendProjectPhase1.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Adding New User
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse userResponse = userService.createUser(request);

        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User created successfully", userResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get All User Details
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") @PositiveOrZero(message = "Page must be greater than 0") int page, @RequestParam(defaultValue = "10") @Positive(message = "Size must be greater than 0") int size, @RequestParam(defaultValue = "createdAt,desc") String sort)
    {
        Page<UserResponse> users = userService.getAllUsers(page, size, sort);
        PageResponse<UserResponse> pageResponse=new PageResponse<>(users.getContent(), users.getNumber(), users.getSize(), users.getTotalElements(), users.getTotalPages());
        ApiResponse<PageResponse<UserResponse>> response =new ApiResponse<>(true, "Users retrieved successfully", pageResponse);
        return ResponseEntity.ok(response);
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable @Positive(message = "ID must be greater than 0") Long id) {
        UserResponse userResponse = userService.getUserById(id);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User retrieved successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    // Update User
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
                                                                  @PathVariable @Positive(message = "ID must be greater than 0") Long id,
                                                                  @Valid @RequestBody UserUpdateRequest request)
    {
        UserResponse userResponse = userService.updateUser(id, request);
        ApiResponse<UserResponse> response = new ApiResponse<>(true, "User updated successfully", userResponse);
        return ResponseEntity.ok(response);
    }

    // Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable @Positive(message = "ID must be greater than 0") Long id)
    {

        userService.deleteUser(id);
        ApiResponse<Void> response = new ApiResponse<>(true, "User deleted successfully", null);
        return ResponseEntity.ok(response);
    }

 // Serach By Name
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(@RequestParam String firstName,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "5") int size,
                                                                       @RequestParam(defaultValue = "createdAt,desc") String sort) {
        Page<UserResponse> users = userService.searchUsers(firstName, page, size, sort);
        PageResponse<UserResponse> pageResponse=new PageResponse<>(users.getContent(), users.getNumber(), users.getSize(), users.getTotalElements(), users.getTotalPages());
        ApiResponse<PageResponse<UserResponse>> response = new ApiResponse<>(true, "Users found successfully", pageResponse);
        return ResponseEntity.ok(response);
    }
}