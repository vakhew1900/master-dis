package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.BranchMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;

/**
 * Test class for {@link BranchMethodExecutor}, extending the common test functionality
 * provided by {@link MethodExecutorTest}. This class specifically tests the
 * branch-based subgraph comparison algorithm.
 * <p>
 * Тестовый класс для {@link BranchMethodExecutor}, расширяющий общую тестовую функциональность,
 * предоставляемую {@link MethodExecutorTest}. Этот класс специально тестирует
 * алгоритм сравнения подграфов на основе ветвей.
 */
public class BranchMethodExecutorTest extends MethodExecutorTest {

    /**
     * Provides an instance of {@link BranchMethodExecutor} for testing.
     * <p>
     * Предоставляет экземпляр {@link BranchMethodExecutor} для тестирования.
     * @return A new instance of BranchMethodExecutor. / Новый экземпляр BranchMethodExecutor.
     */
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new BranchMethodExecutor();
    }
}
