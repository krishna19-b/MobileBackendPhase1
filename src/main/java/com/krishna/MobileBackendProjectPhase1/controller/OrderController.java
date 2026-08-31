package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.request.OrderRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.OrderResponse;
import com.krishna.MobileBackendProjectPhase1.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService =
                orderService;
    }

    // CREATE ORDER
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {

        OrderResponse order = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Order created successfully", order));
    }

    // GET ORDER

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable @Positive(message = "Order ID must be greater than 0")Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved successfully", order));
    }

    // CANCEL ORDER

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable @Positive(message = "Order ID must be greater than 0")Long id) {
        OrderResponse order = orderService.cancelOrder(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order cancelled successfully", order));
    }
}