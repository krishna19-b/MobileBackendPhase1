package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.projection.BestSellingProductProjection;
import com.krishna.MobileBackendProjectPhase1.dto.projection.OrderAnalyticsProjection;
import com.krishna.MobileBackendProjectPhase1.dto.projection.TopCustomerProjection;
import com.krishna.MobileBackendProjectPhase1.dto.response.OrderStatisticsResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Order;
import com.krishna.MobileBackendProjectPhase1.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderAnalyticsService {

    private final OrderRepository orderRepository;

    public OrderAnalyticsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<Order> getHighValueOrders(BigDecimal amount, Pageable pageable) {
        return orderRepository.findHighValueOrders(amount, pageable);
    }

    public Page<TopCustomerProjection> getTopCustomers(Pageable pageable) {
        return orderRepository.findTopCustomers(pageable);
    }

    public Page<BestSellingProductProjection> getBestSellingProducts(Pageable pageable) {
        return orderRepository.findBestSellingProducts(pageable);
    }

    public OrderStatisticsResponse getOrderStatistics() {
        OrderAnalyticsProjection result = orderRepository.getOrderStatistics();

        return new OrderStatisticsResponse(
                result.getTotalOrders(),
                result.getTotalRevenue(),
                result.getAverageOrderValue()
        );
    }
}