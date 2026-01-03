package org.master.diploma.git.generatiion;

import org.master.diploma.git.graph.subgraphmethod.BranchMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

public class BranchMethodGenerationTest extends GenerationTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new BranchMethodExecutor();
    }
}
