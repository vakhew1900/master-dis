package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.UniqueLabelMethodExecutor;

public class UniqueLabelMethodExecutorTest  extends  MethodExecutorTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new UniqueLabelMethodExecutor();
    }
}
