package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.DpMethodHelper;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

public class DpMethodExecutorTest extends  MethodExecutorTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new DpMethodHelper();
    }
}
