package org.master.diploma.git.graph.dto.merged_graph;

import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.ComparisonResultBuilder;
import org.master.diploma.git.graph.dto.CompareResultDto;

/**
 * Builder implementation for Merged Graph visualization.
 */
public class MergedGraphResultBuilder extends ComparisonResultBuilder<MergedGraphComparisonResultDto, MergedGraphComparisonResultDto> {

    public MergedGraphResultBuilder() {
        super(new MergedGraphComparisonPostProcessor());
    }

    @Override
    public MergedGraphComparisonResultDto build(CommitGraph current, CommitGraph target, GraphCompareResult rawResult) {
        MergedGraphComparisonResultDto dto = MergedGraphComparisonResultDto.builder()
                .mergedGraph(new MergedGraphConverter(rawResult, target).convert(current))
                .compareResult(CompareResultDto.from(current, target, rawResult))
                .build();

        // Post-process built DTO to add MOVABLE marks
        postProcessor.postProcess(dto, current, target);

        return dto;
    }
}
