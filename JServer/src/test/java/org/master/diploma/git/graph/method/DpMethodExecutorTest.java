package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.DpMethodHelper;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

/**
 * Test class for {@link DpMethodHelper}, extending the common test functionality
 * provided by {@link MethodExecutorTest}. This class specifically tests the
 * dynamic programming based subgraph comparison algorithm.
 * <p>
 * Тестовый класс для {@link DpMethodHelper}, расширяющий общую тестовую функциональность,
 * предоставляемую {@link MethodExecutorTest}. Этот класс специально тестирует
 * алгоритм сравнения подграфов на основе динамического программирования.
 */
public class DpMethodExecutorTest extends  MethodExecutorTest{
    /**
     * Provides an instance of {@link DpMethodHelper} for testing.
     * <p>
     * Предоставляет экземпляр {@link DpMethodHelper} для тестирования.
     * @return A new instance of DpMethodHelper. / Новый экземпляр DpMethodHelper.
     */
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new DpMethodHelper();
    }
}
