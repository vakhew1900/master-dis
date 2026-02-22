package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.llm.LLMMethodExecutor;

/**
 * Test class for {@link LLMMethodExecutor}, extending the common test functionality
 * provided by {@link MethodExecutorTest}. This class specifically tests the
 * Large Language Model (LLM) based subgraph comparison algorithm.
 * <p>
 * Тестовый класс для {@link LLMMethodExecutor}, расширяющий общую тестовую функциональность,
 * предоставляемую {@link MethodExecutorTest}. Этот класс специально тестирует
 * алгоритм сравнения подграфов на основе Большой Языковой Модели (LLM).
 */
public class LlmMethodExecutorTest extends MethodExecutorTest{
    /**
     * Provides an instance of {@link LLMMethodExecutor} for testing.
     * <p>
     * Предоставляет экземпляр {@link LLMMethodExecutor} для тестирования.
     * @return A new instance of LLMMethodExecutor. / Новый экземпляр LLMMethodExecutor.
     */
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new LLMMethodExecutor();
    }
}
