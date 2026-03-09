package com.example.app.services.staff.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.staff.controller.StaffController;
import com.example.app.services.staff.domain.dto.StaffDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StaffRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        StaffDto.Staff, StaffDto.StaffResponse> {
    public StaffRepresentationModelAssembler() {
        super(StaffController.class, StaffDto.StaffResponse.class);
    }

    @Override
    @NonNull
    public StaffDto.StaffResponse toModel(@NonNull StaffDto.Staff entity) {
        final var model = instantiateModel(entity);
        model.setStaff(entity);
        model.add(linkTo(methodOn(StaffController.class).getStaff(entity.getStaffId()))
                .withSelfRel());
        model.add(linkTo(methodOn(StaffController.class).getStaffList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.staffList));
        return model;
    }

    @Override
    @NonNull
    public CollectionModel<StaffDto.StaffResponse> toCollectionModel(
            @NonNull Iterable<? extends StaffDto.Staff> entities) {
        return super.toCollectionModel(entities);
    }
}
