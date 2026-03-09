package com.example.app.services.payment.controller;

import com.example.app.services.auth.domain.vo.UserRole;
import com.example.app.services.payment.assembler.PaymentRepresentationModelAssembler;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepresentationModelAssembler paymentAssembler;

    @GetMapping(path = "")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<PaymentDto.PaymentResponse>> getPaymentList(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(paymentAssembler.toCollectionModel(
                paymentService.getPaymentList(pageable)));
    }

    @GetMapping(path = "/{paymentId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<PaymentDto.PaymentResponse> getPayment(@PathVariable Integer paymentId) {
        return paymentService.getPayment(paymentId)
                .map(paymentAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{paymentId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> updatePayment(@PathVariable Integer paymentId,
                                              @RequestBody PaymentDto.PaymentRequest model) {
        final var result = paymentService.updatePayment(paymentId, model);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{paymentId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> deletePayment(@PathVariable Integer paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{paymentId}/details")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<PaymentDto.PaymentResponse> getPaymentDetails(@PathVariable Integer paymentId) {
        return paymentService.getPaymentDetails(paymentId)
                .map(paymentAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
