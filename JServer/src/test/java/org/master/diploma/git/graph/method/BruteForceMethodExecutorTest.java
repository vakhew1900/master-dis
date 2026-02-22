package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.BruteForceMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

/**
 * Test class for {@link BruteForceMethodExecutor}, extending the common test functionality
 * provided by {@link MethodExecutorTest}. This class specifically tests the
 * brute-force subgraph comparison algorithm.
 * <p>
 * Тестовый класс для {@link BruteForceMethodExecutor}, расширяющий общую тестовую функциональность,
 * предоставляемую {@link MethodExecutorTest}. Этот класс специально тестирует
 * алгоритм сравнения подграфов методом полного перебора.
 */
public class BruteForceMethodExecutorTest  extends MethodExecutorTest{
    /**
     * Provides an instance of {@link BruteForceMethodExecutor} for testing.
     * <p>
     * Предоставляет экземпляр {@link BruteForceMethodExecutor} для тестирования.
     * @return A new instance of BruteForceMethodExecutor. / Новый экземпляр BruteForceMethodExecutor.
     */
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new BruteForceMethodExecutor();
    }
}
