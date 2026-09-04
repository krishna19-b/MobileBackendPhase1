package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.OrderRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.OrderStatusUpdateRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.OrderResponse;

import org.springframework.data.domain.Page;

public interface OrderService {

    // User
    OrderResponse createOrder(OrderRequest request);

    Page<OrderResponse> getOrdersByUser(
            Long userId,
            int page,
            int size,
            String sort
    );

    OrderResponse getOrderById(Long id);

    OrderResponse cancelOrder(Long orderId);

    // Admin
    Page<OrderResponse> getAllOrders(
            int page,
            int size,
            String sort
    );

    OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatusUpdateRequest request
    );

    // Driver
    Page<OrderResponse> getAssignedOrders(
            Long driverId,
            int page,
            int size,
            String sort
    );

    OrderResponse updateDriverOrderStatus(
            Long orderId,
            OrderStatusUpdateRequest request
    );

    // Admin assigns driver
    OrderResponse assignDriver(
            Long orderId,
            Long driverId
    );
}