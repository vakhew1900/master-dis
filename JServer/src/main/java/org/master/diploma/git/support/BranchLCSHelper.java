package org.master.diploma.git.support;

import org.master.diploma.git.graph.Branch;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.graph.subgraphmethod.BranchMethodExecutor;
import org.master.diploma.git.label.Label;

import java.util.HashMap;
import java.util.List;

public class BranchLCSHelper {


    public static GraphCompareResult findBranchLCS(
            BranchMethodExecutor.BranchMatch<? extends LabelVertex<? extends Label>> branchMatch
    ) {
        return findBranchLCS(
                branchMatch.getFirstBranch(),
                branchMatch.getSecondBranch()
        );
    }

    public static GraphCompareResult findBranchLCS(
            Branch<? extends LabelVertex<?>> first,
            Branch<? extends LabelVertex<?>> second

    ) {

        List<List<GraphCompareResult>> dp = Creator.createMatrix(
                first.getVertices().size() + 1,
                second.getVertices().size() + 1,
                GraphCompareResult::new
        );

        for (int i = 1; i < first.getVertices().size(); i++) {
            for (int j = 1; j < second.getVertices().size(); j++) {
                var firstVertex = first.getVertices().get(i - 1);
                var secondVertex = second.getVertices().get(j - 1);

                if (firstVertex.canRelate(secondVertex)) {
                    var prev = dp.get(i - 1).get(j - 1);
                    var next = GraphCompareResult
                            .builder()
                            .matchingVertices(new HashMap<>(prev.getMatchingVertices()))
                            .labelErrors(new HashMap<>(prev.getLabelErrors()))
                            .build();

                    next.getMatchingVertices().put(firstVertex.getNumber(), secondVertex.getNumber());
                    next
                            .getLabelErrors()
                            .put(
                                    firstVertex.getNumber(),
                                    GraphCompareResult.LabelError.createLabelError(firstVertex, secondVertex)
                            );

                    dp.get(i).set(j, next);

                } else {
                    var prev1 = dp.get(i).get(j - 1);
                    var prev2 = dp.get(i - 1).get(j);
                    var next = (prev1.isBigger(prev2)) ? prev1 : prev2;
                    dp.get(i).set(j, next);
                }
            }
        }

        return dp
                .get(first.getVertices().size())
                .get(second.getVertices().size());
    }
}
