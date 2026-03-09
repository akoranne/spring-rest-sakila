package com.example.app.services.customer.repository.custom;

import com.example.app.services.customer.domain.dto.CustomerDetailsDto;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.rental.domain.vo.RentalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomCustomerRepository {
    List<CustomerDetailsDto.CustomerDetails> findAllCustomerDetailsList();

    Optional<CustomerDetailsDto.CustomerDetails> findCustomerDetailsById(Integer customerId);

    List<PaymentDto.Payment> findAllCustomerPaymentListByIdWithCondition(Integer customerId, LocalDate startDate, LocalDate endDate);

    List<RentalDto.Rental> findAllCustomerRentalListByIdWithCondition(Integer customerId, RentalStatus status, LocalDate startDate, LocalDate endDate);
}
