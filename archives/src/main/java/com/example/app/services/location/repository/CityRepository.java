package com.example.app.services.location.repository;

import com.example.app.services.location.domain.entity.CityEntity;
import com.example.app.services.location.repository.custom.CustomCityRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Integer>, CustomCityRepository {
}
