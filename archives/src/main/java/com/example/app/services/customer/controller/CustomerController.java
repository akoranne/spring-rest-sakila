package com.example.app.services.customer.controller;

import com.example.app.services.auth.domain.vo.UserRole;
import com.example.app.services.customer.assembler.CustomerDetailsRepresentationModelAssembler;
import com.example.app.services.customer.assembler.CustomerRepresentationModelAssembler;
import com.example.app.services.customer.domain.dto.CustomerDetailsDto;
import com.example.app.services.customer.domain.dto.CustomerDto;
import com.example.app.services.customer.service.CustomerService;
import com.example.app.services.payment.assembler.PaymentRepresentationModelAssembler;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.rental.assembler.RentalRepresentationModelAssembler;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.rental.domain.vo.RentalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerRepresentationModelAssembler customerAssembler;
    private final CustomerDetailsRepresentationModelAssembler customerDetailsAssembler;
    private final PaymentRepresentationModelAssembler paymentAssembler;
    private final RentalRepresentationModelAssembler rentalAssembler;

    @GetMapping(path = "")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<CustomerDto.CustomerResponse>> getCustomerList(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(customerAssembler.toCollectionModel(
                customerService.getCustomerList(pageable)));
    }

    @PostMapping(path = "")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> addCustomer(@RequestBody CustomerDto.CustomerRequest model) {
        final var result = customerService.addCustomer(model);
        return ResponseEntity.created(linkTo(methodOn(CustomerController.class)
                .getCustomer(result.getCustomerId())).toUri()).build();
    }

    @GetMapping(path = "/{customerId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CustomerDto.CustomerResponse> getCustomer(@PathVariable Integer customerId) {
        return customerService.getCustomer(customerId)
                .map(customerAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{customerId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> updateCustomer(@PathVariable Integer customerId,
                                               @RequestBody CustomerDto.CustomerRequest model) {
        final var result = customerService.updateCustomer(customerId, model);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/{customerId}")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{customerId}/details")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CustomerDetailsDto.CustomerDetailsResponse> getCustomerDetails(
            @PathVariable Integer customerId) {
        return customerService.getCustomerDetails(customerId)
                .map(customerDetailsAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/{customerId}/payments")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<PaymentDto.PaymentResponse>> getCustomerPaymentList(
            @PathVariable Integer customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(paymentAssembler.toCollectionModel(
                customerService.getCustomerPaymentList(customerId, startDate, endDate)));
    }

    @GetMapping(path = "/{customerId}/rentals")
    @Secured(UserRole.Constants.ROLE_MANAGE)
    public ResponseEntity<CollectionModel<RentalDto.RentalResponse>> getCustomerRentalList(
            @PathVariable Integer customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        final var rentalStatus = status == null ? null : RentalStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(rentalAssembler.toCollectionModel(
                customerService.getCustomerRentalList(customerId, rentalStatus, startDate, endDate)));
    }
}
