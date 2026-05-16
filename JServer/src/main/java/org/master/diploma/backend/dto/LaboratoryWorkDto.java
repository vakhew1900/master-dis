package org.master.diploma.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaboratoryWorkDto {
    private Long id;
    private Integer number;
    private String topic;
    private String description;
    private Double maxGrade;
    private List<TaskDto> tasks;
}
