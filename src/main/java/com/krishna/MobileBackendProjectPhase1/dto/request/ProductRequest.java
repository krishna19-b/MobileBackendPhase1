package com.krishna.MobileBackendProjectPhase1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be greater than 0")
    private Long categoryId;

    @NotNull(message = "Stock quantity is required")
    @Positive(message = "Stock quantity must be greater than 0")
    private Integer stockQuantity;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }


    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}