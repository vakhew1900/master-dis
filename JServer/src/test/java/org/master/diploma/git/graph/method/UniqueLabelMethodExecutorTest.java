package org.master.diploma.git.graph.method;

import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.UniqueLabelMethodExecutor;

/**
 * Test class for {@link UniqueLabelMethodExecutor}, extending the common test functionality
 * provided by {@link MethodExecutorTest}. This class specifically tests the
 * subgraph comparison algorithm designed for graphs with unique labels per vertex.
 * <p>
 * Тестовый класс для {@link UniqueLabelMethodExecutor}, расширяющий общую тестовую функциональность,
 * предоставляемую {@link MethodExecutorTest}. Этот класс специально тестирует
 * алгоритм сравнения подграфов, предназначенный для графов с уникальными метками для каждой вершины.
 */
public class UniqueLabelMethodExecutorTest  extends  MethodExecutorTest{
    /**
     * Provides an instance of {@link UniqueLabelMethodExecutor} for testing.
     * <p>
     * Предоставляет экземпляр {@link UniqueLabelMethodExecutor} для тестирования.
     * @return A new instance of UniqueLabelMethodExecutor. / Новый экземпляр UniqueLabelMethodExecutor.
     */
    @Override
    protected SubgraphMethodExecutor getSubgraphMethodExecutor() {
        return new UniqueLabelMethodExecutor();
    }
}
