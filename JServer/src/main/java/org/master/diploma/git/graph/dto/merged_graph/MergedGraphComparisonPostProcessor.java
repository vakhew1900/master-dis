package org.master.diploma.git.graph.dto.merged_graph;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.dto.GitComparisonPostProcessor;
import org.master.diploma.git.graph.dto.samples.NodeDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Post-processor for the merged graph view.
 * Updates NodeDto severities directly within the merged DTO after it is built.
 */
public class MergedGraphComparisonPostProcessor extends GitComparisonPostProcessor<MergedGraphComparisonResultDto> {

    @Override
    public void postProcess(MergedGraphComparisonResultDto dto, CommitGraph first, CommitGraph second) {
        List<NodeDto> nodes = dto.getMergedGraph().getNodes();
        
        Set<String> matchedInSecond = new HashSet<>(dto.getCompareResult().getMatchedHashes1To2().values());

        for (NodeDto node : nodes) {
            if (!NodeDto.SEVERITY_EXTRA.equals(node.getSeverity())) {
                continue;
            }

            Commit commit = first.getVertex(node.getNumber());

            for (NodeDto otherNode : nodes) {
                if (!NodeDto.SEVERITY_MISSED.equals(otherNode.getSeverity()) || matchedInSecond.contains(otherNode.getHash())) {
                    continue;
                }

                Commit otherCommit = second.getVertex(otherNode.getNumber());

                if (commit.canRelate(otherCommit)) {
                    node.setSeverity(NodeDto.SEVERITY_MOVABLE);
                    otherNode.setSeverity(NodeDto.SEVERITY_MOVABLE);
                    dto.getCompareResult().getMatchedHashes1To2().put(node.getHash(), otherNode.getHash());
                    matchedInSecond.add(otherNode.getHash());
                    
                    // Recalculate diffs for the paired nodes to show actual differences instead of just EXTRACT/MISSED
                    recalculateDiffs(node, otherNode, commit, otherCommit);
                    break;
                }
            }
        }
    }
}
