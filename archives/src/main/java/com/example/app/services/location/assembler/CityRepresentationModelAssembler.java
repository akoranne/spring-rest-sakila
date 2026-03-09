package com.example.app.services.location.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.location.controller.LocationController;
import com.example.app.services.location.domain.dto.CityDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CityRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        CityDto.City, CityDto.CityResponse> {
    public CityRepresentationModelAssembler() {
        super(LocationController.class, CityDto.CityResponse.class);
    }

    @Override
    @NonNull
    public CityDto.CityResponse toModel(@NonNull CityDto.City entity) {
        final var model = instantiateModel(entity);
        model.setCity(entity);
        model.add(linkTo(methodOn(LocationController.class).getCity(entity.getCityId()))
                .withSelfRel());
        model.add(linkTo(methodOn(LocationController.class).getAddressList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.addressList));
        return model;
    }
}
