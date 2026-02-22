package org.master.diploma.git.graph.subgraphmethod;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.exception.MaxVertexCountException;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.support.CombinatoricHelper;
import org.master.diploma.git.support.Timeout;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implements a brute-force approach to find the Maximum Common Transitive Subgraph (MCTS)
 * between two given graphs. This method checks all possible permutations and combinations
 * of vertices, making it highly computationally intensive and suitable only for very small graphs.
 * It includes checks for maximum vertex count and operation limits to prevent excessive execution times.
 * <p>
 * Реализует метод полного перебора для поиска Максимального Общего Транзитивного Подграфа (MCTS)
 * между двумя заданными графами. Этот метод проверяет все возможные перестановки и комбинации
 * вершин, что делает его крайне вычислительно затратным и пригодным только для очень маленьких графов.
 * Он включает проверки на максимальное количество вершин и пределы операций для предотвращения чрезмерного времени выполнения.
 */
public class BruteForceMethodExecutor extends SubgraphMethodExecutor {

    /** Logger for this class. <p> Логгер для этого класса. */
    private static final Logger LOG = LogManager.getLogger(BruteForceMethodExecutor.class); // Corrected logger class
    /** Maximum number of vertices supported by the brute-force method. <p> Максимальное количество вершин, поддерживаемое методом полного перебора. */
    private static final int MAX_VERTEX_SIZE = 16;
    /** Threshold for the estimated number of operations to prevent excessive computation. <p> Порог для оценочного количества операций для предотвращения чрезмерных вычислений. */
    private static final long OPERATION_COUNT = 100_000_000_000L;
    /** Timeout in milliseconds for the brute-force execution. <p> Таймаут в миллисекундах для выполнения полного перебора. */
    public static final long TIME_OUT = 5 * 60 * 1000;

    /**
     * Executes the brute-force subgraph comparison method.
     * This method attempts to find the largest common subgraph by iterating through
     * all possible combinations and permutations of vertices between the two graphs.
     * It includes safeguards for graph size and operation count to prevent excessive computation.
     * <p>
     * Выполняет метод полного перебора для сравнения подграфов.
     * Этот метод пытается найти наибольший общий подграф, перебирая
     * все возможные комбинации и перестановки вершин между двумя графами.
     * Он включает меры предосторожности для размера графа и количества операций для предотвращения чрезмерных вычислений.
     *
     * @param first  The first graph to compare. / Первый граф для сравнения.
     * @param second The second graph to compare. / Второй граф для сравнения.
     * @param <T>    The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A GraphCompareResult object containing the matching vertices and label errors. / Объект GraphCompareResult, содержащий сопоставленные вершины и ошибки меток.
     * @throws MaxVertexCountException If the graph size or estimated operation count exceeds defined limits. / Если размер графа или оценочное количество операций превышает установленные пределы.
     */
    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(
            Graph<T> first,
            Graph<T> second
    ) {
        // Determine if graphs should be inverted for optimization (always compare smaller to larger)
        // Определяем, следует ли инвертировать графы для оптимизации (всегда сравниваем меньший с большим)
        boolean invert = first.getVertices().size() > second.getVertices().size();

        if (invert) {
            // Swap graphs to ensure 'first' is smaller or equal in size to 'second'
            // Меняем графы местами, чтобы 'first' был меньше или равен по размеру 'second'
            var tmp = first;
            first = second;
            second = tmp;
        }

        // Check if the second (larger) graph exceeds the maximum supported vertex count for brute-force
        // Проверяем, превышает ли второй (больший) граф максимальное количество вершин, поддерживаемое методом полного перебора
        if (second.getVertices().size() > MAX_VERTEX_SIZE) {
            throw new MaxVertexCountException(
                    String.format(
                            "Bruteforce method not support graph with vertices count more than %s. First graph - %d, second graph - %d",
                            MAX_VERTEX_SIZE,
                            first.getVertices().size(),
                            second.getVertices().size()
                    )
            );
        }

        // Estimate the number of operations and check against a predefined limit
        // Оцениваем количество операций и проверяем на соответствие предопределенному лимиту
        long operationCount = getOperationCount(first, second);

        if (operationCount > OPERATION_COUNT) {
            throw new MaxVertexCountException("Operation count is too big for bruteforce method");
        }

        // Pre-calculate possible matches for each vertex based on `canRelate`
        // Предварительно вычисляем возможные совпадения для каждой вершины на основе `canRelate`
        Map<Integer, Set<Integer>> verticesMatching = new HashMap<>();
        for (T u : first.getVertices()) {
            verticesMatching.put(u.getNumber(), new HashSet<>());

            for (T v : second.getVertices()) {
                if (u.canRelate(v)) {
                    verticesMatching.get(u.getNumber()).add(v.getNumber());
                }
            }
        }

        int n = second.getVertices().size(); // Number of vertices in the second graph
        int k = first.getVertices().size();  // Number of vertices in the first graph

        Timeout.getInstance().updateStartTime(); // Start timeout timer

        GraphCompareResult res = new GraphCompareResult(); // Stores the best comparison result found
        res.setInvert(invert); // Set invert flag for the final result

        long count = 0; // Counter for iterations
        // Iterate through all possible subgraph sizes (number of matched vertices)
        // Итерируем по всем возможным размерам подграфов (количество сопоставленных вершин)
        for (int i = 1; i <= k; i++) {
            // Generate combinations of vertices from the first graph for the current size 'i'
            // Генерируем комбинации вершин из первого графа для текущего размера 'i'
            List<List<Integer>> allFirstVerticesPermutation = CombinatoricHelper.generateCombinations(k, i);
            // Generate permutations of vertices from the second graph for the current size 'i'
            // Генерируем перестановки вершин из второго графа для текущего размера 'i'
            List<List<Integer>> allSecondVerticesPermutation = CombinatoricHelper.generatePermutations(n, i);

            Timeout.getInstance().brokeTime(TIME_OUT); // Check for timeout

            for (var firstPermutation : allFirstVerticesPermutation) {
                Timeout.getInstance().brokeTime(BruteForceMethodExecutor.TIME_OUT); // Check for timeout
                for (var secondPermutation : allSecondVerticesPermutation) {
                    boolean canCompare = true; // Flag to indicate if the current permutation is valid

                    // Check if each pair of vertices in the current permutations can be related
                    // Проверяем, могут ли каждая пара вершин в текущих перестановках быть связаны
                    for (int j = 0; j < firstPermutation.size(); j++) {
                        canCompare = canCompare && first
                                .getVertices().get(firstPermutation.get(j) - 1)
                                .canRelate(
                                        second
                                                .getVertices()
                                                .get(secondPermutation.get(j) - 1)
                                );
                    }

                    count++; // Increment iteration counter
                    if (count % 1000000000 == 0) // Log progress periodically
                        LOG.info(count);
                    Graph<T> finalFirst = first;
                    Graph<T> finalSecond = second;

                    // If all selected vertices can be related, calculate the graph comparison result for this mapping
                    // Если все выбранные вершины могут быть связаны, вычисляем результат сравнения графов для этого сопоставления
                    if (canCompare) {
                        GraphCompareResult next = calculate(first, second,
                                firstPermutation
                                        .stream()
                                        .map(pos -> finalFirst.getVertices().get(pos - 1).getNumber())
                                        .collect(Collectors.toList()),
                                secondPermutation
                                        .stream()
                                        .map(pos -> finalSecond.getVertices().get(pos - 1).getNumber())
                                        .collect(Collectors.toList())
                        );
                        next.setInvert(invert); // Set invert flag for this sub-result
                        next.fillLabelError(first, second); // Fill label errors for this sub-result
                        // If the current result is "bigger" (better) than the best found so far, update it
                        // Если текущий результат "больше" (лучше), чем найденный до сих пор, обновляем его
                        if (next.isBigger(res)) {
                            res = next;
                        }
                    }
                }
            }
        }

        // Finalize label errors and apply inversion if necessary
        // Завершаем заполнение ошибок меток и применяем инверсию при необходимости
        res.fillLFinaLabelError(first, second); //todo поменять - this todo indicates potential improvement or recheck
        return res;
    }

    /**
     * Calculates a GraphCompareResult for a given set of vertex permutations.
     * It checks if the transitive closure of the subgraphs formed by the permutations are identical.
     * <p>
     * Вычисляет GraphCompareResult для заданного набора перестановок вершин.
     * Проверяет, идентичны ли транзитивные замыкания подграфов, образованных перестановками.
     *
     * @param first            The first graph. / Первый граф.
     * @param second           The second graph. / Второй граф.
     * @param firstPermutation  List of vertex numbers from the first graph forming a permutation. / Список номеров вершин из первого графа, образующих перестановку.
     * @param secondPermutation List of vertex numbers from the second graph forming a permutation. / Список номеров вершин из второго графа, образующих перестановку.
     * @param <T>              The type of Vertex used in the graphs. / Тип Vertex, используемый в графах.
     * @return A GraphCompareResult indicating matching vertices if the transitive closures are identical. / GraphCompareResult, указывающий совпадающие вершины, если транзитивные замыкания идентичны.
     */
    private <T extends Vertex> GraphCompareResult calculate(
            Graph<T> first,
            Graph<T> second,
            List<Integer> firstPermutation,
            List<Integer> secondPermutation
    ) {
        // Assign unique UUIDs to vertices in the permutations for comparison
        // Присваиваем уникальные UUID вершинам в перестановках для сравнения
        Map<Integer, UUID> firstUUIDS = new HashMap<>();
        Map<Integer, UUID> secondUUIDS = new HashMap<>();

        for (int i = 0; i < firstPermutation.size(); i++) {
            UUID uuid = UUID.randomUUID();
            firstUUIDS.put(firstPermutation.get(i), uuid);
            secondUUIDS.put(secondPermutation.get(i), uuid);
        }

        // Convert transitive closures to sets of UUID pairs for comparison
        // Преобразуем транзитивные замыкания в наборы пар UUID для сравнения
        Set<Map.Entry<UUID, UUID>> firstSet = integerToUUID(firstUUIDS, first);
        Set<Map.Entry<UUID, UUID>> secondSet = integerToUUID(secondUUIDS, second);

        GraphCompareResult result = new GraphCompareResult();
        // If the sets of UUID pairs representing transitive closures are identical, then the subgraphs match
        // Если наборы пар UUID, представляющие транзитивные замыкания, идентичны, то подграфы совпадают
        if (Sets.difference(firstSet, secondSet).isEmpty() && Sets.difference(secondSet, firstSet).isEmpty()) {
            result.setMatchingVertices(
                    IntStream
                            .range(0, firstPermutation.size())
                            .boxed()
                            .collect(Collectors.toMap(
                                            firstPermutation::get,
                                            secondPermutation::get
                                    )
                            )
            );
        }

        return result;
    }

    /**
     * Converts a graph's transitive closure from integer-based vertex numbers to UUID-based pairs.
     * This is used to compare the structural equivalence of subgraphs.
     * <p>
     * Преобразует транзитивное замыкание графа из пар вершин, основанных на целых числах, в пары, основанные на UUID.
     * Это используется для сравнения структурной эквивалентности подграфов.
     *
     * @param map   A map from integer vertex numbers to their assigned UUIDs. / Карта из целочисленных номеров вершин в их присвоенные UUID.
     * @param graph The graph from which to get the transitive closure. / Граф, из которого извлекается транзитивное замыкание.
     * @param <T>   The type of Vertex used in the graph. / Тип Vertex, используемый в графе.
     * @return A set of Map.Entry (UUID, UUID) representing the transitive closure with UUIDs. / Набор Map.Entry (UUID, UUID), представляющий транзитивное замыкание с UUID.
     */
    private <T extends Vertex> Set<Map.Entry<UUID, UUID>> integerToUUID(Map<Integer, UUID> map, Graph<T> graph) {
        return graph.getTransitiveClosure()
                .stream()
                .filter(entry -> map.containsKey(entry.getKey()) && map.containsKey(entry.getValue())) // Filter entries where both source and target vertices are in the permutation
                .map(entry -> new AbstractMap.SimpleEntry<>(map.get(entry.getKey()), map.get(entry.getValue()))) // Map vertex numbers to their UUIDs
                .collect(Collectors.toSet());
    }

    /**
     * Estimates the number of operations required for the brute-force comparison.
     * This is a heuristic calculation to avoid extremely long running times.
     * <p>
     * Оценивает количество операций, необходимых для сравнения методом полного перебора.
     * Это эвристический расчет для предотвращения чрезмерно длительного времени выполнения.
     *
     * @param first  The first graph. / Первый граф.
     * @param second The second graph. / Второй граф.
     * @return An estimated count of operations. / Оценочное количество операций.
     */
    private long getOperationCount(Graph<?> first, Graph<?> second) {
        return CombinatoricHelper.factorial(second.getVertices().size()) * (long) Math.pow(2, (first.getVertices().size()));
    }


}
