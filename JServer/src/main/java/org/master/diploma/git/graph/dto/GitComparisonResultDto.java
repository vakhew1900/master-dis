package org.master.diploma.git.graph.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;

/**
 * Data Transfer Object representing the result of comparing two commit graphs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitComparisonResultDto {

    public static class FIELDS {
        public static final String FIRST_GRAPH = "first_graph";
        public static final String SECOND_GRAPH = "second_graph";
        public static final String COMPARE_RESULT = "compare_result";
    }

    @SerializedName(FIELDS.FIRST_GRAPH)
    private GitGraphDto firstGraph;

    @SerializedName(FIELDS.SECOND_GRAPH)
    private GitGraphDto secondGraph;

    @SerializedName(FIELDS.COMPARE_RESULT)
    private GraphCompareResult compareResult;

    /**
     * Constructs a GitComparisonResultDto by transforming CommitGraphs and the comparison result.
     *
     * @param commitGraph1        the first graph to transform
     * @param commitGraph2        the second graph to transform
     * @param graphCompareResult the result of the comparison between the two graphs
     */
    public GitComparisonResultDto(CommitGraph commitGraph1, CommitGraph commitGraph2, GraphCompareResult graphCompareResult) {
        this.firstGraph = GitGraphDto.from(commitGraph1);
        this.secondGraph = GitGraphDto.from(commitGraph2);
        this.compareResult = graphCompareResult;
    }
}
