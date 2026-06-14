package org.git_tutor.git_logic.generation;

import org.git_tutor.git_logic.git.graph.subgraphmethod.BruteForceMethodExecutor;
import org.git_tutor.git_logic.git.graph.subgraphmethod.SubgraphMethodExecutor;

public class BruteforceMethodGenerationTest  extends GenerationTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new BruteForceMethodExecutor();
    }
}
