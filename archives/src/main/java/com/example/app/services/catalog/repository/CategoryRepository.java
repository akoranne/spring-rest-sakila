package com.example.app.services.catalog.repository;

import com.example.app.services.catalog.domain.entity.CategoryEntity;
import com.example.app.services.catalog.repository.custom.CustomCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer>, CustomCategoryRepository {
}
