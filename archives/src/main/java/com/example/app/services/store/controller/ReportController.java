package com.example.app.services.store.controller;

import com.example.app.services.auth.domain.vo.UserRole;
import com.example.app.services.store.assembler.CategorySalesRepresentationModelAssembler;
import com.example.app.services.store.assembler.StoreSalesRepresentationModelAssembler;
import com.example.app.services.store.domain.dto.CategorySalesDto;
import com.example.app.services.store.domain.dto.StoreSalesDto;
import com.example.app.services.store.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final CategorySalesRepresentationModelAssembler categorySalesAssembler;
    private final StoreSalesRepresentationModelAssembler storeSalesAssembler;

    @GetMapping(path = "/sales/categories")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<CategorySalesDto.CategorySalesResponse>> reportSalesByCategory() {
        return ResponseEntity.ok(categorySalesAssembler.toCollectionModel(
                reportService.reportSalesByCategory()));
    }

    @GetMapping(path = "/sales/stores")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<StoreSalesDto.StoreSalesResponse>> reportSalesByStore() {
        return ResponseEntity.ok(storeSalesAssembler.toCollectionModel(
                reportService.reportSalesByStore()));
    }
}
