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
public class TaskDto {

    public static class FIELDS {
        public static final String ID = "id";
        public static final String NUMBER = "number";
        public static final String DESCRIPTION = "description";
        public static final String REFERENCE_REPO_PATH = "reference_repo_path";
    }

    @SerializedName(FIELDS.ID)
    private Long id;

    @SerializedName(FIELDS.NUMBER)
    private Integer number;

    @SerializedName(FIELDS.DESCRIPTION)
    private String description;

    @SerializedName(FIELDS.REFERENCE_REPO_PATH)
    private String referenceRepoPath;
}
