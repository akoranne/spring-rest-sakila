package com.example.app.services.store.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.store.controller.StoreController;
import com.example.app.services.store.domain.dto.StoreDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StoreRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        StoreDto.Store, StoreDto.StoreResponse> {
    public StoreRepresentationModelAssembler() {
        super(StoreController.class, StoreDto.StoreResponse.class);
    }

    @Override
    @NonNull
    public StoreDto.StoreResponse toModel(@NonNull StoreDto.Store entity) {
        final var model = instantiateModel(entity);
        model.setStore(entity);
        model.add(linkTo(methodOn(StoreController.class).getStore(entity.getStoreId()))
                .withSelfRel());
        model.add(linkTo(methodOn(StoreController.class).getStoreList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.storeList));
        return model;
    }
}
