package com.example.app.services.payment.service;

import com.example.app.common.exception.ResourceNotFoundException;
import com.example.app.services.customer.domain.dto.CustomerDto;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.payment.domain.mapper.PaymentMapper;
import com.example.app.services.payment.repository.PaymentRepository;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.staff.domain.dto.StaffDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto.Payment> getPaymentList(Pageable pageable) {
        final var list = paymentRepository.findAll(pageable);
        return paymentMapper.mapToDtoList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDto.Payment> getPayment(Integer paymentId) {
        final var entity = paymentRepository.findById(paymentId).orElseThrow(() ->
                new ResourceNotFoundException("Payment not found with id '" + paymentId + "'"));
        return Optional.of(paymentMapper.mapToDto(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto.Payment> getPaymentDetailsList() {
        return paymentRepository.findAllPaymentDetailsList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDto.Payment> getPaymentDetails(Integer paymentId) {
        final var model = paymentRepository.findPaymentDetailsById(paymentId);
        if (model.isEmpty()) {
            throw new ResourceNotFoundException("Payment not found with id '" + paymentId + "'");
        }
        return model;
    }

    @Override
    @Transactional
    public PaymentDto.Payment createPayment(PaymentDto.PaymentRequest model, CustomerDto.Customer customer,
                                            StaffDto.Staff staff, RentalDto.Rental rental) {
        final var payment = PaymentDto.Payment.builder()
                .customerId(customer.getCustomerId())
                .staffId(staff.getStaffId())
                .rentalId(model.getRentalId())
                .amount(model.getAmount())
                .paymentDate(model.getPaymentDate())
                .customerByCustomerId(customer)
                .staffByStaffId(staff)
                .rentalByRentalId(rental)
                .build();
        final var savedEntity = paymentRepository.save(paymentMapper.mapToEntity(payment));
        return paymentMapper.mapToDto(savedEntity);
    }

    @Override
    @Transactional
    public PaymentDto.Payment updatePayment(Integer paymentId, PaymentDto.PaymentRequest model) {
        final var entity = paymentRepository.findById(paymentId).orElseThrow(() ->
                new ResourceNotFoundException("Payment not found with id '" + paymentId + "'"));
        entity.update(paymentMapper.mapToEntity(model));
        return paymentMapper.mapToDto(entity);
    }

    @Override
    @Transactional
    public void deletePayment(Integer paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
