package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.BruteForceMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

public class BruteForceMethodExecutorTest  extends MethodExecutorTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new BruteForceMethodExecutor();
    }
}
