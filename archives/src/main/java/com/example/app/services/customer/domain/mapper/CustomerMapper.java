package com.example.app.services.customer.domain.mapper;

import com.example.app.common.domain.mapper.GenericMapper;
import com.example.app.services.customer.domain.dto.CustomerDto;
import com.example.app.services.customer.domain.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface CustomerMapper extends GenericMapper<CustomerEntity, CustomerDto.Customer> {
    @Override
    @Mapping(target = "storeByStoreId", ignore = true)
    @Mapping(target = "addressByAddressId", ignore = true)
    @Mapping(target = "authorityByAuthorityId", ignore = true)
    CustomerDto.Customer mapToDto(CustomerEntity entity);

    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "fullName.firstName", source = "firstName")
    @Mapping(target = "fullName.lastName", source = "lastName")
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "storeByStoreId", ignore = true)
    @Mapping(target = "addressByAddressId", ignore = true)
    @Mapping(target = "authorityByAuthorityId", ignore = true)
    CustomerEntity mapToEntity(CustomerDto.CustomerRequest dto);
}
