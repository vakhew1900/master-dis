package org.master.diploma.git.graph.dto.merged_graph;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
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

    @SerializedName("merged_graph")
    private GitGraphDto mergedGraph;

    @Override
    public String toString() {
       return GSON.toJson(this);
    }
}
