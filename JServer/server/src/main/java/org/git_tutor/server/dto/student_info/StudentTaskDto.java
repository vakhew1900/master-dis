package org.git_tutor.server.dto.student_info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTaskDto {
    private Long id;
    private Integer number;
    private String description;
    private Double grade;
    private String feedback;
    private LocalDateTime submittedAt;
    private String status;
}
