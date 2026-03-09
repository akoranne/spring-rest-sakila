package com.example.app.services.location.repository;

import com.example.app.services.location.domain.entity.LanguageEntity;
import com.example.app.services.location.repository.custom.CustomLanguageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<LanguageEntity, Integer>, CustomLanguageRepository {
}
