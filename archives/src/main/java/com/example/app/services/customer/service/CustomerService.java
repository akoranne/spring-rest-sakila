package com.example.app.services.customer.service;

import com.example.app.services.customer.domain.dto.CustomerDetailsDto;
import com.example.app.services.customer.domain.dto.CustomerDto;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.rental.domain.vo.RentalStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerDto.Customer> getCustomerList(Pageable pageable);

    Optional<CustomerDto.Customer> getCustomer(Integer customerId);

    List<CustomerDetailsDto.CustomerDetails> getCustomerDetailsList();

    Optional<CustomerDetailsDto.CustomerDetails> getCustomerDetails(Integer customerId);

    List<PaymentDto.Payment> getCustomerPaymentList(Integer customerId, String startDate, String endDate);

    List<RentalDto.Rental> getCustomerRentalList(Integer customerId, RentalStatus status, String startDate, String endDate);

    CustomerDto.Customer addCustomer(CustomerDto.CustomerRequest model);

    CustomerDto.Customer updateCustomer(Integer customerId, CustomerDto.CustomerRequest model);

    void deleteCustomer(Integer customerId);
}
