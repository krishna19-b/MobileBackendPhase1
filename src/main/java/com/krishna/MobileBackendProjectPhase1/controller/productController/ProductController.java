package com.krishna.MobileBackendProjectPhase1.controller.productController;

import com.krishna.MobileBackendProjectPhase1.dto.request.ProductRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.PageResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.ProductResponse;
import com.krishna.MobileBackendProjectPhase1.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create product",
            description = "Creates a new product. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Product already exists"
            )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Product created successfully",
                        response
                ));
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Returns paginated products with sorting."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or sorting parameters"
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "Number of products per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size,

            @Parameter(
                    description = "Sorting field and direction",
                    example = "name,asc"
            )
            @RequestParam(defaultValue = "name,asc") String sort) {

        PageResponse<ProductResponse> pageResponse =productService.getAll(page, size, sort);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Products retrieved successfully",
                        pageResponse
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Returns a product using its ID."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(
                    description = "Product ID",
                    example = "51"
            )
            @PathVariable Long id) {

        ProductResponse product =
                productService.getById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Product retrieved successfully",
                        product
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update product",
            description = "Updates an existing product. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Concurrent update or duplicate product"
            )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(
                    description = "Product ID",
                    example = "51"
            )
            @PathVariable Long id,

            @Valid @RequestBody ProductRequest request) {

        ProductResponse product =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Product updated successfully",
                        product
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete product",
            description = "Deletes a product. ADMIN role required."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(
                    description = "Product ID",
                    example = "51"
            )
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Product deleted successfully",
                        null
                )
        );
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search products",
            description = "Searches products using name, category and price filters with pagination and sorting."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid search or pagination parameters"
            )
    })
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(
            @Parameter(
                    description = "Product name filter",
                    example = "Product 10"
            )
            @RequestParam(required = false) String name,

            @Parameter(
                    description = "Page number, starting from 0",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "Number of products per page",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int size,

            @Parameter(
                    description = "Sorting field and direction",
                    example = "name,asc"
            )
            @RequestParam(defaultValue = "name,asc") String sort,

            @Parameter(
                    description = "Category name filter",
                    example = "Electronics"
            )
            @RequestParam(required = false) String category,

            @Parameter(
                    description = "Minimum product price",
                    example = "500.00"
            )
            @RequestParam(required = false) Double minPrice,

            @Parameter(
                    description = "Maximum product price",
                    example = "5000.00"
            )
            @RequestParam(required = false) Double maxPrice) {

        Page<ProductResponse> products =
                productService.searchProducts(
                        name,
                        page,
                        size,
                        sort,
                        category,
                        minPrice,
                        maxPrice
                );

        PageResponse<ProductResponse> pageResponse =
                new PageResponse<>(
                        products.getContent(),
                        products.getNumber(),
                        products.getSize(),
                        products.getTotalElements(),
                        products.getTotalPages()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Products retrieved successfully",
                        pageResponse
                )
        );
    }
}