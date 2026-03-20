package org.project.model.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitListDto {
    @JsonProperty("unit_id")
    private Long unitId;

    @JsonProperty("unit_name")
    private String unitName;
}
