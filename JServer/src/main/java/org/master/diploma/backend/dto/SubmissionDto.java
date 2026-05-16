package org.master.diploma.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionDto {
    private Long labId;
    private Integer labNumber;
    private Long taskId;
    private String taskName;
    private Long submissionId;
    private Double grade;
    private boolean isExists;
}
