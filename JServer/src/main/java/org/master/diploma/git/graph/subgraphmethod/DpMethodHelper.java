package org.master.diploma.git.graph.subgraphmethod;

import lombok.EqualsAndHashCode;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.support.Constants;
import org.master.diploma.git.support.Creator;
import org.master.diploma.git.support.TwoOrderedMap;

import java.util.*;

/**
 * Implements a dynamic programming (DP) based approach to find the Maximum Common Transitive Subgraph (MCTS).
 * This method uses a recursive approach with memoization (dynamic programming) to efficiently
 * compare two graphs and find the largest common subgraph. It also incorporates the Hungarian algorithm
 * for optimal matching of child subproblems.
 * <p>
 * Реализует подход на основе динамического программирования (DP) для поиска Максимального Общего Транзитивного Подграфа (MCTS).
 * Этот метод использует рекурсивный подход с мемоизацией (динамическое программирование) для эффективного
 * сравнения двух графов и поиска наибольшего общего подграфа. Он также включает венгерский алгоритм
 * для оптимального сопоставления дочерних подзадач.
 */
public class DpMethodHelper extends SubgraphMethodExecutor {


    /**
     * Constant representing an uninitialized state or a weight of zero.
     * <p>
     * Константа, представляющая неинициализированное состояние или нулевой вес.
     */
    private static final int UN_INIT = 0;

    /**
     * Executes the dynamic programming subgraph comparison method.
     * <p>
     * Выполняет метод сравнения подграфов на основе динамического программирования.
     *
     * @param first  The first graph to compare. / Первый граф для сравнения.
     * @param second The second graph to compare. / Второй граф для сравнения.
     * @param <T>    The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A GraphCompareResult object containing the matching vertices and label errors. / Объект GraphCompareResult, содержащий сопоставленные вершины и ошибки меток.
     */
    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> first, Graph<T> second) {
        return findBiggestSubSequenceSubgraph(first, second).toGraphCompareResult();
    }

    /**
     * Finds the biggest common subsequence subgraph between two graphs using dynamic programming.
     * This method initializes the DP table and starts the recursive computation from the roots of the graphs.
     * <p>
     * Находит наибольший общий подграф-подпоследовательность между двумя графами с использованием динамического программирования.
     * Этот метод инициализирует DP-таблицу и запускает рекурсивные вычисления от корней графов.
     *
     * @param first  The first graph. / Первый граф.
     * @param second The second graph. / Второй граф.
     * @param <T>    The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A DpElement representing the biggest common subsequence subgraph. / DpElement, представляющий наибольший общий подграф-подпоследовательность.
     */
    public static <T extends LabelVertex<?>> DpElement findBiggestSubSequenceSubgraph(
            Graph<T> first,
            Graph<T> second
    ) {

        // Initialize the DP table
        // Инициализация массива dp
        // Determine the dimensions of the DP table based on the maximum vertex numbers in each graph
        // Определяем размеры таблицы DP на основе максимальных номеров вершин в каждом графе
        int rowCount = first.getVertices().stream().max(Comparator.comparingInt(Vertex::getNumber)).get().getNumber() + 1;
        int colCount = second.getVertices().stream().max(Comparator.comparingInt(Vertex::getNumber)).get().getNumber() + 1;

        // Create the DP table with DpElement instances
        // Создаем таблицу DP с экземплярами DpElement
        List<List<DpElement>> dp = Creator.createMatrix(
                rowCount,
                colCount,
                DpElement::new
        );

        // Start the recursive computation from the roots of the graphs
        // Запускаем рекурсивные вычисления от корней графов
        return findBiggestSubSequenceSubgraph(
                dp,
                first.getRoot(),  // Root of the first graph
                second.getRoot(), // Root of the second graph
                first,
                second
        );
    }

    /**
     * Recursive helper method for dynamic programming.
     * Computes the MCTS for subgraphs rooted at 'u' (from first graph) and 'v' (from second graph).
     * <p>
     * Рекурсивный вспомогательный метод для динамического программирования.
     * Вычисляет MCTS для подграфов, укорененных в 'u' (из первого графа) и 'v' (из второго графа).
     *
     * @param dp     The DP table for memoization. / Таблица DP для мемоизации.
     * @param u      The current vertex number from the first graph. / Текущий номер вершины из первого графа.
     * @param v      The current vertex number from the second graph. / Текущий номер вершины из второго графа.
     * @param first  The first graph. / Первый граф.
     * @param second The second graph. / Второй граф.
     * @param <T>    The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A DpElement representing the MCTS for the subproblem (u, v). / DpElement, представляющий MCTS для подзадачи (u, v).
     */
    private static <T extends LabelVertex<?>> DpElement findBiggestSubSequenceSubgraph(
            List<List<DpElement>> dp,
            int u,
            int v,
            Graph<T> first,
            Graph<T> second
    ) {
        // If the result for this subproblem (u, v) is already computed, return it (memoization)
        // Если результат для этой подзадачи (u, v) уже вычислен, возвращаем его (мемоизация)
        if (dp.get(u).get(v).getWeight() != UN_INIT) {
            return dp.get(u).get(v);
        }

        DpElement relatingValue = new DpElement();
        // Check if vertices u and v can be related (e.g., have compatible labels)
        // Проверяем, могут ли вершины u и v быть связаны (например, имеют совместимые метки)
        if (first.getVertex(u)
                .canRelate(second.getVertex(v))
        ) {
            // If they can relate, initialize a DpElement for this direct match
            // Если они могут быть связаны, инициализируем DpElement для этого прямого совпадения
            relatingValue = new DpElement();
            relatingValue.add(u, v); // Add the current matching pair

            // If both vertices have children, consider matching their children
            // Если обе вершины имеют потомков, рассматриваем сопоставление их потомков
            if (!first.getChildrenNumbers(u).isEmpty() && !second.getChildren(v).isEmpty()) {
                TwoOrderedMap<Integer, Integer> rows = new TwoOrderedMap<>(); // Maps childU index to childU number
                TwoOrderedMap<Integer, Integer> cols = new TwoOrderedMap<>(); // Maps childV index to childV number

                // Populate row and column mappings for children
                // Заполняем отображения строк и столбцов для потомков
                for (var childU : first.getChildrenNumbers(u)) {
                    rows.put(rows.size(), childU);
                    for (var childV : second.getChildrenNumbers(v)) {
                        if (!cols.containsValue(childV)) {
                            cols.put(cols.size(), childV);
                        }
                    }
                }

                // Create a weight matrix for children's subproblem results
                // Создаем матрицу весов для результатов подзадач потомков
                List<List<Integer>> weightMatrix = Creator.createMatrix(
                        rows.size(),
                        cols.size(),
                        () -> 0
                );

                // Fill the weight matrix by recursively solving subproblems for children
                // Заполняем матрицу весов, рекурсивно решая подзадачи для потомков
                for (var childU : first.getChildrenNumbers(u)) {
                    for (var childV : second.getChildrenNumbers(v)) {
                        DpElement tmp = findBiggestSubSequenceSubgraph(
                                dp,
                                childU,
                                childV,
                                first,
                                second
                        );
                        weightMatrix.get(rows.getKey(childU)).set(cols.getKey(childV), tmp.getWeight());
                    }
                }

                // Find the maximum child weight matching using an algorithm (e.g., Hungarian algorithm)
                // Находим максимальное сопоставление весов потомков с использованием алгоритма (например, венгерского)
                var maximumChildWeightList = getMaximumChildWeight(weightMatrix);

                // Map child matches from the optimal solution
                // Сопоставляем потомков из оптимального решения
                Map<Integer, Integer> firstGraphChildToSecondGraphChild = new HashMap<>(); // For easier debugging.
                for (int row = 0; row < weightMatrix.size(); row++) {
                    int col = maximumChildWeightList.get(row);
                    int i = rows.get(row); // Original childU number
                    if (cols.size() > col) { // Check if column is valid (could be -1 if no match)
                        int j = cols.get(col); // Original childV number
                        firstGraphChildToSecondGraphChild.put(i, j);
                    }
                }

                // Add the results of matched children subproblems to the current relatingValue
                // Добавляем результаты сопоставленных подзадач потомков к текущему relatingValue
                DpElement finalRelatingValue = relatingValue;
                firstGraphChildToSecondGraphChild.forEach(
                        (key, value) -> {
                            // Only add if the subproblem has a positive weight (actual match)
                            // Добавляем только если подзадача имеет положительный вес (фактическое совпадение)
                            if (dp.get(key).get(value).getWeight() > 0) {
                                finalRelatingValue.addDpElement(
                                        dp.get(key).get(value)
                                );
                            }
                        }
                );
            }
        }

        // Consider cases where 'u' is matched but 'v' is not, or vice versa (deletions/insertions)
        // Рассматриваем случаи, когда 'u' сопоставлено, а 'v' нет, или наоборот (удаления/вставки)
        DpElement other = new DpElement();

        // Recursively solve subproblems where childU is considered, but v remains the same
        // Рекурсивно решаем подзадачи, где childU рассматривается, но v остается тем же
        for (var childU : first.getChildrenNumbers(u)) {
            DpElement tmp = findBiggestSubSequenceSubgraph(
                    dp,
                    childU,
                    v,
                    first,
                    second
            );
            other.fillLabelError(first, second); // Fill label error for 'other' path
            tmp.fillLabelError(first, second);   // Fill label error for 'tmp' path
            other = DpElement.max(other, tmp);   // Take the maximum of current 'other' and 'tmp'
        }

        // Recursively solve subproblems where childV is considered, but u remains the same
        // Рекурсивно решаем подзадачи, где childV рассматривается, но u остается тем же
        for (var childV : second.getChildrenNumbers(v)) {
            DpElement tmp = findBiggestSubSequenceSubgraph(
                    dp,
                    u,
                    childV,
                    first,
                    second
            );
            other.fillLabelError(first, second); // Fill label error for 'other' path
            tmp.fillLabelError(first, second);   // Fill label error for 'tmp' path
            other = DpElement.max(other, tmp);   // Take the maximum of current 'other' and 'tmp'
        }

        // Fill label errors for the direct match case
        // Заполняем ошибки меток для случая прямого совпадения
        relatingValue.fillLabelError(first, second);
        // Fill label errors for the 'other' (deletion/insertion) case
        // Заполняем ошибки меток для случая 'other' (удаление/вставка)
        other.fillLabelError(first, second);

        // Store and return the maximum of the direct match (relatingValue) and other paths
        // Сохраняем и возвращаем максимум из прямого совпадения (relatingValue) и других путей
        dp.get(u).set(v, DpElement.max(relatingValue, other));
        return dp.get(u).get(v);
    }

    /**
     * Finds the maximum weight matching in a bipartite graph represented by the input matrix.
     * This method uses the Hungarian algorithm to solve the assignment problem.
     * <p>
     * Находит максимальное весовое сопоставление в двудольном графе, представленном входной матрицей.
     * Этот метод использует венгерский алгоритм для решения задачи о назначениях.
     *
     * @param matrix The weight matrix representing the bipartite graph. / Матрица весов, представляющая двудольный граф.
     * @return A list of column indices, where index 'i' corresponds to the matched column for row 'i'. / Список индексов столбцов, где индекс 'i' соответствует сопоставленному столбцу для строки 'i'.
     */
    private static List<Integer> getMaximumChildWeight(List<List<Integer>> matrix) {

        // Determine the size for the square matrix required by the Hungarian algorithm
        // Определяем размер для квадратной матрицы, требуемой венгерским алгоритмом
        int size = Math.max(matrix.size(), matrix.get(0).size()) + 1;
        List<List<Integer>> newMatrix = Creator.createMatrix(
                size,
                size,
                () -> 0
        );

        // Copy the original matrix into the new square matrix, adjusting for 1-based indexing used by the Hungarian algorithm
        // Копируем исходную матрицу в новую квадратную матрицу, корректируя для 1-базированной индексации, используемой венгерским алгоритмом
        for (int i = 1; i <= matrix.size(); i++) {
            for (int j = 1; j <= matrix.get(0).size(); j++) {
                newMatrix.get(i).set(j, matrix.get(i - 1).get(j - 1));
            }
        }

        // Reduce the matrix to prepare for the Hungarian algorithm (e.g., by subtracting max from each row/column)
        // Приводим матрицу к требуемому виду для венгерского алгоритма (например, вычитая максимум из каждой строки/столбца)
        reductionMatrix(newMatrix);

        // Apply the Hungarian algorithm to find the optimal assignment
        // Применяем венгерский алгоритм для поиска оптимального назначения
        List<Integer> temp = HungarianAlgorithm(newMatrix);
        List<Integer> result = new ArrayList<>();

        // Adjust the results back to 0-based indexing
        // Корректируем результаты обратно к 0-базированной индексации
        for (int i = 1; i < temp.size(); i++) { // Shift left by 1 because Hungarian algorithm uses 1-based indexing
            result.add(temp.get(i) - 1);
        }

        return result;
    }

    /**
     * Performs a reduction operation on the input matrix, typically by subtracting the maximum element
     * from each element in a row. This is a common preprocessing step for the Hungarian algorithm
     * when adapting it for maximum weight assignments.
     * <p>
     * Выполняет операцию редукции над входной матрицей, обычно вычитая максимальный элемент
     * из каждого элемента в строке. Это распространенный шаг предварительной обработки для венгерского алгоритма
     * при адаптации его для назначений с максимальным весом.
     *
     * @param matrix The matrix to be reduced. / Матрица, подлежащая редукции.
     */
    private static void reductionMatrix(List<List<Integer>> matrix) {
        matrix.forEach(
                row -> {
                    int max = Collections.max(row); // Find the maximum value in the current row
                    // Вычисляем максимальное значение в текущей строке

                    // Subtract the maximum value from each element in the row
                    // Вычитаем максимальное значение из каждого элемента в строке
                    for (int i = 0; i < row.size(); i++) {
                        row.set(i, max - row.get(i));
                    }
                }
        );
    }

    /**
     * Implements the Hungarian algorithm to solve the assignment problem for a bipartite graph.
     * This algorithm finds a maximum weight matching (or minimum cost perfect matching) in polynomial time.
     * Source: http://www.e-maxx-ru.1gb.ru/algo/assignment_hungary#5
     * <p>
     * Реализует венгерский алгоритм для решения задачи о назначениях для двудольного графа.
     * Этот алгоритм находит максимальное весовое сопоставление (или минимальное по стоимости идеальное сопоставление)
     * за полиномиальное время.
     * Источник: http://www.e-maxx-ru.1gb.ru/algo/assignment_hungary#5
     *
     * @param a The cost matrix (or weight matrix after reduction). / Матрица стоимости (или матрица весов после редукции).
     * @return A list where ans[i] is the column assigned to row i, or UN_INIT if no assignment. / Список, где ans[i] - столбец, назначенный строке i, или UN_INIT, если назначение отсутствует.
     */
    private static List<Integer> HungarianAlgorithm(List<List<Integer>> a) {
        int n = a.size() - 1; // Number of rows (0-indexed, so -1 to match 1-based logic in algorithm)
        int m = a.get(0).size() - 1; // Number of columns

        // Potentials for rows and columns, used to transform costs
        // Потенциалы для строк и столбцов, используемые для преобразования стоимостей
        List<Integer> u = new ArrayList<>(Collections.nCopies(n + 1, 0)); // Инициализируем нулями
        List<Integer> v = new ArrayList<>(Collections.nCopies(m + 1, 0)); // Инициализируем нулями

        // p[j] stores the row to which column j is assigned
        // p[j] хранит строку, которой назначен столбец j
        List<Integer> p = new ArrayList<>(Collections.nCopies(m + 1, 0)); // Инициализируем нулями

        // way[j] stores the predecessor of column j in the alternating path search
        // way[j] хранит предшественника столбца j при поиске чередующегося пути
        List<Integer> way = new ArrayList<>(Collections.nCopies(m + 1, 0)); // Инициализируем нулями

        for (int i = 1; i <= n; ++i) { // Iterate through each row
            p.set(0, i); // Initialize a "dummy" assignment for row i
            int j0 = 0; // Current column in path
            // minv[j] stores the minimum "slack" (a[i0][j] - u[i0] - v[j]) for non-covered columns
            // minv[j] хранит минимальный "слабину" (a[i0][j] - u[i0] - v[j]) для непокрытых столбцов
            List<Integer> minv = new ArrayList<>(Collections.nCopies(m + 1, Constants.INFINITY));
            // used[j] indicates if column j is covered by an alternating path
            // used[j] указывает, покрыт ли столбец j чередующимся путем
            List<Boolean> used = new ArrayList<>(Collections.nCopies(m + 1, false));

            do {
                used.set(j0, true); // Mark current column as used
                int i0 = p.get(j0); // Get the row assigned to current column
                int delta = Constants.INFINITY; // Minimum slack found in this iteration
                int j1 = -1; // Column with minimum slack

                // Find the minimum slack for non-covered columns
                // Находим минимальный слабину для непокрытых столбцов
                for (int j = 1; j <= m; ++j) {
                    if (!used.get(j)) {
                        int cur = a.get(i0).get(j) - u.get(i0) - v.get(j); // Calculate current slack
                        if (cur < minv.get(j)) {
                            minv.set(j, cur); // Update minimum slack
                            way.set(j, j0); // Record predecessor
                        }
                        if (minv.get(j) < delta) { // Find overall minimum slack
                            delta = minv.get(j);
                            j1 = j;
                        }
                    }
                }

                // Adjust potentials (u and v) and minv values
                // Корректируем потенциалы (u и v) и значения minv
                for (int j = 0; j <= m; ++j) {
                    if (used.get(j)) { // For covered columns, adjust u (rows assigned to them) and v
                        u.set(p.get(j), u.get(p.get(j)) + delta);
                        v.set(j, v.get(j) - delta);
                    } else { // For non-covered columns, just update minv
                        minv.set(j, minv.get(j) - delta);
                    }
                }

                if (j1 == -1) { // If no path found (should not happen in standard implementations if graph is balanced)
                    break;
                }

                j0 = j1; // Move to the next column in the alternating path
            } while (p.get(j0) != 0); // Continue until an unmatched row (p[j0] == 0) is found

            // Augment the path
            // Расширяем путь
            do {
                int j1 = way.get(j0);
                p.set(j0, p.get(j1));
                j0 = j1;
            } while (j0 != 0);
        }

        // Construct the final assignment result
        // Строим окончательный результат назначения
        List<Integer> ans = new ArrayList<>(Collections.nCopies(n + 1, UN_INIT));
        for (int j = 1; j <= m; ++j) {
            if (p.get(j) <= n) {  // Check to avoid IndexOutOfBoundsException, ensures p[j] is a valid row index
                ans.set(p.get(j), j); // ans[row] = column
            }
        }
        return ans;
    }

    /**
     * Represents an element in the DP table, extending GraphCompareResult.
     * It stores the result of a subproblem (matched vertices and label errors)
     * and provides methods for combining results.
     * <p>
     * Представляет элемент в таблице DP, расширяющий GraphCompareResult.
     * Он хранит результат подзадачи (сопоставленные вершины и ошибки меток)
     * и предоставляет методы для объединения результатов.
     */
    @EqualsAndHashCode
    public static class DpElement extends GraphCompareResult {

        /**
         * Returns the weight of this DpElement, which is the number of matching vertices.
         * <p>
         * Возвращает вес этого DpElement, который является количеством сопоставленных вершин.
         *
         * @return The number of matching vertices. / Количество сопоставленных вершин.
         */
        public int getWeight() {
            return getMatchingVertices().size();
        }

        /**
         * Returns the DpElement with the maximum weight (i.e., more matching vertices).
         * <p>
         * Возвращает DpElement с максимальным весом (т.е. большим количеством сопоставленных вершин).
         *
         * @param first  The first DpElement. / Первый DpElement.
         * @param second The second DpElement. / Второй DpElement.
         * @return The DpElement with the greater weight. / DpElement с большим весом.
         */
        public static DpElement max(DpElement first, DpElement second) {
            return (first.isBigger(second)) ? first : second;
        }

        /**
         * Adds the matching vertices of another DpElement to this one.
         * <p>
         * Добавляет сопоставленные вершины другого DpElement к текущему.
         *
         * @param other The other DpElement to add. / Другой DpElement для добавления.
         */
        public void addDpElement(DpElement other) {
            this.getMatchingVertices().putAll(other.getMatchingVertices());
        }

        /**
         * Adds a single matched pair of vertices to this DpElement.
         * <p>
         * Добавляет одну сопоставленную пару вершин к этому DpElement.
         *
         * @param first  The vertex number from the first graph. / Номер вершины из первого графа.
         * @param second The vertex number from the second graph. / Номер вершины из второго графа.
         */
        public void add(int first, int second) {
            getMatchingVertices().put(first, second);
        }

        /**
         * Converts this DpElement to a standard GraphCompareResult.
         * <p>
         * Преобразует этот DpElement в стандартный GraphCompareResult.
         *
         * @return A new GraphCompareResult instance. / Новый экземпляр GraphCompareResult.
         */
        public GraphCompareResult toGraphCompareResult() {
            return GraphCompareResult
                    .builder().
                    matchingVertices(getMatchingVertices())
                    .labelErrors(getLabelErrors())
                    .build();

        }
    }
}
