package com.example.app.services.customer.service;

import com.example.app.common.exception.ResourceNotFoundException;
import com.example.app.services.customer.domain.dto.CustomerDetailsDto;
import com.example.app.services.customer.domain.dto.CustomerDto;
import com.example.app.services.customer.domain.mapper.CustomerMapper;
import com.example.app.services.customer.repository.CustomerRepository;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.rental.domain.vo.RentalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto.Customer> getCustomerList(Pageable pageable) {
        final var list = customerRepository.findAll(pageable);
        return customerMapper.mapToDtoList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDto.Customer> getCustomer(Integer customerId) {
        final var entity = customerRepository.findById(customerId).orElseThrow(() ->
                new ResourceNotFoundException("Customer not found with id '" + customerId + "'"));
        return Optional.of(customerMapper.mapToDto(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDetailsDto.CustomerDetails> getCustomerDetailsList() {
        return customerRepository.findAllCustomerDetailsList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDetailsDto.CustomerDetails> getCustomerDetails(Integer customerId) {
        final var model = customerRepository.findCustomerDetailsById(customerId);
        if (model.isEmpty()) {
            throw new ResourceNotFoundException("Customer not found with id '" + customerId + "'");
        }
        return model;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto.Payment> getCustomerPaymentList(Integer customerId, String startDate, String endDate) {
        return customerRepository.findAllCustomerPaymentListByIdWithCondition(
                customerId, LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDto.Rental> getCustomerRentalList(Integer customerId, RentalStatus status, String startDate, String endDate) {
        return customerRepository.findAllCustomerRentalListByIdWithCondition(
                customerId, status, LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @Override
    @Transactional
    public CustomerDto.Customer addCustomer(CustomerDto.CustomerRequest model) {
        final var savedEntity = customerRepository.save(customerMapper.mapToEntity(model));
        return customerMapper.mapToDto(savedEntity);
    }

    @Override
    @Transactional
    public CustomerDto.Customer updateCustomer(Integer customerId, CustomerDto.CustomerRequest model) {
        final var entity = customerRepository.findById(customerId).orElseThrow(() ->
                new ResourceNotFoundException("Customer not found with id '" + customerId + "'"));
        entity.update(customerMapper.mapToEntity(model));
        return customerMapper.mapToDto(entity);
    }

    @Override
    @Transactional
    public void deleteCustomer(Integer customerId) {
        customerRepository.deleteById(customerId);
    }
}
