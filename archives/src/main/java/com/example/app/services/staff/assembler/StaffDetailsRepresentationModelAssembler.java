package com.example.app.services.staff.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.staff.controller.StaffController;
import com.example.app.services.staff.domain.dto.StaffDetailsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StaffDetailsRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        StaffDetailsDto.StaffDetails, StaffDetailsDto.StaffDetailsResponse> {
    public StaffDetailsRepresentationModelAssembler() {
        super(StaffController.class, StaffDetailsDto.StaffDetailsResponse.class);
    }

    @Override
    @NonNull
    public StaffDetailsDto.StaffDetailsResponse toModel(@NonNull StaffDetailsDto.StaffDetails entity) {
        final var model = instantiateModel(entity);
        model.setStaffDetails(entity);
        model.add(linkTo(methodOn(StaffController.class).getStaffDetails(entity.getId()))
                .withSelfRel());
        model.add(linkTo(methodOn(StaffController.class).getStaff(entity.getId()))
                .withRel(HalRelation.Fields.staff));
        model.add(linkTo(methodOn(StaffController.class).getStaffList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.staffList));
        return model;
    }
}
