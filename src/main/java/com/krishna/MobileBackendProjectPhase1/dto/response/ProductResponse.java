package com.krishna.MobileBackendProjectPhase1.dto.response;

import com.krishna.MobileBackendProjectPhase1.entity.Product;

public class ProductResponse {

    private Long id;

    private String name;

    private double price;

    private int stockQuantity;

    private Long categoryId;

    private String categoryName;


    public ProductResponse(Product product) {

        this.id = product.getId();

        this.name = product.getName();

        this.price = product.getPrice();

        this.stockQuantity =
                product.getStockQuantity();

        this.categoryId =
                product.getCategory().getId();

        this.categoryName =
                product.getCategory().getName();
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}