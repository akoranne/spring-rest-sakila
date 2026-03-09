package com.example.app.services.rental.repository;

import com.example.app.services.rental.domain.entity.RentalEntity;
import com.example.app.services.rental.repository.custom.CustomRentalRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<RentalEntity, Integer>, CustomRentalRepository {
}
