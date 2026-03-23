package org.master.diploma.git.graph.dto.merged_graph;

import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.ComparisonResultBuilder;
import org.master.diploma.git.graph.dto.CompareResultDto;
import org.master.diploma.git.graph.dto.GitComparisonPreProcessor;

/**
 * Builder implementation for Merged Graph visualization.
 */
public class MergedGraphResultBuilder extends ComparisonResultBuilder<MergedGraphComparisonResultDto> {

    public MergedGraphResultBuilder() {
        super(new GitComparisonPreProcessor());
    }

    @Override
    public MergedGraphComparisonResultDto build(CommitGraph current, CommitGraph target, GraphCompareResult rawResult) {
        // Pre-process rawResult to add MOVABLE matches
        var movableCompareResult = preProcessor.process(current, target, rawResult);

        return MergedGraphComparisonResultDto.builder()
                .mergedGraph(new MergedGraphConverter(movableCompareResult, target).convert(current))
                .compareResult(CompareResultDto.from(current, target, rawResult))
                .build();
    }
}
