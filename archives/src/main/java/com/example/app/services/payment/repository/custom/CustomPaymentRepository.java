package com.example.app.services.payment.repository.custom;

import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.store.domain.dto.CategorySalesDto;
import com.example.app.services.store.domain.dto.StoreSalesDto;

import java.util.List;
import java.util.Optional;

public interface CustomPaymentRepository {
    List<PaymentDto.Payment> findAllPaymentDetailsList();

    Optional<PaymentDto.Payment> findPaymentDetailsById(Integer paymentId);

    List<CategorySalesDto.CategorySales> calculateSalesByCategory();

    List<StoreSalesDto.StoreSales> calculateSalesByStore();
}
