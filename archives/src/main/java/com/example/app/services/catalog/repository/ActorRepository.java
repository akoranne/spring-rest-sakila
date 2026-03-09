package com.example.app.services.catalog.repository;

import com.example.app.services.catalog.domain.entity.ActorEntity;
import com.example.app.services.catalog.repository.custom.CustomActorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<ActorEntity, Integer>, CustomActorRepository {
    List<ActorEntity> findActorEntitiesByFullNameFirstNameContainsOrFullNameLastNameContains(String firstName, String lastName);
}
