package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.request.CategoryRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ApiResponse;
import com.krishna.MobileBackendProjectPhase1.dto.response.CategoryResponse;
import com.krishna.MobileBackendProjectPhase1.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse category =
                categoryService.createCategory(request);

        ApiResponse<CategoryResponse> response =
                new ApiResponse<>(
                        true,
                        "Category created successfully",
                        category
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {

        List<CategoryResponse> categories =
                categoryService.getAllCategories();

        ApiResponse<List<CategoryResponse>> response =
                new ApiResponse<>(
                        true,
                        "Categories found successfully",
                        categories
                );

        return ResponseEntity.ok(response);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long id) {

        CategoryResponse category =
                categoryService.getCategoryById(id);

        ApiResponse<CategoryResponse> response =
                new ApiResponse<>(
                        true,
                        "Category found successfully",
                        category
                );

        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse category =
                categoryService.updateCategory(id, request);

        ApiResponse<CategoryResponse> response =
                new ApiResponse<>(
                        true,
                        "Category updated successfully",
                        category
                );

        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        ApiResponse<Void> response =
                new ApiResponse<>(
                        true,
                        "Category deleted successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }
}