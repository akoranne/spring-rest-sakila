package com.example.app.services.store.service;

import com.example.app.services.store.domain.dto.CategorySalesDto;
import com.example.app.services.store.domain.dto.StoreSalesDto;

import java.util.List;

public interface ReportService {

    List<CategorySalesDto.CategorySales> reportSalesByCategory();

    List<StoreSalesDto.StoreSales> reportSalesByStore();
}
