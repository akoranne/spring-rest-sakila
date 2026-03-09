package com.example.app.services.rental.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.rental.controller.RentalController;
import com.example.app.services.rental.domain.dto.RentalDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RentalRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        RentalDto.Rental, RentalDto.RentalResponse> {
    public RentalRepresentationModelAssembler() {
        super(RentalController.class, RentalDto.RentalResponse.class);
    }

    @Override
    @NonNull
    public RentalDto.RentalResponse toModel(@NonNull RentalDto.Rental entity) {
        final var model = instantiateModel(entity);
        model.setRental(entity);
        model.add(linkTo(methodOn(RentalController.class).getRental(entity.getRentalId()))
                .withSelfRel());
        model.add(linkTo(methodOn(RentalController.class).getRentalList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.rentalList));
        return model;
    }
}
