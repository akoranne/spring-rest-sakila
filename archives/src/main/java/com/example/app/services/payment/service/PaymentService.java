package com.example.app.services.payment.service;

import com.example.app.services.customer.domain.dto.CustomerDto;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.staff.domain.dto.StaffDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    List<PaymentDto.Payment> getPaymentList(Pageable pageable);

    Optional<PaymentDto.Payment> getPayment(Integer paymentId);

    List<PaymentDto.Payment> getPaymentDetailsList();

    Optional<PaymentDto.Payment> getPaymentDetails(Integer paymentId);

    PaymentDto.Payment createPayment(PaymentDto.PaymentRequest model, CustomerDto.Customer customer, StaffDto.Staff staff, RentalDto.Rental rental);

    PaymentDto.Payment updatePayment(Integer paymentId, PaymentDto.PaymentRequest model);

    void deletePayment(Integer paymentId);
}
