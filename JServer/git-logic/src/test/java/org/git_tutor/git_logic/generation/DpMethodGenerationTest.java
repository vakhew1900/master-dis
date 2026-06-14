package org.git_tutor.git_logic.generation;

import org.git_tutor.git_logic.graph.subgraphmethod.DpMethodHelper;
import org.git_tutor.git_logic.graph.subgraphmethod.SubgraphMethodExecutor;

public class DpMethodGenerationTest extends GenerationTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new DpMethodHelper();
    }
}
