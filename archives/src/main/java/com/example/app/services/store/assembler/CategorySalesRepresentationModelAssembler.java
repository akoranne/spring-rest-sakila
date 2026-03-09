package com.example.app.services.store.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.store.controller.ReportController;
import com.example.app.services.store.domain.dto.CategorySalesDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategorySalesRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        CategorySalesDto.CategorySales, CategorySalesDto.CategorySalesResponse> {
    public CategorySalesRepresentationModelAssembler() {
        super(ReportController.class, CategorySalesDto.CategorySalesResponse.class);
    }

    @Override
    @NonNull
    public CategorySalesDto.CategorySalesResponse toModel(@NonNull CategorySalesDto.CategorySales entity) {
        final var model = instantiateModel(entity);
        model.setCategorySales(entity);
        return model;
    }

    @Override
    @NonNull
    public CollectionModel<CategorySalesDto.CategorySalesResponse> toCollectionModel(
            @NonNull Iterable<? extends CategorySalesDto.CategorySales> entities) {
        final var collectionModel = super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(ReportController.class).reportSalesByCategory())
                .withSelfRel());
        collectionModel.add(linkTo(methodOn(ReportController.class).reportSalesByStore())
                .withRel(HalRelation.Fields.storeSales));
        return collectionModel;
    }
}
