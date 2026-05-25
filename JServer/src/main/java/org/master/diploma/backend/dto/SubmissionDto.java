package org.master.diploma.backend.dto;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName(FIELDS.EXISTS)
    private boolean isExists;

    public static final class FIELDS {
        public static final String EXISTS = "exists";
    }
}
