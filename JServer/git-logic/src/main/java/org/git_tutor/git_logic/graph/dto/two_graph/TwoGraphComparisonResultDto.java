package org.git_tutor.git_logic.graph.dto.two_graph;

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
 * DTO for the traditional side-by-side comparison of two Git graphs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for the traditional side-by-side comparison of two Git graphs.")
public class TwoGraphComparisonResultDto implements GitComparisonResultDto {

    private static final Gson GSON = new Gson();

    @Override
    public String getType() {
        return "two_graph";
    }

    public static class FIELDS {
        public static final String FIRST_GRAPH = "first_graph";
        public static final String SECOND_GRAPH = "second_graph";
        public static final String COMPARE_RESULT = "compare_result";
    }

    @NotNull
    @SerializedName(FIELDS.FIRST_GRAPH)
    @Schema(name = FIELDS.FIRST_GRAPH, description = "The first graph data")
    private GitGraphDto firstGraph;

    @NotNull
    @SerializedName(FIELDS.SECOND_GRAPH)
    @Schema(name = FIELDS.SECOND_GRAPH, description = "The second graph data")
    private GitGraphDto secondGraph;

    @NotNull
    @SerializedName(FIELDS.COMPARE_RESULT)
    @Schema(name = FIELDS.COMPARE_RESULT, description = "The comparison mapping results")
    private CompareResultDto compareResult;

    @Override
    public String toString() {
       return GSON.toJson(this);
    }
}
