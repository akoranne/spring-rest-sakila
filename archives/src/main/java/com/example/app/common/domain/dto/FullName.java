package com.example.app.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@ToString
@FieldNameConstants
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullName {
    @JsonProperty("firstName")
    @Size(min = 1, max = 45)
    private String firstName;

    @JsonProperty("lastName")
    @Size(min = 1, max = 45)
    private String lastName;
}
