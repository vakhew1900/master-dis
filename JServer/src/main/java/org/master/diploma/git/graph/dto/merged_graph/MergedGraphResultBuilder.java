package org.master.diploma.git.graph.dto.merged_graph;

import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.ComparisonResultBuilder;

/**
 * Builder implementation for Merged Graph visualization.
 */
public class MergedGraphResultBuilder extends ComparisonResultBuilder<MergedGraphComparisonResultDto, GraphCompareResult> {

    public MergedGraphResultBuilder() {
        super(new MergedGraphComparisonPostProcessor());
    }

    @Override
    public MergedGraphComparisonResultDto build(CommitGraph current, CommitGraph target, GraphCompareResult rawResult) {
        // Pre-process raw results before conversion
        postProcessor.postProcess(rawResult, current, target);

        return MergedGraphComparisonResultDto.builder()
                .mergedGraph(new MergedGraphConverter(rawResult, target).convert(current))
                .build();
    }
}
