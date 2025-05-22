package org.master.diploma.git.graph.subgraphmethod;

import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;

public abstract class SubgraphMethodExecutor {

    public abstract  <T extends Vertex> GraphCompareResult execute(
            Graph<T> first,
            Graph<T> second
    );
}
