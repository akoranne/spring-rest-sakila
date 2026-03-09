package com.example.app.services.payment.repository;

import com.example.app.services.payment.domain.entity.PaymentEntity;
import com.example.app.services.payment.repository.custom.CustomPaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer>, CustomPaymentRepository {
}
