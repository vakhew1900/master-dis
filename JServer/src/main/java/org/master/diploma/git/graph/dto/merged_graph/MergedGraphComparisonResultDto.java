package org.master.diploma.git.graph.dto.merged_graph;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.CompareResultDto;
import org.master.diploma.git.graph.dto.GitComparisonResultDto;
import org.master.diploma.git.graph.dto.samples.GitGraphDto;

/**
 * DTO for the merged view of two Git graphs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MergedGraphComparisonResultDto implements GitComparisonResultDto {

    private static final Gson GSON = new Gson();

    public static class FIELDS {
        public static final String MERGED_GRAPH = "merged_graph";
        public static final String COMPARE_RESULT = "compare_result";
    }

    @SerializedName(FIELDS.MERGED_GRAPH)
    private GitGraphDto mergedGraph;

    @SerializedName(FIELDS.COMPARE_RESULT)
    private CompareResultDto compareResult;

    @Override
    public String toString() {
       return GSON.toJson(this);
    }
}
