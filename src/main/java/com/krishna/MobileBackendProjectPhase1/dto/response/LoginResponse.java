package com.krishna.MobileBackendProjectPhase1.dto.response;


public class LoginResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String message;

    public LoginResponse(
            Long userId,
            String firstName,
            String lastName,
            String email,
            String message) {

        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }
}
