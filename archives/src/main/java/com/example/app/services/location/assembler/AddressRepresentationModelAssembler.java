package com.example.app.services.location.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.location.controller.LocationController;
import com.example.app.services.location.domain.dto.AddressDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AddressRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        AddressDto.Address, AddressDto.AddressResponse> {
    public AddressRepresentationModelAssembler() {
        super(LocationController.class, AddressDto.AddressResponse.class);
    }

    @Override
    @NonNull
    public AddressDto.AddressResponse toModel(@NonNull AddressDto.Address entity) {
        final var model = instantiateModel(entity);
        model.setAddress(entity);
        model.add(linkTo(methodOn(LocationController.class).getAddress(entity.getAddressId()))
                .withSelfRel());
        model.add(linkTo(methodOn(LocationController.class).getAddressList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.addressList));
        return model;
    }

    @Override
    @NonNull
    public CollectionModel<AddressDto.AddressResponse> toCollectionModel(
            @NonNull Iterable<? extends AddressDto.Address> entities) {
        final var collectionModel = super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(LocationController.class).getAddressList(Pageable.unpaged()))
                .withSelfRel());
        return collectionModel;
    }
}
