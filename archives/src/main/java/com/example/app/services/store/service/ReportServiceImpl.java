package com.example.app.services.store.service;

import com.example.app.services.payment.repository.PaymentRepository;
import com.example.app.services.store.domain.dto.CategorySalesDto;
import com.example.app.services.store.domain.dto.StoreSalesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategorySalesDto.CategorySales> reportSalesByCategory() {
        return paymentRepository.calculateSalesByCategory();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreSalesDto.StoreSales> reportSalesByStore() {
        return paymentRepository.calculateSalesByStore();
    }
}
