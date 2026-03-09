package com.example.app.util;

import com.epages.restdocs.apispec.ConstrainedFields;
import lombok.RequiredArgsConstructor;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ConstrainedFieldDocumentation {
    private final Class<?> clazz;

    public List<FieldDescriptor> restdocsFields(List<FieldDescriptor> requestFieldList) {
        final var constrainedFieldAttribute = new ConstrainedFieldAttribute(clazz);
        return requestFieldList.stream()
                .map(fieldDescriptor -> {
                    final var validationConstraints = constrainedFieldAttribute.constrainedAttribute(fieldDescriptor.getPath());
                    return fieldDescriptor.attributes(validationConstraints);
                })
                .collect(Collectors.toList());
    }

    public List<FieldDescriptor> openapiFields(List<FieldDescriptor> requestFieldList) {
        final var constrainedFieldDescriptor = new ConstrainedFields(clazz);
        return requestFieldList.stream()
                .map(fieldDescriptor -> {
                    final var type = fieldDescriptor.getType();
                    final var optional = fieldDescriptor.isOptional();
                    final var ignored = fieldDescriptor.isIgnored();
                    final var description = fieldDescriptor.getDescription();

                    final var field = constrainedFieldDescriptor.withPath(fieldDescriptor.getPath());
                    field.type(type).description(description);
                    if (optional) {
                        field.optional();
                    }
                    if (ignored) {
                        field.ignored();
                    }
                    return field;
                })
                .collect(Collectors.toList());
    }
}
