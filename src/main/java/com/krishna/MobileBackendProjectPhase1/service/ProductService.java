package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.ProductRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.ProductResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Category;
import com.krishna.MobileBackendProjectPhase1.entity.Product;
import com.krishna.MobileBackendProjectPhase1.exception.CategoryNotFoundException;
import com.krishna.MobileBackendProjectPhase1.exception.DuplicateProductException;
import com.krishna.MobileBackendProjectPhase1.exception.ProductNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.CategoryRepository;
import com.krishna.MobileBackendProjectPhase1.repository.ProductRepository;
import com.krishna.MobileBackendProjectPhase1.specification.ProductSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    // CREATE PRODUCT

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsByNameIgnoreCase(request.getName())) {

            throw new DuplicateProductException("Product already exists: "+ request.getName());
        }
      Product product=new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());

        Product savedProduct = productRepository.save(product);

        return new ProductResponse(savedProduct);
    }

    // GET ALL PRODUCTS

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);

        Page<Product> products = productRepository.findAll(pageable);

        return products.map(ProductResponse::new);
    }

    // =========================================
    // GET PRODUCT BY ID
    // =========================================

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {

        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        return new ProductResponse(product);
    }

    // UPDATE PRODUCT
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product =
                productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (productRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {

            throw new DuplicateProductException("Product already exists: " + request.getName());
        }

        Category category =
                categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return new ProductResponse(updatedProduct);
    }

    // SEARCH + FILTER

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String name, int page, int size, String sort, String category, Double minPrice, Double maxPrice) {

        Pageable pageable = createPageable(page, size, sort);

        Specification<Product> specification = ProductSpecification.filter(name, category, minPrice, maxPrice);

        Page<Product> products = productRepository.findAll(specification, pageable);

        return products.map(ProductResponse::new);
    }

    // DELETE PRODUCT

    @Transactional
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    // PAGINATION + SORT

    private Pageable createPageable(int page, int size, String sort) {

        String[] sortParts = sort.split(",");

        String property = sortParts[0];

        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}