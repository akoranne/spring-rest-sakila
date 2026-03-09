package com.example.app.services.location.repository;

import com.example.app.services.location.domain.entity.AddressEntity;
import com.example.app.services.location.repository.custom.CustomAddressRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Integer>, CustomAddressRepository {
}
