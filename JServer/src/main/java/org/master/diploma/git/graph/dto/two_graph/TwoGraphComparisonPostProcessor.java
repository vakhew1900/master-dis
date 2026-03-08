package org.master.diploma.git.graph.dto.two_graph;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.dto.GitComparisonPostProcessor;
import org.master.diploma.git.graph.dto.samples.NodeDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of post-processor for the two-graph side-by-side view.
 * Updates NodeDto severities directly within the comparison DTO.
 */
public class TwoGraphComparisonPostProcessor extends GitComparisonPostProcessor<TwoGraphComparisonResultDto> {

    @Override
    public void postProcess(TwoGraphComparisonResultDto dto, CommitGraph first, CommitGraph second) {
        List<NodeDto> firstNodes = dto.getFirstGraph().getNodes();
        List<NodeDto> secondNodes = dto.getSecondGraph().getNodes();
        
        Set<String> matchedInSecond = new HashSet<>(dto.getCompareResult().getMatchedHashes1To2().values());

        for (NodeDto node : firstNodes) {
            if (!NodeDto.SEVERITY_EXTRA.equals(node.getSeverity())) {
                continue;
            }

            Commit commit = first.getVertex(node.getNumber());

            for (NodeDto otherNode : secondNodes) {
                if (!NodeDto.SEVERITY_EXTRA.equals(otherNode.getSeverity()) || matchedInSecond.contains(otherNode.getHash())) {
                    continue;
                }

                Commit otherCommit = second.getVertex(otherNode.getNumber());

                if (commit.canRelate(otherCommit)) {
                    node.setSeverity(NodeDto.SEVERITY_MOVABLE);
                    otherNode.setSeverity(NodeDto.SEVERITY_MOVABLE);
                    dto.getCompareResult().getMatchedHashes1To2().put(node.getHash(), otherNode.getHash());
                    matchedInSecond.add(otherNode.getHash());
                    break;
                }
            }
        }
    }
}
