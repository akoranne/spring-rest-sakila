package com.example.app.services.rental.domain.mapper;

import com.example.app.common.domain.mapper.DoMapping;
import com.example.app.common.domain.mapper.GenericMapper;
import com.example.app.services.rental.domain.dto.RentalDto;
import com.example.app.services.rental.domain.entity.RentalEntity;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface RentalMapper extends GenericMapper<RentalEntity, RentalDto.Rental> {
    @Override
    @Mapping(target = "inventoryByInventoryId", ignore = true)
    @Mapping(target = "customerByCustomerId", ignore = true)
    @Mapping(target = "staffByStaffId", ignore = true)
    RentalDto.Rental mapToDto(RentalEntity entity);

    @Override
    @DoMapping
    @Mapping(target = "rentalId", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(LocalDateTime.now())")
    RentalEntity mapToEntity(RentalDto.Rental dto);

    @Mapping(target = "rentalId", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "inventoryByInventoryId", ignore = true)
    @Mapping(target = "customerByCustomerId", ignore = true)
    @Mapping(target = "staffByStaffId", ignore = true)
    RentalEntity mapToEntity(RentalDto.RentalUpdateRequest dto);

    @Mapping(target = "rentalId", ignore = true)
    @Mapping(target = "returnDate", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(LocalDateTime.now())")
    RentalEntity mapToRentEntity(RentalDto.Rental dto);

    @Override
    @IterableMapping(qualifiedBy = DoMapping.class)
    List<RentalEntity> mapToEntityList(List<RentalDto.Rental> dtoList);
}
