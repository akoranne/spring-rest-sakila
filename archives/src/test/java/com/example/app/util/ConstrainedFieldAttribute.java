package com.example.app.util;

import lombok.RequiredArgsConstructor;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.util.StringUtils;

import static org.springframework.restdocs.snippet.Attributes.key;

@RequiredArgsConstructor
public class ConstrainedFieldAttribute {
    private final ConstraintDescriptions constraintDescriptions;

    public ConstrainedFieldAttribute(Class<?> clazz) {
        this.constraintDescriptions = new ConstraintDescriptions(clazz);
    }

    public Attributes.Attribute constrainedAttribute(String path) {
        return key("validationConstraints").value(StringUtils.collectionToDelimitedString(
                this.constraintDescriptions.descriptionsForProperty(path), ". "));
    }
}
