package com.example.app.services.payment.domain.mapper;

import com.example.app.common.domain.mapper.GenericMapper;
import com.example.app.services.payment.domain.dto.PaymentDto;
import com.example.app.services.payment.domain.entity.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface PaymentMapper extends GenericMapper<PaymentEntity, PaymentDto.Payment> {
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "customerByCustomerId", ignore = true)
    @Mapping(target = "staffByStaffId", ignore = true)
    @Mapping(target = "rentalByRentalId", ignore = true)
    PaymentEntity mapToEntity(PaymentDto.PaymentRequest dto);
}
