package com.krishna.MobileBackendProjectPhase1.repository;

import com.krishna.MobileBackendProjectPhase1.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}