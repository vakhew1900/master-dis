package org.git_tutor.git_logic.graph.dto.samples;

import io.swagger.v3.oas.annotations.media.Schema;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for a link (edge) between two commits in the Git graph.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for a link (edge) between two commits")
public class LinkDto {

    public static class FIELDS {
        public static final String SOURCE = "source";
        public static final String TARGET = "target";
    }

    @SerializedName(FIELDS.SOURCE)
    @Schema(name = FIELDS.SOURCE, description = "Hash of the source (parent) commit")
    private String source;

    @SerializedName(FIELDS.TARGET)
    @Schema(name = FIELDS.TARGET, description = "Hash of the target (child) commit")
    private String target;
}
