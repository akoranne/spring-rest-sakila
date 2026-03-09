package com.example.app.services.staff.domain.mapper;

import com.example.app.common.domain.mapper.GenericMapper;
import com.example.app.services.staff.domain.dto.StaffDto;
import com.example.app.services.staff.domain.entity.StaffEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface StaffMapper extends GenericMapper<StaffEntity, StaffDto.Staff> {
    @Override
    @Mapping(target = "addressByAddressId", ignore = true)
    @Mapping(target = "storeByStoreId", ignore = true)
    @Mapping(target = "authorityByAuthorityId", ignore = true)
    StaffDto.Staff mapToDto(StaffEntity entity);

    @Mapping(target = "staffId", ignore = true)
    @Mapping(target = "fullName.firstName", source = "firstName")
    @Mapping(target = "fullName.lastName", source = "lastName")
    @Mapping(target = "lastUpdate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "addressByAddressId", ignore = true)
    @Mapping(target = "storeByStoreId", ignore = true)
    @Mapping(target = "authorityByAuthorityId", ignore = true)
    StaffEntity mapToEntity(StaffDto.StaffRequest dto);
}
