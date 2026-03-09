package com.example.app.services.store.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.store.controller.StoreController;
import com.example.app.services.store.domain.dto.StoreDetailsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StoreDetailsRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        StoreDetailsDto.StoreDetails, StoreDetailsDto.StoreDetailsResponse> {
    public StoreDetailsRepresentationModelAssembler() {
        super(StoreController.class, StoreDetailsDto.StoreDetailsResponse.class);
    }

    @Override
    @NonNull
    public StoreDetailsDto.StoreDetailsResponse toModel(@NonNull StoreDetailsDto.StoreDetails entity) {
        final var model = instantiateModel(entity);
        model.setStoreDetails(entity);
        model.add(linkTo(methodOn(StoreController.class).getStoreDetails(entity.getId()))
                .withSelfRel());
        model.add(linkTo(methodOn(StoreController.class).getStore(entity.getId()))
                .withRel(HalRelation.Fields.store));
        model.add(linkTo(methodOn(StoreController.class).getStoreList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.storeList));
        return model;
    }
}
