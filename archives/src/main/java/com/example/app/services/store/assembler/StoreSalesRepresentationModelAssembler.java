package com.example.app.services.store.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.store.controller.ReportController;
import com.example.app.services.store.domain.dto.StoreSalesDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StoreSalesRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        StoreSalesDto.StoreSales, StoreSalesDto.StoreSalesResponse> {
    public StoreSalesRepresentationModelAssembler() {
        super(ReportController.class, StoreSalesDto.StoreSalesResponse.class);
    }

    @Override
    @NonNull
    public StoreSalesDto.StoreSalesResponse toModel(@NonNull StoreSalesDto.StoreSales entity) {
        final var model = instantiateModel(entity);
        model.setStoreSales(entity);
        return model;
    }

    @Override
    @NonNull
    public CollectionModel<StoreSalesDto.StoreSalesResponse> toCollectionModel(
            @NonNull Iterable<? extends StoreSalesDto.StoreSales> entities) {
        final var collectionModel = super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(ReportController.class).reportSalesByStore())
                .withSelfRel());
        collectionModel.add(linkTo(methodOn(ReportController.class).reportSalesByCategory())
                .withRel(HalRelation.Fields.categorySales));
        return collectionModel;
    }
}
