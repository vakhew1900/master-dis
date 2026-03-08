package org.master.diploma.git.graph.dto.two_graph;

import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.ComparisonResultBuilder;
import org.master.diploma.git.graph.dto.CompareResultDto;
import org.master.diploma.git.graph.dto.converter.ReferenceGraphConverter;
import org.master.diploma.git.graph.dto.converter.StudentGraphConverter;

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
