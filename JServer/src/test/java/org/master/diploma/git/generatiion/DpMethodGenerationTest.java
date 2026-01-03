package org.master.diploma.git.generatiion;

import org.master.diploma.git.graph.subgraphmethod.DpMethodHelper;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

public class DpMethodGenerationTest extends GenerationTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new DpMethodHelper();
    }
}
