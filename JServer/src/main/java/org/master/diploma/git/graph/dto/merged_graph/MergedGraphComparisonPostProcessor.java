package org.master.diploma.git.graph.dto.merged_graph;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.GitComparisonPostProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * Post-processor for the merged graph view.
 * Updates the raw GraphCompareResult with MOVABLE matches before the merged DTO is built.
 */
public class MergedGraphComparisonPostProcessor extends GitComparisonPostProcessor<GraphCompareResult> {

    @Override
    public void postProcess(GraphCompareResult result, CommitGraph first, CommitGraph second) {
        Set<Integer> matchedG1 = new HashSet<>(result.getMatchingVertices().keySet());
        Set<Integer> matchedG2 = new HashSet<>(result.getMatchingVertices().values());

        for (Commit commit1 : first.getVertices().stream().map(v -> v.asCommit()).toList()) {
            if (matchedG1.contains(commit1.getNumber())) {
                continue;
            }

            for (Commit commit2 : second.getVertices().stream().map(v -> v.asCommit()).toList()) {
                if (matchedG2.contains(commit2.getNumber())) {
                    continue;
                }

                if (commit1.canRelate(commit2)) {
                    // Match found as MOVABLE
                    result.getMatchingVertices().put(commit1.getNumber(), commit2.getNumber());
                    // Since we don't want to modify GraphCompareResult with new fields, 
                    // we'll rely on the fact that these are now "matched" for the merger.
                    
                    matchedG1.add(commit1.getNumber());
                    matchedG2.add(commit2.getNumber());
                    break;
                }
            }
        }
    }
}
