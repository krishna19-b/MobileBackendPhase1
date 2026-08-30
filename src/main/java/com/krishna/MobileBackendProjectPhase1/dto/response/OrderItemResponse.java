package com.krishna.MobileBackendProjectPhase1.dto.response;

import com.krishna.MobileBackendProjectPhase1.entity.OrderItem;

public class OrderItemResponse {

    private Long id;

    private Long productId;

    private String productName;

    private int quantity;

    private double price;

    private double subtotal;


    public OrderItemResponse(OrderItem item) {

        this.id = item.getId();

        this.productId =
                item.getProduct().getId();

        this.productName =
                item.getProduct().getName();

        this.quantity =
                item.getQuantity();

        this.price =
                item.getProduct().getPrice();

        this.subtotal =
                item.getProduct().getPrice()
                        * item.getQuantity();
    }


    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return subtotal;
    }
}