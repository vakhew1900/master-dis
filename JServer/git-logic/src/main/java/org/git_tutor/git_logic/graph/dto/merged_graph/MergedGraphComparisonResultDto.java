package org.git_tutor.git_logic.graph.dto.merged_graph;

import io.swagger.v3.oas.annotations.media.Schema;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.git_tutor.git_logic.graph.dto.CompareResultDto;
import org.git_tutor.git_logic.graph.dto.GitComparisonResultDto;
import org.git_tutor.git_logic.graph.dto.samples.GitGraphDto;

/**
 * DTO for the merged view of two Git graphs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for the merged view of two Git graphs.")
public class MergedGraphComparisonResultDto implements GitComparisonResultDto {

    private static final Gson GSON = new Gson();

    @Override
    public String getType() {
        return "merged_graph";
    }

    public static class FIELDS {
        public static final String MERGED_GRAPH = "merged_graph";
        public static final String COMPARE_RESULT = "compare_result";
    }

    @SerializedName(FIELDS.MERGED_GRAPH)
    @NotNull
    @Schema(name = FIELDS.MERGED_GRAPH, description = "The merged graph visualization data")
    private GitGraphDto mergedGraph;

    @SerializedName(FIELDS.COMPARE_RESULT)
    @NotNull
    @Schema(name = FIELDS.COMPARE_RESULT, description = "The comparison mapping results")
    private CompareResultDto compareResult;

    @Override
    public String toString() {
       return GSON.toJson(this);
    }
}
