package org.git_tutor.git_logic.graph.dto.two_graph;

import org.git_tutor.git_logic.model.CommitGraph;
import org.git_tutor.git_logic.graph.GraphCompareResult;
import org.git_tutor.git_logic.graph.dto.ComparisonResultBuilder;
import org.git_tutor.git_logic.graph.dto.CompareResultDto;

/**
 * Builder implementation for Side-by-Side (Two Graph) visualization.
 */
public class TwoGraphResultBuilder extends ComparisonResultBuilder<TwoGraphComparisonResultDto, TwoGraphComparisonResultDto> {

    public TwoGraphResultBuilder() {
        super(new TwoGraphComparisonPostProcessor());
    }

    @Override
    public TwoGraphComparisonResultDto build(CommitGraph g1, CommitGraph g2, GraphCompareResult rawResult) {
        TwoGraphComparisonResultDto dto = TwoGraphComparisonResultDto.builder()
                .firstGraph(new StudentGraphConverter(rawResult).convert(g1))
                .secondGraph(new ReferenceGraphConverter(rawResult).convert(g2))
                .compareResult(CompareResultDto.from(g1, g2, rawResult))
                .build();

        postProcessor.postProcess(dto, g1, g2);
        return dto;
    }
}
