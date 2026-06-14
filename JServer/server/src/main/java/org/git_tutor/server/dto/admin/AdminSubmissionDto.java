package org.git_tutor.server.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.git_tutor.server.dto.user.UserResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSubmissionDto {
    private Long id;
    private Long taskId;
    private String taskDescription;
    private UserResponseDto student;
    private String studentRepoPath;
    private Double grade;
    private String feedback;
    private LocalDateTime submittedAt;
}
