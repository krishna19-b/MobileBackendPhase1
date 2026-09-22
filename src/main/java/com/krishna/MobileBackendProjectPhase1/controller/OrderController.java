package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.request.OrderRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.OrderStatusUpdateRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.OrderResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.PageResponse;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.service.OrderServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderServiceImpl orderService;

    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create order", description = "Creates a new order. USER and ADMIN users can create orders.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid order data or insufficient stock"
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
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request) {

        OrderResponse order = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Order created successfully",
                        order
                ));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all orders",
            description = "Returns all orders with pagination and sorting. Only ADMIN users can access this endpoint."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "All orders retrieved successfully"
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
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAllOrders(
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sorting format, for example createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Page<OrderResponse> orders = orderService.getAllOrders(page, size, sort);

        PageResponse<OrderResponse> pageResponse = new PageResponse<>(
                orders.getContent(),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "All orders retrieved successfully",
                        pageResponse
                )
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "Get my orders",
            description = "Returns the authenticated user's orders with pagination and sorting."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User orders retrieved successfully"
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
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getMyOrders(
            Authentication authentication,

            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sorting format, for example createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        User user = (User) authentication.getPrincipal();

        Page<OrderResponse> orders =
                orderService.getOrdersByUser(user.getId(), page, size, sort);

        PageResponse<OrderResponse> pageResponse = new PageResponse<>(
                orders.getContent(),
                orders.getNumber(),
                orders.getSize(),
                orders.getTotalElements(),
                orders.getTotalPages()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Your orders retrieved successfully",
                        pageResponse
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DRIVER')")
    @Operation(
            summary = "Get order by ID",
            description = "Returns an order using its ID."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order retrieved successfully"
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
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(description = "Order ID", required = true)
            @PathVariable
            @Positive(message = "Order ID must be greater than 0") Long id) {

        OrderResponse order = orderService.getOrderById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Order retrieved successfully",
                        order
                )
        );
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update order status",
            description = "Updates the status of an order. Only ADMIN users can update the order status."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order status updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status"
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
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "Order ID", required = true)
            @PathVariable
            @Positive(message = "Order ID must be greater than 0") Long id,

            @Valid @RequestBody OrderStatusUpdateRequest request) {

        OrderResponse order = orderService.updateOrderStatus(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Order status updated successfully",
                        order
                )
        );
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "Cancel order",
            description = "Cancels an existing order. USER and ADMIN users can cancel orders."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order cancelled successfully"
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
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable
            @Positive(message = "Order ID must be greater than 0") Long id) {

        OrderResponse order = orderService.cancelOrder(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Order cancelled successfully",
                        order
                )
        );
    }

    @PutMapping("/{orderId}/driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Assign driver to order",
            description = "Assigns a driver to an order. Only ADMIN users can assign drivers."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Driver assigned successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid order or driver ID"
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
                    description = "Order or driver not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> assignDriver(
            @Parameter(description = "Order ID", required = true)
            @PathVariable
            @Positive(message = "Order ID must be greater than 0") Long orderId,

            @Parameter(description = "Driver user ID", required = true)
            @PathVariable
            @Positive(message = "Driver ID must be greater than 0") Long driverId) {

        OrderResponse order = orderService.assignDriver(orderId, driverId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Driver assigned successfully",
                        order
                )
        );
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(
            summary = "Get assigned orders",
            description = "Returns orders assigned to the authenticated driver."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Assigned orders retrieved successfully"
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
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAssignedOrders(
            Authentication authentication,

            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sorting format, for example createdAt,desc")
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        User driver = (User) authentication.getPrincipal();

        Page<OrderResponse> orders =
                orderService.getAssignedOrders(driver.getId(), page, size, sort);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Assigned orders retrieved successfully",
                        orders
                )
        );
    }

    @PutMapping("/{id}/driver-status")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(
            summary = "Update assigned order status",
            description = "Allows the authenticated driver to update the delivery status of an assigned order."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order delivery status updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status"
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
                    description = "Order not found"
            )
    })
    public ResponseEntity<ApiResponse<OrderResponse>> updateDriverOrderStatus(
            @Parameter(description = "Order ID", required = true)
            @PathVariable
            @Positive(message = "Order ID must be greater than 0") Long id,

            @Valid @RequestBody OrderStatusUpdateRequest request) {

        OrderResponse order =
                orderService.updateDriverOrderStatus(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Order delivery status updated successfully",
                        order
                )
        );
    }
}