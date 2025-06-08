package org.master.diploma.git.graph.subgraphmethod;

import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.label.LabelVertex;

public abstract class SubgraphMethodExecutor {

    public abstract  <T extends LabelVertex<?>> GraphCompareResult execute(
            Graph<T> first,
            Graph<T> second
    );
}
