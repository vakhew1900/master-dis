package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.llm.LLMMethodExecutor;

public class LlmMethodExecutorTest extends MethodExecutorTest{
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new LLMMethodExecutor();
    }
}
