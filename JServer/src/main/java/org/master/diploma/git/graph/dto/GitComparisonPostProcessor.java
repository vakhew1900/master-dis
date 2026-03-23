package org.master.diploma.git.graph.dto;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.dto.samples.DiffDto;
import org.master.diploma.git.graph.dto.samples.NodeDto;
import org.master.diploma.git.label.GitLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base abstract class for post-processing Git comparison results.
 * Different strategies are implemented for two-graph vs merged-graph views.
 */
public abstract class GitComparisonPostProcessor<T> {

    /**
     * Performs post-processing on the comparison DTO/result.
     *
     * @param result the comparison result object (DTO or raw) to update
     * @param first  the first commit graph
     * @param second  the second commit graph
     */
    public abstract void postProcess(T result, CommitGraph first, CommitGraph second);

    /**
     * Recalculates diffs for matched but moved nodes.
     *
     * @param firstNode  DTO of the first node
     * @param secondNode DTO of the second node
     * @param first      the first commit
     * @param second     the second commit
     */
    protected void recalculateDiffs(NodeDto firstNode, NodeDto secondNode, Commit first, Commit second) {
        Map<String, List<GitLabel>> firstLabels = first.getLabels().stream()
                .collect(Collectors.groupingBy(l -> l.getLabelInfo().getValue()));
        Map<String, List<GitLabel>> secondLabels = second.getLabels().stream()
                .collect(Collectors.groupingBy(l -> l.getLabelInfo().getValue()));

        Set<String> allValues = firstLabels.keySet();
        Set<String> allOtherValues = secondLabels.keySet();

        List<DiffDto> firstDiffs = new ArrayList<>();
        List<DiffDto> secondDiffs = new ArrayList<>();

        // Recalculate first node diffs
        for (String value : allValues) {
            String state = secondLabels.containsKey(value) ? DiffDto.STATE_CORRECT : DiffDto.STATE_EXTRACT;
            firstDiffs.add(new DiffDto(value, state));
        }

        // Recalculate second node diffs
        for (String value : allOtherValues) {
            String state = firstLabels.containsKey(value) ? DiffDto.STATE_CORRECT : DiffDto.STATE_MISSED;
            secondDiffs.add(new DiffDto(value, state));
        }

        firstNode.setDiffs(firstDiffs);
        secondNode.setDiffs(secondDiffs);
    }
}
