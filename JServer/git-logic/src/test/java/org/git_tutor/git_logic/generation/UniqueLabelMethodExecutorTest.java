package org.git_tutor.git_logic.generation;

import org.git_tutor.git_logic.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.git_tutor.git_logic.git.graph.subgraphmethod.UniqueLabelMethodExecutor;

public class UniqueLabelMethodExecutorTest extends GenerationTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new UniqueLabelMethodExecutor();
    }
}
