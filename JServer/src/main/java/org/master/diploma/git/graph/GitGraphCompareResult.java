package org.master.diploma.git.graph;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Git-specific comparison result that includes information about movable (fuzzy-matched) vertices.
 */
@Getter
@Setter
public class GitGraphCompareResult extends GraphCompareResult {

    @SerializedName("movable_vertices")
    private Set<Integer> movableVertices = new HashSet<>();

    public GitGraphCompareResult() {
        super();
    }

    /**
     * Copy constructor that performs a deep copy of the base GraphCompareResult.
     */
    public GitGraphCompareResult(GraphCompareResult result) {
        var other = result.clone();
        this.setInvert(other.isInvert());
        this.setMatchingVertices(other.getMatchingVertices().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        this.setLabelErrors(other.getLabelErrors().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().clone())));
    }
}
