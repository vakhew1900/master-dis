package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.BranchMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

public class BranchMethodExecutorTest extends MethodExecutorTest {

    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new BranchMethodExecutor();
    }
}
