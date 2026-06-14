package org.git_tutor.git_logic.generation;

import org.git_tutor.git_logic.graph.subgraphmethod.BranchMethodExecutor;
import org.git_tutor.git_logic.graph.subgraphmethod.SubgraphMethodExecutor;

public class BranchMethodGenerationTest extends GenerationTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new BranchMethodExecutor();
    }
}
