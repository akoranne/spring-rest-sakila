package com.example.app.services.auth.repository;

import com.example.app.services.auth.domain.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Integer> {
    AuthorityEntity findAuthorityEntityByEmail(String email);
}
