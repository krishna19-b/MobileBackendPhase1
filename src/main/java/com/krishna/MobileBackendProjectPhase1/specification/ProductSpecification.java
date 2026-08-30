package com.krishna.MobileBackendProjectPhase1.specification;

import com.krishna.MobileBackendProjectPhase1.entity.Product;

import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> filter(
            String name,
            String category,
            Double minPrice,
            Double maxPrice) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            // Product name
            if (name != null && !name.isBlank()) {

                String search = "%" + name.trim().toLowerCase() + "%";

                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), search));
            }

            // Category
            if (category != null && !category.isBlank()) {

                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("category").get("name")), category.trim().toLowerCase()));
            }

            // Minimum price
            if (minPrice != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            // Maximum price
            if (maxPrice != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return predicate;};
    }
}