package org.project.model.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CylinderTypeOptionDto {
    @JsonProperty("cylinder_type_id")
    private Long cylinderTypeId;
    private String name;
}
