package com.example.app.services.customer.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.customer.controller.CustomerController;
import com.example.app.services.customer.domain.dto.CustomerDetailsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CustomerDetailsRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        CustomerDetailsDto.CustomerDetails, CustomerDetailsDto.CustomerDetailsResponse> {
    public CustomerDetailsRepresentationModelAssembler() {
        super(CustomerDetailsDto.CustomerDetails.class, CustomerDetailsDto.CustomerDetailsResponse.class);
    }

    @Override
    @NonNull
    public CustomerDetailsDto.CustomerDetailsResponse toModel(@NonNull CustomerDetailsDto.CustomerDetails entity) {
        final var model = instantiateModel(entity);
        model.setCustomerDetailsModel(entity);
        model.add(linkTo(methodOn(CustomerController.class).getCustomerDetails(entity.getId()))
                .withSelfRel());
        model.add(linkTo(methodOn(CustomerController.class).getCustomer(entity.getId()))
                .withRel(HalRelation.Fields.customer));
        model.add(linkTo(methodOn(CustomerController.class).getCustomerList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.customerList));
        return model;
    }
}
