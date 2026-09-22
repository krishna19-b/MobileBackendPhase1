package com.krishna.MobileBackendProjectPhase1.controller.userContoller;

import com.krishna.MobileBackendProjectPhase1.dto.request.userRequest.UserRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.userRequest.UserUpdateRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.PageResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.UserResponse;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.service.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "Users", description = "User management APIs")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DRIVER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get my profile",
            description = "Returns the profile of the currently authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        UserResponse userResponse = userService.getUserById(user.getId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile retrieved successfully",
                        userResponse
                )
        );
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DRIVER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update my profile",
            description = "Updates the profile of the currently authenticated user."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Duplicate email or mobile number"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request) {

        User user = (User) authentication.getPrincipal();

        UserResponse userResponse =
                userService.updateUser(user.getId(), request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile updated successfully",
                        userResponse
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create user",
            description = "Creates a new user. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Duplicate email or mobile number"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request) {

        UserResponse userResponse = userService.createUser(request);

        ApiResponse<UserResponse> response =
                new ApiResponse<>(
                        true,
                        "User created successfully",
                        userResponse
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of users. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "Page must be greater than or equal to 0")
            int page,

            @Parameter(
                    description = "Number of users per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            @Positive(message = "Size must be greater than 0")
            int size,

            @Parameter(
                    description = "Sorting field and direction",
                    example = "createdAt,desc"
            )
            @RequestParam(defaultValue = "createdAt,desc")
            String sort) {

        Page<UserResponse> users =
                userService.getAllUsers(page, size, sort);

        PageResponse<UserResponse> pageResponse =
                new PageResponse<>(
                        users.getContent(),
                        users.getNumber(),
                        users.getSize(),
                        users.getTotalElements(),
                        users.getTotalPages()
                );

        ApiResponse<PageResponse<UserResponse>> response =
                new ApiResponse<>(
                        true,
                        "Users retrieved successfully",
                        pageResponse
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get user by ID",
            description = "Returns a user using the user ID. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @PathVariable
            @Positive(message = "ID must be greater than 0")
            Long id) {

        UserResponse userResponse =
                userService.getUserById(id);

        ApiResponse<UserResponse> response =
                new ApiResponse<>(
                        true,
                        "User retrieved successfully",
                        userResponse
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update user",
            description = "Updates an existing user. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Duplicate email or mobile number"
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @PathVariable
            @Positive(message = "ID must be greater than 0")
            Long id,

            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse userResponse =
                userService.updateUser(id, request);

        ApiResponse<UserResponse> response =
                new ApiResponse<>(
                        true,
                        "User updated successfully",
                        userResponse
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete user",
            description = "Deletes a user. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(
                    description = "User ID",
                    example = "1"
            )
            @PathVariable
            @Positive(message = "ID must be greater than 0")
            Long id) {

        userService.deleteUser(id);

        ApiResponse<Void> response =
                new ApiResponse<>(
                        true,
                        "User deleted successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Search users",
            description = "Searches users by first name with pagination and sorting. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users found successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid search or pagination parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(
            @Parameter(
                    description = "First name to search for",
                    example = "Arjun"
            )
            @RequestParam String firstName,

            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "Number of users per page",
                    example = "5"
            )
            @RequestParam(defaultValue = "5") int size,

            @Parameter(
                    description = "Sorting field and direction",
                    example = "createdAt,desc"
            )
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Page<UserResponse> users =
                userService.searchUsers(
                        firstName,
                        page,
                        size,
                        sort
                );

        PageResponse<UserResponse> pageResponse =
                new PageResponse<>(
                        users.getContent(),
                        users.getNumber(),
                        users.getSize(),
                        users.getTotalElements(),
                        users.getTotalPages()
                );

        ApiResponse<PageResponse<UserResponse>> response =
                new ApiResponse<>(
                        true,
                        "Users found successfully",
                        pageResponse
                );

        return ResponseEntity.ok(response);
    }
}