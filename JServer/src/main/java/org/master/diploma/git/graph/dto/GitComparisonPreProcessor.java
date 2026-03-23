package org.master.diploma.git.graph.dto;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GitGraphCompareResult;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Pre-processor that enhances GraphCompareResult by matching unmatched vertices
 * that can be related based on their labels.
 * Returns a GitGraphCompareResult which contains information about movable vertices.
 */
public class GitComparisonPreProcessor {

    public GitGraphCompareResult process(CommitGraph current, CommitGraph target, GraphCompareResult rawResult) {
        GitGraphCompareResult result = new GitGraphCompareResult(rawResult);
        
        Map<Integer, Integer> matchingVertices = result.getMatchingVertices();
        
        Set<Integer> matchedG1 = new HashSet<>(matchingVertices.keySet());
        Set<Integer> matchedG2 = new HashSet<>(matchingVertices.values());

        for (Vertex v1 : current.getVertices()) {
            Commit c1 = v1.asCommit();
            if (matchedG1.contains(c1.getNumber())) {
                continue;
            }

            for (Vertex v2 : target.getVertices()) {
                Commit c2 = v2.asCommit();
                if (matchedG2.contains(c2.getNumber())) {
                    continue;
                }

                if (c1.canRelate(c2)) {
                    matchingVertices.put(c1.getNumber(), c2.getNumber());
                    matchedG1.add(c1.getNumber());
                    matchedG2.add(c2.getNumber());
                    
                    // Add to movable vertices to track this fuzzy match
                    result.getMovableVertices().add(c1.getNumber());
                    result.getMovableVertices().add(c2.getNumber());

                    // Fill label error for the new match
                    GraphCompareResult.LabelError labelError = GraphCompareResult.LabelError.createLabelError(c1, c2);
                    result.getLabelErrors().put(c1.getNumber(), labelError);
                    break;
                }
            }
        }
        return result;
    }
}
