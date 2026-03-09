package com.example.app.services.catalog.repository;

import com.example.app.services.catalog.domain.entity.FilmTextEntity;
import com.example.app.services.catalog.repository.custom.CustomFilmTextRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmTextRepository extends JpaRepository<FilmTextEntity, Integer>, CustomFilmTextRepository {
}
