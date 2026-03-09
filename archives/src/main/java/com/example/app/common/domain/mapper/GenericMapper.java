package com.example.app.common.domain.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface GenericMapper<T, U> {
    U mapToDto(T entity);

    T mapToEntity(U dto);

    List<U> mapToDtoList(List<T> entityList);

    List<U> mapToDtoList(Page<T> pageEntityList);

    List<T> mapToEntityList(List<U> dtoList);
}
