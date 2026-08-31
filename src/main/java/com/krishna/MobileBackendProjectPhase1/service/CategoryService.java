package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.CategoryRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.CategoryResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Category;
import com.krishna.MobileBackendProjectPhase1.exception.DuplicateCategoryException;
import com.krishna.MobileBackendProjectPhase1.exception.CategoryNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateCategoryException(
                    "Category already exists: " + request.getName()
            );
        }

        Category category = new Category();

        category.setName(request.getName());

        Category savedCategory = categoryRepository.save(category);

        return new CategoryResponse(savedCategory);
    }

    // GET ALL
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(CategoryResponse::new)
                .toList();
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        return new CategoryResponse(category);
    }

    // UPDATE
    @Transactional
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        if (categoryRepository.existsByName(request.getName())
                && !category.getName().equals(request.getName())) {

            throw new DuplicateCategoryException(
                    "Category already exists: " + request.getName()
            );
        }

        category.setName(request.getName());

        Category updatedCategory = categoryRepository.save(category);

        return new CategoryResponse(updatedCategory);
    }

    // DELETE
    @Transactional
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        categoryRepository.delete(category);
    }
}