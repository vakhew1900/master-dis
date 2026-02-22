package org.master.diploma.git.graph.subgraphmethod;

import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.label.LabelVertex;

/**
 * Abstract base class for methods that find the Maximum Common Transitive Subgraph (MCTS)
 * between two given graphs. Implementations will define specific algorithms
 * for this comparison.
 * <p>
 * Абстрактный базовый класс для методов, которые находят Максимальный Общий Транзитивный Подграф (MCTS)
 * между двумя заданными графами. Реализации будут определять конкретные алгоритмы
 * для этого сравнения.
 */
public abstract class SubgraphMethodExecutor {

    /**
     * Executes the subgraph comparison method to find the Maximum Common Transitive Subgraph (MCTS)
     * between two given graphs.
     * <p>
     * Выполняет метод сравнения подграфов для поиска Максимального Общего Транзитивного Подграфа (MCTS)
     * между двумя заданными графами.
     *
     * @param first  The first graph to compare. / Первый граф для сравнения.
     * @param second The second graph to compare. / Второй граф для сравнения.
     * @param <T>    The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A GraphCompareResult object containing the matching vertices and label errors. / Объект GraphCompareResult, содержащий сопоставленные вершины и ошибки меток.
     */
    public abstract  <T extends LabelVertex<?>> GraphCompareResult execute(
            Graph<T> first,
            Graph<T> second
    );
}
