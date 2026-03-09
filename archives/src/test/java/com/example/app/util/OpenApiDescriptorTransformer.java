package com.example.app.util;

import com.epages.restdocs.apispec.HeaderDescriptorWithType;
import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenApiDescriptorTransformer {
    public static List<HeaderDescriptorWithType> transformHeader(List<HeaderDescriptor> descriptorList) {
        return descriptorList.stream()
                .map(HeaderDescriptorWithType.Companion::fromHeaderDescriptor)
                .collect(Collectors.toList());
    }

    public static List<ParameterDescriptorWithType> transformParameter(List<ParameterDescriptor> descriptorList) {
        return descriptorList.stream()
                .map(ParameterDescriptorWithType.Companion::fromParameterDescriptor)
                .collect(Collectors.toList());
    }
}
