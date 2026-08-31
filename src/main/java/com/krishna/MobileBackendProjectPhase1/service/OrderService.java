package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.OrderRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.OrderResponse;

import org.springframework.data.domain.Page;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(Long id);


    Page<OrderResponse> getOrdersByUser(Long userId, int page, int size, String sort);

    OrderResponse cancelOrder(Long orderId);
}