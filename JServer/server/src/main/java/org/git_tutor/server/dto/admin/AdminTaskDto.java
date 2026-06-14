package org.git_tutor.server.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTaskDto {
    private Long id;
    private Integer number;
    private String description;
    private String referenceRepoPath;
}
