package org.git_tutor.server.dto.student_info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentLabDto {
    private Long id;
    private Integer number;
    private String topic;
    private String description;
    private Double maxGrade;
    private List<StudentTaskDto> tasks;
}
