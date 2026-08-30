package com.krishna.MobileBackendProjectPhase1.controller.productController;

import com.krishna.MobileBackendProjectPhase1.dto.request.ProductRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.ProductResponse;
import com.krishna.MobileBackendProjectPhase1.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE PRODUCT
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Product created successfully", response));
    }

    // GET ALL PRODUCTS

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
                                                                              @RequestParam(defaultValue = "0")
                                                                               int page,
                                                                              @RequestParam(defaultValue = "10")
                                                                              int size,
                                                                              @RequestParam(defaultValue = "name,asc") String sort) {

        Page<ProductResponse> products = productService.getAll(page, size, sort);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Products retrieved successfully", products));
    }

    // GET PRODUCT BY ID

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>>
    getProductById(@PathVariable Long id) {

        ProductResponse product = productService.getById(id);

        return ResponseEntity.ok(new ApiResponse<>(true, "Product retrieved successfully", product));
    }

    // UPDATE PRODUCT

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
                                                                         @PathVariable Long id,
                                                                         @Valid @RequestBody ProductRequest request) {

        ProductResponse product = productService.updateProduct(id, request);

        return ResponseEntity.ok(new ApiResponse<>(true, "Product updated successfully", product));
    }


    // DELETE PRODUCT

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product deleted successfully", null));
    }

    // SEARCH PRODUCTS

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
                                                                               @RequestParam(required = false) String name,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size,
                                                                               @RequestParam(defaultValue = "name,asc") String sort,
                                                                               @RequestParam(required = false) String category,
                                                                               @RequestParam(required = false) Double minPrice,
                                                                               @RequestParam(required = false) Double maxPrice)
    {

        Page<ProductResponse> products = productService.searchProducts(name, page, size, sort, category, minPrice, maxPrice);

        return ResponseEntity.ok(new ApiResponse<>(true, "Products retrieved successfully", products));
    }
}