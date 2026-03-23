package org.master.diploma.git.graph.dto.two_graph;

import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.ComparisonResultBuilder;
import org.master.diploma.git.graph.dto.CompareResultDto;
import org.master.diploma.git.graph.dto.GitComparisonPreProcessor;

/**
 * Builder implementation for Side-by-Side (Two Graph) visualization.
 */
public class TwoGraphResultBuilder extends ComparisonResultBuilder<TwoGraphComparisonResultDto> {

    public TwoGraphResultBuilder() {
        super(new GitComparisonPreProcessor());
    }

    @Override
    public TwoGraphComparisonResultDto build(CommitGraph g1, CommitGraph g2, GraphCompareResult rawResult) {
        // Pre-process rawResult to add MOVABLE matches
        var graphCompareResult = preProcessor.process(g1, g2, rawResult);

        return TwoGraphComparisonResultDto.builder()
                .firstGraph(new StudentGraphConverter(graphCompareResult).convert(g1))
                .secondGraph(new ReferenceGraphConverter(graphCompareResult).convert(g2))
                .compareResult(CompareResultDto.from(g1, g2, graphCompareResult))
                .build();
    }
}
