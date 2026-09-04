package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.request.OrderRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.OrderStatusUpdateRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.OrderResponse;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // USER / ADMIN - CREATE ORDER
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Order created successfully", order));
    }


    // ADMIN - GET ALL ORDERS

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10")
                                                                          int size,
                                                                          @RequestParam(defaultValue = "createdAt,desc")
                                                                          String sort) {

           Page<OrderResponse> orders = orderService.getAllOrders(page, size, sort);

        return ResponseEntity.ok(new ApiResponse<>(true, "All orders retrieved successfully", orders));
    }


    // USER - GET MY ORDERS
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(Authentication authentication, @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "createdAt,desc") String sort) {
        User user = (User) authentication.getPrincipal();

        Page<OrderResponse> orders = orderService.getOrdersByUser(user.getId(), page, size, sort);

        return ResponseEntity.ok(new ApiResponse<>(true, "Your orders retrieved successfully", orders));
    }

    // USER / ADMIN / DRIVER - GET ORDER BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DRIVER')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable @Positive(message = "Order ID must be greater than 0") Long id) {

        OrderResponse order = orderService.getOrderById(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved successfully", order));
    }


    // ADMIN - UPDATE ORDER STATUS
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable @Positive(message = "Order ID must be greater than 0") Long id, @Valid @RequestBody OrderStatusUpdateRequest request)
    {
        OrderResponse order = orderService.updateOrderStatus(id, request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated successfully", order));
    }


    // USER / ADMIN - CANCEL ORDER
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable@Positive(message = "Order ID must be greater than 0") Long id) {

        OrderResponse order = orderService.cancelOrder(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Order cancelled successfully", order));
    }

    // ADMIN - ASSIGN DRIVER
    @PutMapping("/{orderId}/driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> assignDriver(@PathVariable @Positive(message = "Order ID must be greater than 0") Long orderId, @PathVariable @Positive(message = "Driver ID must be greater than 0") Long driverId) {

        OrderResponse order = orderService.assignDriver(orderId, driverId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Driver assigned successfully", order));
    }

    // DRIVER - GET ASSIGNED ORDERS
    @GetMapping("/assigned")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAssignedOrders(Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt,desc") String sort) {

        User driver = (User) authentication.getPrincipal();

        Page<OrderResponse> orders = orderService.getAssignedOrders(driver.getId(), page, size, sort);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Assigned orders retrieved successfully", orders));
    }

    // DRIVER - UPDATE ASSIGNED ORDER STATUS
    @PutMapping("/{id}/driver-status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateDriverOrderStatus(@PathVariable @Positive(message = "Order ID must be greater than 0") Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {

        OrderResponse order = orderService.updateDriverOrderStatus(id, request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Order delivery status updated successfully", order));
    }
}