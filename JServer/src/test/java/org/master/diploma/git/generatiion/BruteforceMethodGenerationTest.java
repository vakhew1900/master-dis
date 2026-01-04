package org.master.diploma.git.generatiion;

import org.master.diploma.git.graph.subgraphmethod.BruteForceMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

public class BruteforceMethodGenerationTest  extends GenerationTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new BruteForceMethodExecutor();
    }
}
