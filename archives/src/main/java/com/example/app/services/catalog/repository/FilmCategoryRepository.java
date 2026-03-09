package com.example.app.services.catalog.repository;

import com.example.app.services.catalog.domain.entity.FilmCategoryEntity;
import com.example.app.services.catalog.repository.custom.CustomFilmCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmCategoryRepository extends JpaRepository<FilmCategoryEntity, Integer>, CustomFilmCategoryRepository {
}
