package com.krishna.MobileBackendProjectPhase1.dto.response;

import com.krishna.MobileBackendProjectPhase1.entity.Order;
import com.krishna.MobileBackendProjectPhase1.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;

    private Long userId;

    private Long driverId;

    private String driverName;

    private String driverEmail;

    private double totalAmount;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> orderItems;


    public OrderResponse(Order order) {

        this.id = order.getId();

        this.userId = order.getUser().getId();

        // Driver may not be assigned yet
        if (order.getDriver() != null) {

            this.driverId = order.getDriver().getId();

            this.driverName =
                    order.getDriver().getFirstName()
                            + " "
                            + order.getDriver().getLastName();

            this.driverEmail =
                    order.getDriver().getEmail();
        }

        this.totalAmount = order.getTotalAmount();

        this.status = order.getStatus();

        this.createdAt = order.getCreatedAt();

        this.orderItems =
                order.getOrderItems()
                        .stream()
                        .map(OrderItemResponse::new)
                        .toList();
    }


    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemResponse> getOrderItems() {
        return orderItems;
    }
}