package com.krishna.MobileBackendProjectPhase1.repository;

import com.krishna.MobileBackendProjectPhase1.entity.Product;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Product p
            WHERE p.id = :id
            """)
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
    @Query("""
            SELECT p
            FROM Product p
            JOIN p.category c
            WHERE c.name = :category
            """)
    Page<Product> findProductsByCategory(@Param("category") String category, Pageable pageable);
    @Query(
            """
             SELECT p from Product p join fetch p.category c
            """)
    Page<Product> findAll(Pageable pageable);

}