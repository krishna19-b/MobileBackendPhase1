package com.krishna.MobileBackendProjectPhase1.exception;

import com.krishna.MobileBackendProjectPhase1.dto.response.ErrorResponse;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // USER NOT FOUND - 404
    // =========================================================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "USER_NOT_FOUND"
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // =========================================================
    // PRODUCT NOT FOUND - 404
    // =========================================================

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "PRODUCT_NOT_FOUND"
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // =========================================================
    // CATEGORY NOT FOUND - 404
    // =========================================================

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(
            CategoryNotFoundException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "CATEGORY_NOT_FOUND"
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // =========================================================
    // DUPLICATE USER - 409
    // =========================================================

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(
            DuplicateUserException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "DUPLICATE_USER"
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // =========================================================
    // DUPLICATE PRODUCT - 409
    // =========================================================

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProduct(
            DuplicateProductException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "DUPLICATE_PRODUCT"
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // =========================================================
    // INVALID LOGIN - 401
    // =========================================================

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "INVALID_CREDENTIALS"
                );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    // =========================================================
    // ACCESS DENIED - 403
    // =========================================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex) {

        ErrorResponse response =
                new ErrorResponse(
                        false,
                        "Access denied",
                        "ACCESS_DENIED"
                );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    // =========================================================
    // VALIDATION ERRORS - 400
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>>
    handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // =========================================================
    // PATH VARIABLE / REQUEST PARAM VALIDATION - 400
    // =========================================================

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>>
    handleConstraintViolation(
            ConstraintViolationException ex) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        ex.getConstraintViolations()
                .forEach(violation ->
                        errors.put(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // =========================================================
    // UNEXPECTED EXCEPTION - 500
    // =========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex) {

        ErrorResponse response =
                new ErrorResponse(
                        false,
                        "An unexpected error occurred",
                        "INTERNAL_SERVER_ERROR"
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }


    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(
            InvalidRefreshTokenException ex) {

        ErrorResponse response = new ErrorResponse(
                false,
                ex.getMessage(),
                "INVALID_REFRESH_TOKEN"
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(
            OrderNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "ORDER_NOT_FOUND"
                ));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "INSUFFICIENT_STOCK"
                ));
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStatus(
            InvalidOrderStatusException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        false,
                        ex.getMessage(),
                        "INVALID_ORDER_STATUS"
                ));
    }
}