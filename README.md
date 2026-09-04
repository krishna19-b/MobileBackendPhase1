# MobileBackendPhase1

Project Overview

MobileBackendProjectPhase1 is a backend application developed using Java, Spring Boot, Spring Data JPA, PostgreSQL, and Spring Security.

The project provides REST APIs for user management and backend operations, with a strong focus on authentication, authorization, data validation, exception handling, and API security.

Technologies Used
Java
Spring Boot
Spring Data JPA
Hibernate
PostgreSQL
Spring Security
JWT (JSON Web Token)
BCrypt
Jakarta Validation
Maven
REST APIs
Git & GitHub
Project Architecture

The application follows a layered architecture:

Client / Mobile Application
          ↓
      REST API
          ↓
     Controller
          ↓
       Service
          ↓
     Repository
          ↓
    Spring Data JPA
          ↓
      PostgreSQL

The project is organized into separate layers for better maintainability, readability, and scalability.

Main Modules
1. User Management

Implemented APIs for:

User registration
Get all users
Get user by ID
Update user
Delete user
Search users
Logged-in user (/me) operations
Duplicate email validation
Duplicate mobile number validation
2. DTO Management

DTOs are used to separate API request/response data from database entities.

Implemented:

Request DTOs
Response DTOs
Entity-to-DTO mapping
DTO validation

This helps prevent direct exposure of entity objects through REST APIs.

3. Database Integration

PostgreSQL is used as the relational database.

JPA relationships were implemented between:

User
Profile
Address
Order
OrderItem
Product
Category

Relationships include:

@OneToOne
@OneToMany
@ManyToOne

Additional concepts implemented:

Foreign keys
Lazy loading
Cascade operations
Entity mapping
4. Validation

Jakarta Validation is used to validate incoming API requests.

Examples of validations:

@NotBlank
@Email
@Size
@Pattern
@Positive
@PositiveOrZero

Validation is applied to request bodies and request parameters to prevent invalid data from entering the application.

5. Exception Handling

Centralized exception handling is implemented using:

@ControllerAdvice
@ExceptionHandler

Custom exceptions include:

UserNotFoundException
DuplicateUserException
Other application-specific exceptions

The application provides consistent error responses for different failure scenarios.

Spring Security
6. Authentication

Spring Security is used to secure the application.

The authentication flow includes:

Login Request
     ↓
AuthenticationManager
     ↓
UserDetailsService
     ↓
Password Verification
     ↓
JWT Access Token
     ↓
Client

Passwords are securely stored using BCrypt password hashing instead of plain-text passwords.

7. JWT Authentication

JWT-based authentication is implemented for protected APIs.

After successful login:

POST /api/auth/login
        ↓
Authentication
        ↓
JWT Access Token
        ↓
Client

For protected APIs, the client sends:

Authorization: Bearer <JWT_TOKEN>

The JWT filter:

Extracts the token from the Authorization header.
Validates the JWT.
Checks the token expiration.
Extracts the username/email from the token.
Loads user details.
Sets authentication in the Spring Security SecurityContext.

OncePerRequestFilter is used for JWT processing.

8. Authorization

Role-based authorization is implemented for:

USER
ADMIN
DRIVER

Method-level authorization is implemented using @PreAuthorize.

Examples:

@PreAuthorize("hasRole('ADMIN')")

and

@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DRIVER')")

Authentication determines who the user is, while authorization determines what the authenticated user is allowed to access.

9. User Ownership and IDOR Protection

User ownership protection is implemented for user-specific operations.

For /me APIs, the authenticated user's identity is obtained from the Spring Security context instead of accepting another user's ID directly from the client.

This helps prevent unauthorized access to another user's data and provides protection against Insecure Direct Object Reference (IDOR) issues.

Refresh Token
10. Refresh Token Management

Refresh-token functionality is implemented along with JWT access tokens.

The refresh token can be used to obtain a new access token when the existing access token expires.

Implemented functionality includes:

Refresh-token creation
Refresh-token validation
Refresh-token expiration
Refresh-token revocation
Logout
Prevention of reuse of revoked refresh tokens
API Security
11. CORS

CORS configuration is implemented to control communication between frontend/mobile applications and the backend API.

The configuration handles:

Allowed origins
Allowed HTTP methods
Allowed headers
Credentials
Preflight OPTIONS requests
12. CSRF

CSRF considerations were studied and configured based on the stateless JWT-based API architecture.

Since authentication is handled through JWT tokens rather than traditional server-side sessions, the security configuration is designed according to a stateless REST API approach.

13. Security Error Handling

Custom security error handling is implemented using:

AuthenticationEntryPoint

Returns:

401 Unauthorized

when authentication is missing or invalid.

AccessDeniedHandler

Returns:

403 Forbidden

when an authenticated user does not have sufficient permissions.

Pagination, Sorting and Search
14. Pagination

Pagination is implemented using:

Page
Pageable
PageRequest

Example:

GET /api/products?page=0&size=10
15. Sorting

Sorting is implemented using Spring Data's Sort.

Example:

GET /api/products?sort=price
16. Dynamic Search

Dynamic filtering/search is implemented using Spring Data JPA Specifications.

This allows records to be filtered based on different search conditions without creating multiple repository methods.

Testing and Troubleshooting

During development, several configuration and implementation issues were identified and resolved, including:

PasswordEncoder bean configuration
JWT secret configuration
JWT parsing issues
PostgreSQL connectivity
Hibernate dialect configuration
Authentication issues
Authorization issues
JWT filter processing
Role-based access restrictions
Refresh-token validation
API security errors

Troubleshooting these issues helped improve understanding of Spring Boot application startup, dependency injection, database connectivity, JWT processing, Spring Security filters, and authorization flow.

Current Status

The major backend functionality and security components have been implemented.

Completed / Implemented
User Management
DTOs
PostgreSQL Integration
JPA Entity Relationships
Request Validation
Global Exception Handling
Pagination
Sorting
Dynamic Search
BCrypt Password Hashing
Spring Security Authentication
JWT Authentication
JWT Filter
Role-Based Authorization
User Ownership Protection
IDOR Protection
Refresh Tokens
Refresh Token Revocation
Logout
CORS Configuration
CSRF Configuration
AuthenticationEntryPoint
AccessDeniedHandler
Current Focus

The current focus is on:

End-to-end API testing
Testing USER, ADMIN, and DRIVER scenarios
Verifying authorization restrictions
Testing JWT and refresh-token flows
Testing logout and token revocation
Improving API reliability
Security verification
Production-readiness improvements

