package org.master.diploma.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    private Integer number;
    private String description;
    private Double grade;
    private String feedback;
    private LocalDateTime submittedAt;
    private String status;
}
