package com.example.app.services.customer.repository;

import com.example.app.services.customer.domain.entity.CustomerEntity;
import com.example.app.services.customer.repository.custom.CustomCustomerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer>, CustomCustomerRepository {
}
