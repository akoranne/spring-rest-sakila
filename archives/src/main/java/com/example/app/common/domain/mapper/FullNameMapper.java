package com.example.app.common.domain.mapper;

import com.example.app.common.domain.entity.FullName;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullNameMapper {
    FullName mapToFullName(String firstName, String lastName);
}
