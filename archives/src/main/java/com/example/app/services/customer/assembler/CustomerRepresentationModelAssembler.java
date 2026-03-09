package com.example.app.services.customer.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.customer.controller.CustomerController;
import com.example.app.services.customer.domain.dto.CustomerDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CustomerRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        CustomerDto.Customer, CustomerDto.CustomerResponse> {
    public CustomerRepresentationModelAssembler() {
        super(CustomerController.class, CustomerDto.CustomerResponse.class);
    }

    @Override
    @NonNull
    public CustomerDto.CustomerResponse toModel(@NonNull CustomerDto.Customer entity) {
        final var model = instantiateModel(entity);
        model.setCustomer(entity);
        model.add(linkTo(methodOn(CustomerController.class).getCustomer(entity.getCustomerId()))
                .withSelfRel());
        model.add(linkTo(methodOn(CustomerController.class).getCustomerList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.customerList));
        return model;
    }
}
