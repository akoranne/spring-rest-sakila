package com.example.app.services.auth.domain.converter;

import com.example.app.services.auth.domain.vo.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Converter
public class UserRoleConverter implements AttributeConverter<EnumSet<UserRole>, String> {
    @Override
    public String convertToDatabaseColumn(EnumSet<UserRole> attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException();
        }
        return attribute.stream()
                .map(UserRole::getAuthority)
                .collect(Collectors.joining(","));
    }

    @Override
    public EnumSet<UserRole> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new IllegalArgumentException();
        }
        return Arrays.stream(dbData.split(","))
                .map(UserRole::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(UserRole.class)));
    }
}
