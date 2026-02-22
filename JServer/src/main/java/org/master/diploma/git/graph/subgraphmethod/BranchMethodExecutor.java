package org.master.diploma.git.graph.subgraphmethod;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.master.diploma.git.graph.Branch;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.Label;
import org.master.diploma.git.support.BranchLCSHelper;
import org.master.diploma.git.support.Multisets;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements a graph comparison method that focuses on matching branches between two graphs.
 * It identifies all possible branches in both graphs, finds the best matches between them
 * based on label similarity, and then uses a Longest Common Subsequence (LCS) approach
 * to compare the matched branches.
 * <p>
 * Реализует метод сравнения графов, который сосредоточен на сопоставлении ветвей (branches) между двумя графами.
 * Он идентифицирует все возможные ветви в обоих графах, находит наилучшие совпадения между ними
 * на основе сходства меток, а затем использует подход Наибольшей Общей Подпоследовательности (LCS)
 * для сравнения сопоставленных ветвей.
 */
public class BranchMethodExecutor extends SubgraphMethodExecutor {

    /** Logger for this class. <p> Логгер для этого класса. */
    private static final Logger LOG = LogManager.getLogger(BranchMethodExecutor.class);
    /**
     * Executes the branch-based graph comparison method.
     * This method first extracts all branches from both graphs, then identifies matching branches
     * based on label similarity, and finally computes the Longest Common Subsequence (LCS) for
     * each matched branch pair to determine the overall graph comparison result.
     * <p>
     * Выполняет метод сравнения графов на основе ветвей.
     * Этот метод сначала извлекает все ветви из обоих графов, затем определяет совпадающие ветви
     * на основе сходства меток и, наконец, вычисляет Наибольшую Общую Подпоследовательность (LCS) для
     * каждой пары совпадающих ветвей для определения общего результата сравнения графов.
     *
     * @param first  The first graph to compare. / Первый граф для сравнения.
     * @param second The second graph to compare. / Второй граф для сравнения.
     * @param <T>    The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A GraphCompareResult object containing the matching vertices and label errors. / Объект GraphCompareResult, содержащий сопоставленные вершины и ошибки меток.
     */
    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> first, Graph<T> second) {

        // 1. Get all branches from both graphs
        // 1. Получаем все ветви из обоих графов
        List<Branch<T>> firstAllBranches = getAllBranches(first);
        List<Branch<T>> secondAllBranches = getAllBranches(second);

        // 2. Find matches between branches based on label similarity
        // 2. Находим совпадения между ветвями на основе сходства меток
        List<BranchMatch<T>> branchMatches = getBranchMatches(firstAllBranches, secondAllBranches);

        GraphCompareResult graphCompareResult = new GraphCompareResult();
        Set<Integer> removedG1Vertices = new HashSet<>();
        Set<Integer> removedG2Vertices = new HashSet<>();

        // 3. For each matched branch pair, find their Longest Common Subsequence (LCS)
        // 3. Для каждой совпадающей пары ветвей находим их Наибольшую Общую Подпоследовательность (LCS)
        branchMatches.forEach(
                branchMatch -> {
                    // Find LCS for the current branch match
                    // Находим LCS для текущего совпадения ветвей
                    var result = BranchLCSHelper.findBranchLCS(branchMatch);
                    int prev = graphCompareResult.getMatchingVertices().size();
                    // Accumulate results
                    // Накапливаем результаты
                    graphCompareResult.add(result);
                    int next = graphCompareResult.getMatchingVertices().size();
                    
                    // If new matches were added, track which vertices were NOT matched within these branches
                    // Если были добавлены новые совпадения, отслеживаем, какие вершины НЕ были сопоставлены в этих ветвях
                    if (next != prev) {
                        removedG1Vertices.addAll(
                                Sets.difference(
                                        branchMatch
                                                .firstBranch
                                                .getVertexNumbers()
                                        ,
                                        result.getMatchingVertices().keySet()
                                )
                        );
                        removedG2Vertices.addAll(
                                Sets.difference(
                                        branchMatch
                                                .secondBranch
                                                .getVertexNumbers(),
                                        new HashSet<>(result.getMatchingVertices().values())
                                )
                        );
                    }
                }
        );

        // Remove any vertices from the overall comparison result that were explicitly deemed "removed" during branch matching
        // Удаляем из общего результата сравнения любые вершины, которые были явно помечены как "удаленные" во время сопоставления ветвей
        graphCompareResult.removeMatchingVertex(removedG1Vertices, removedG2Vertices);
        // Add label errors for all vertices in the graphs
        // Добавляем ошибки меток для всех вершин в графах
        graphCompareResult.addLabelErrors(first, second);
        return graphCompareResult;
    }

    /**
     * Finds potential matches between branches from the first and second graphs.
     * Matches are created for every possible pair of branches and then filtered to ensure
     * that each branch is used in at most one match, prioritizing matches with higher similarity.
     * <p>
     * Находит потенциальные совпадения между ветвями из первого и второго графов.
     * Совпадения создаются для каждой возможной пары ветвей, а затем фильтруются, чтобы гарантировать,
     * что каждая ветвь используется максимум в одном совпадении, отдавая предпочтение совпадениям с более высоким сходством.
     *
     * @param firstAllBranches  List of all branches from the first graph. / Список всех ветвей из первого графа.
     * @param secondAllBranches List of all branches from the second graph. / Список всех ветвей из второго графа.
     * @param <T>               The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A sorted and filtered list of BranchMatch objects. / Отсортированный и отфильтрованный список объектов BranchMatch.
     */
    private <T extends LabelVertex<?>> List<BranchMatch<T>> getBranchMatches(
            List<Branch<T>> firstAllBranches,
            List<Branch<T>> secondAllBranches
    ) {
        List<BranchMatch<T>> branchMatches = new ArrayList<>();

        // Create all possible pairs of branches and calculate their match percentage
        // Создаем все возможные пары ветвей и вычисляем процент их совпадения
        for (var firstBranch : firstAllBranches) {
            for (var secondBranch : secondAllBranches) {
                branchMatches.add(new BranchMatch<>(firstBranch, secondBranch));
            }
        }

        // Sort matches by percentage match in descending order
        // Сортируем совпадения по проценту совпадения в порядке убывания
        branchMatches.sort(Comparator.comparing(BranchMatch::getPercentageMatch));
        Collections.reverse(branchMatches);


        List<BranchMatch<T>> result = new ArrayList<>();
        Set<UUID> branchIds = new HashSet<>(); // Keep track of already used branch UUIDs

        // Filter matches to ensure each branch is used at most once
        // Фильтруем совпадения, чтобы гарантировать, что каждая ветвь используется не более одного раза
        branchMatches.forEach(
                branchMatch -> {
                    // Only add if neither branch has been used in a previous, better match
                    // Добавляем только если ни одна из ветвей не была использована в предыдущем, лучшем совпадении
                    if (
                            !branchIds.contains(branchMatch.firstBranch.getUuid()) &&
                                    !branchIds.contains(branchMatch.secondBranch.getUuid())
                    ) {
                        branchIds.add(branchMatch.firstBranch.getUuid());
                        branchIds.add(branchMatch.secondBranch.getUuid());
                        result.add(branchMatch);
                    }
                }
        );


        return result;
    }


    /**
     * Recursively traverses the graph to find all possible branches starting from the root.
     * A branch is defined as a path from the root to a leaf node.
     * <p>
     * Рекурсивно обходит граф для поиска всех возможных ветвей, начиная от корня.
     * Ветвь определяется как путь от корневого узла до листового узла.
     *
     * @param graph The graph to extract branches from. / Граф, из которого извлекаются ветви.
     * @param <T>   The type of Vertex used in the graph. / Тип Vertex, используемый в графе.
     * @return A list of all branches found in the graph. / Список всех ветвей, найденных в графе.
     */
    private <T extends Vertex> List<Branch<T>> getAllBranches(Graph<T> graph) {

        List<Branch<T>> allBranches = new ArrayList<>();
        // Start the recursive traversal from the root of the graph
        // Начинаем рекурсивный обход от корня графа
        getAllBranches(
                graph.getVertex(graph.getRoot()),
                graph,
                new ArrayList<>(), // Current path for the branch
                allBranches        // Accumulator for all found branches
        );

        return allBranches;
    }

    /**
     * Recursive helper method to find all branches in a graph using Depth First Search (DFS).
     * It builds a branch path from the current node and adds it to the list of all branches
     * when a leaf node is reached.
     * <p>
     * Рекурсивный вспомогательный метод для поиска всех ветвей в графе с использованием поиска в глубину (DFS).
     * Он строит путь ветви от текущего узла и добавляет его в список всех ветвей,
     * когда достигается листовой узел.
     *
     * @param cur         The current vertex being visited. / Текущая посещаемая вершина.
     * @param graph       The graph being traversed. / Обходимый граф.
     * @param branch      The current branch path being built. / Текущий строящийся путь ветви.
     * @param allBranches The list to accumulate all found branches. / Список для накопления всех найденных ветвей.
     * @param <T>         The type of Vertex used in the graph. / Тип Vertex, используемый в графе.
     */
    private <T extends Vertex> void getAllBranches(
            T cur,
            Graph<T> graph,
            ArrayList<T> branch,
            List<Branch<T>> allBranches
    ) {
        // Add the current vertex to the branch path
        // Добавляем текущую вершину к пути ветви
        branch.add(cur);

        // If the current vertex is a leaf (no children), add the current branch to the list of all branches
        // Если текущая вершина является листом (нет потомков), добавляем текущую ветвь в список всех ветвей
        if (graph.getChildren(cur.getNumber()).isEmpty()) {
            allBranches.add(
                    new Branch<>(
                            UUID.randomUUID(), // Assign a unique ID to the branch
                            new ArrayList<>(branch) // Create a new list to avoid modification issues with shared 'branch'
                    )
            );
        }

        // Recursively call for each child
        // Рекурсивно вызываем для каждого потомка
        for (var child : graph.getChildren(cur.getNumber())) {
            getAllBranches(child, graph, branch, allBranches);
        }

        // Backtrack: remove the current vertex from the branch path
        // Возврат: удаляем текущую вершину из пути ветви
        branch.remove(branch.size() - 1);
    }


    /**
     * Represents a potential match between two branches, along with their calculated similarity.
     * <p>
     * Представляет потенциальное совпадение между двумя ветвями, а также их рассчитанное сходство.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class BranchMatch<T extends LabelVertex<?>> {
        /**
         * The calculated percentage of similarity between the two branches based on their labels.
         * <p>
         * Рассчитанный процент сходства между двумя ветвями на основе их меток.
         */
        private double percentageMatch;
        /**
         * The branch from the first graph.
         * <p>
         * Ветвь из первого графа.
         */
        private Branch<T> firstBranch;
        /**
         * The branch from the second graph.
         * <p>
         * Ветвь из второго графа.
         */
        private Branch<T> secondBranch;

        /**
         * Constructor that calculates the percentage match between two branches based on their labels.
         * <p>
         * Конструктор, который вычисляет процент совпадения между двумя ветвями на основе их меток.
         *
         * @param firstBranch  The branch from the first graph. / Ветвь из первого графа.
         * @param secondBranch The branch from the second graph. / Ветвь из второго графа.
         */
        public BranchMatch(Branch<T> firstBranch, Branch<T> secondBranch) {
            Multiset<Label> firstLabels = HashMultiset.create();
            Multiset<Label> secondLabels = HashMultiset.create();

            // Collect all labels from the first branch's vertices into a multiset
            // Собираем все метки вершин первой ветви в мультимножество
            firstBranch
                    .getVertices()
                    .stream()
                    .map(vertex -> vertex.getLabels())
                    .flatMap(List::stream)
                    .forEach(firstLabels::add);

            // Collect all labels from the second branch's vertices into a multiset
            // Собираем все метки вершин второй ветви в мультимножество
            secondBranch
                    .getVertices()
                    .stream()
                    .map(vertex -> vertex.getLabels())
                    .flatMap(List::stream)
                    .forEach(secondLabels::add);

            this.firstBranch = firstBranch;
            this.secondBranch = secondBranch;
            this.percentageMatch = calculatePercentageMatch(firstLabels, secondLabels);
        }
    }

    /**
     * Calculates the percentage match between two multisets of labels.
     * The match is determined by the size of their intersection divided by the size of the larger multiset.
     * <p>
     * Вычисляет процент совпадения между двумя мультимножествами меток.
     * Совпадение определяется размером их пересечения, деленным на размер большего мультимножества.
     *
     * @param firstLabels  The first multiset of labels. / Первое мультимножество меток.
     * @param secondLabels The second multiset of labels. / Второе мультимножество меток.
     * @param <T>          The type of labels. / Тип меток.
     * @return The percentage match as a double, from 0.0 to 1.0. / Процент совпадения в виде double, от 0.0 до 1.0.
     */
    private static <T> double calculatePercentageMatch(Multiset<T> firstLabels, Multiset<T> secondLabels) {
        Multiset<T> intersection = Multisets.intersect(firstLabels, secondLabels);
        int intersectionSize = intersection.size();
        // Use the size of the larger multiset to normalize the percentage match.
        // Используем размер большего мультимножества для нормализации процента совпадения.
        int minSize = Math.max(firstLabels.size(), secondLabels.size());

        // Prevent division by zero if both multisets are empty or one of them is empty.
        // Предотвращаем деление на ноль, если оба мультимножества пусты или одно из них пусто.
        return (minSize == 0) ? 0.0 : (double) intersectionSize / minSize;
    }
}
