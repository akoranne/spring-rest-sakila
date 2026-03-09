package com.example.app.services.payment.assembler;

import com.example.app.common.constant.HalRelation;
import com.example.app.services.payment.controller.PaymentController;
import com.example.app.services.payment.domain.dto.PaymentDto;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PaymentRepresentationModelAssembler extends RepresentationModelAssemblerSupport<
        PaymentDto.Payment, PaymentDto.PaymentResponse> {
    public PaymentRepresentationModelAssembler() {
        super(PaymentController.class, PaymentDto.PaymentResponse.class);
    }

    @Override
    @NonNull
    public PaymentDto.PaymentResponse toModel(@NonNull PaymentDto.Payment entity) {
        final var model = instantiateModel(entity);
        model.setPayment(entity);
        model.add(linkTo(methodOn(PaymentController.class).getPayment(entity.getPaymentId()))
                .withSelfRel());
        model.add(linkTo(methodOn(PaymentController.class).getPaymentList(Pageable.unpaged()))
                .withRel(HalRelation.Fields.paymentList));
        return model;
    }
}
