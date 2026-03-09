package com.example.app.services.catalog.repository;

import com.example.app.services.catalog.domain.entity.FilmEntity;
import com.example.app.services.catalog.repository.custom.CustomFilmRepository;
import com.example.app.services.catalog.repository.custom.RentalRateProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends JpaRepository<FilmEntity, Integer>, CustomFilmRepository {
    RentalRateProjection findRentalRateByFilmId(Integer filmId);
}
