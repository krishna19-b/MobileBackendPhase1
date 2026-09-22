package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.projection.BestSellingProductProjection;
import com.krishna.MobileBackendProjectPhase1.dto.projection.TopCustomerProjection;
import com.krishna.MobileBackendProjectPhase1.dto.response.*;
import com.krishna.MobileBackendProjectPhase1.service.OrderAnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Order Analytics", description = "Order and sales analytics APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderAnalyticsController {

    private final OrderAnalyticsService orderAnalyticsService;

    public OrderAnalyticsController(OrderAnalyticsService orderAnalyticsService) {
        this.orderAnalyticsService = orderAnalyticsService;
    }

    @GetMapping("/orders/high-value")
    @Operation(
            summary = "Get high-value orders",
            description = "Returns orders whose total amount is greater than the specified amount."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "High-value orders retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid amount, page, or size"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getHighValueOrders(
            @Parameter(description = "Minimum order amount", required = true)
            @RequestParam BigDecimal amount,

            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("totalAmount").descending());

        Page<OrderResponse> orders = orderAnalyticsService.getHighValueOrders(amount, pageable)
                .map(OrderResponse::new);

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
                        "High-value orders retrieved successfully",
                        pageResponse
                )
        );
    }

    @GetMapping("/users/top-customers")
    @Operation(
            summary = "Get top customers",
            description = "Returns customers ranked according to order analytics."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Top customers retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid page or size"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<TopCustomerProjection>>> getTopCustomers(
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<TopCustomerProjection> customers =
                orderAnalyticsService.getTopCustomers(pageable);

        PageResponse<TopCustomerProjection> pageResponse = new PageResponse<>(
                customers.getContent(),
                customers.getNumber(),
                customers.getSize(),
                customers.getTotalElements(),
                customers.getTotalPages()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Top customers retrieved successfully",
                        pageResponse
                )
        );
    }

    @GetMapping("/products/best-selling")
    @Operation(
            summary = "Get best-selling products",
            description = "Returns products ranked by their sales performance."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Best-selling products retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid page or size"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<BestSellingProductProjection>>> getBestSellingProducts(
            @Parameter(description = "Page number, starting from 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of records per page")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<BestSellingProductProjection> products =
                orderAnalyticsService.getBestSellingProducts(pageable);

        PageResponse<BestSellingProductProjection> pageResponse = new PageResponse<>(
                products.getContent(),
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Best-selling products retrieved successfully",
                        pageResponse
                )
        );
    }

    @GetMapping("/orders/statistics")
    @Operation(
            summary = "Get order statistics",
            description = "Returns overall order statistics including total orders, total revenue, and average order value."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order statistics retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<ApiResponse<OrderStatisticsResponse>> getOrderStatistics() {

        OrderStatisticsResponse statistics =
                orderAnalyticsService.getOrderStatistics();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Order statistics retrieved successfully",
                        statistics
                )
        );
    }
}