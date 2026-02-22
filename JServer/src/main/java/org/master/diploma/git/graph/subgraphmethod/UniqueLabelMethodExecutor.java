package org.master.diploma.git.graph.subgraphmethod;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import lombok.*;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.exception.GraphException;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.Label;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * Implements a subgraph comparison method that assumes each vertex has a unique label,
 * simplifying the matching process. This method focuses on finding a matching based
 * on these unique labels and their parent-child relationships, effectively finding
 * the largest common subgraph where labels are unique identifiers.
 * <p>
 * Реализует метод сравнения подграфов, который предполагает, что каждая вершина имеет уникальную метку,
 * что упрощает процесс сопоставления. Этот метод сосредоточен на поиске сопоставления на основе
 * этих уникальных меток и их родительско-дочерних отношений, эффективно находя
 * наибольший общий подграф, где метки являются уникальными идентификаторами.
 */
public class UniqueLabelMethodExecutor extends SubgraphMethodExecutor {


    /** Gson instance for JSON serialization/deserialization. <p> Экземпляр Gson для сериализации/десериализации JSON. */
    public static final Gson GSON = new Gson();

    /** Expected number of labels per vertex for this method. <p> Ожидаемое количество меток на вершину для этого метода. */
    private static final int LABEL_COUNT = 1;

    /**
     * Executes the unique label based subgraph comparison method.
     * This method works by first filtering out vertices with non-intersecting labels,
     * then building parent relationships, and finally finding the largest set of
     * matched vertices using a recursive search and a brute-force combination approach.
     * <p>
     * Выполняет метод сравнения подграфов на основе уникальных меток.
     * Этот метод сначала отфильтровывает вершины с непересекающимися метками,
     * затем строит родительские отношения и, наконец, находит наибольший набор
     * сопоставленных вершин, используя рекурсивный поиск и подход полного перебора комбинаций.
     *
     * @param g1 The first graph to compare. / Первый граф для сравнения.
     * @param g2 The second graph to compare. / Второй граф для сравнения.
     * @param <T> The type of LabelVertex used in the graphs. / Тип LabelVertex, используемый в графах.
     * @return A GraphCompareResult object containing the matching vertices and label errors. / Объект GraphCompareResult, содержащий сопоставленные вершины и ошибки меток.
     */
    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> g1, Graph<T> g2) {
        // Optional graph checks (commented out)
        // Необязательные проверки графа (закомментированы)
//        checkGraph(g1);
//        checkGraph(g2);

        // Clone graphs to avoid modifying originals
        // Клонируем графы, чтобы избежать изменения оригиналов
        var first = g1.clone();
        var second = g2.clone();

        // Get labels that are common to both graphs
        // Получаем метки, которые являются общими для обоих графов
        Set<Integer> intersectionLabels = getIntersectionLabels(first, second);

        // Define a consumer to remove vertices whose labels are not in the intersection
        // Определяем потребителя для удаления вершин, метки которых не входят в пересечение
        Consumer<Graph<T>> removeDiffLabels = (graph) -> {
            List<T> removedVertex = new ArrayList<>();
            for (T v : graph.getVertices()) {
                if (!intersectionLabels.contains(labelFromVertex(v))) {
                    removedVertex.add(v);
                }
            }
            removedVertex.forEach(graph::removeVertex);
        };

        // Apply the filter to both graphs
        // Применяем фильтр к обоим графам
        removeDiffLabels.accept(first);
        removeDiffLabels.accept(second);

        // If either graph becomes empty after filtering, return an empty comparison result
        // Если какой-либо граф становится пустым после фильтрации, возвращаем пустой результат сравнения
        if (first.getVertices().isEmpty() || second.getVertices().isEmpty()) {
            GraphCompareResult graphCompareResult = new GraphCompareResult();
            graphCompareResult.addLabelErrors(g1, g2); // Still add label errors based on original graphs
            return graphCompareResult;
        }

        // Build parent relationships for both graphs
        // Строим родительские отношения для обоих графов
        Map<Integer, List<Integer>> firstAllParents = getAllParents(first);
        Map<Integer, List<Integer>> secondAllParents = getAllParents(second);

        // Find the best set of matching vertices using a recursive search
        // Находим наилучший набор сопоставленных вершин с помощью рекурсивного поиска
        VertexSet<T> currentVertexSet = findResult(
                new GraphContainer<>(
                        firstAllParents,
                        first,
                        first.getVertex(first.getRoot()), // Start from the root of the first graph
                        new HashMap<>() // Initial empty vertex set map for first graph
                ),
                new GraphContainer<>(
                        secondAllParents,
                        second,
                        second.getVertex(second.getRoot()) // Start from the root of the second graph
                )
        )
                .stream()
                .max(Comparator.comparingInt(vertexSet -> vertexSet.vertices.size())) // Get the set with max matched vertices
                .get();

        // Create a map from unique label ID to vertex number for the second graph
        // Создаем карту из уникального ID метки в номер вершины для второго графа
        Map<Integer, Integer> secondLabelToVertex = second
                .getVertices()
                .stream()
                .collect(
                        Collectors.toMap(
                                UniqueLabelMethodExecutor::labelFromVertex,
                                Vertex::getNumber,
                                (existing, replacement) -> existing // Handle duplicate labels by keeping existing
                        )
                );

        // Build the final matching vertices map from the best found vertex set
        // Строим окончательную карту сопоставленных вершин из наилучшего найденного набора вершин
        Map<Integer, Integer> map = currentVertexSet
                .vertices
                .stream()
                .collect(Collectors.toMap(
                                Vertex::getNumber, // Key: vertex number from first graph
                                vertex -> secondLabelToVertex.get(labelFromVertex(vertex)) // Value: corresponding vertex number from second graph
                        )
                );

        // Construct the final GraphCompareResult
        // Создаем итоговый GraphCompareResult
        GraphCompareResult result = GraphCompareResult
                .builder()
                .matchingVertices(map)
                .labelErrors(new HashMap<>()) // Initialize with empty label errors
                .build();

        // Fill in detailed label errors based on the original graphs
        // Заполняем подробные ошибки меток на основе исходных графов
        result.fillLabelError(g1, g2);
        return result;
    }

    /**
     * Recursive method to find possible VertexSet matches by exploring parent relationships.
     * This forms the core logic for matching subgraphs based on unique labels and their ancestry.
     * <p>
     * Рекурсивный метод для поиска возможных совпадений VertexSet путем исследования родительских связей.
     * Это формирует основную логику для сопоставления подграфов на основе уникальных меток и их предков.
     *
     * @param first  GraphContainer for the first graph. / GraphContainer для первого графа.
     * @param second GraphContainer for the second graph. / GraphContainer для второго графа.
     * @param <T>    The type of LabelVertex. / Тип LabelVertex.
     * @return A set of VertexSet objects representing potential common subgraphs. / Набор объектов VertexSet, представляющих потенциальные общие подграфы.
     */
    private <T extends LabelVertex<?>> Set<VertexSet<T>> findResult(
            GraphContainer<T> first,
            GraphContainer<T> second
    ) {

        int currentVertexNumber = labelFromVertex(first.vertex);
        // Find the index of the current vertex in the second graph's parent list
        // Находим индекс текущей вершины в списке родителей второго графа
        int currentParentNumber = second.allParents.getOrDefault(currentVertexNumber, Collections.emptyList()).size() - 1;
        List<Integer> allSecondParents = second.allParents.get(currentVertexNumber);
        List<Integer> allFirstParents = first.allParents.get(currentVertexNumber);

        Set<Integer> conflictVertices = new HashSet<>(); // Vertices that cause conflict
        Set<T> usedVertices = new HashSet<>(); // Vertices already part of a found match
        Set<VertexSet<T>> result = new HashSet<>();

        // Iterate through parents of the current vertex in the second graph
        // Итерируем по родителям текущей вершины во втором графе
        while (currentParentNumber >= 0) {
            // If the parent from the second graph is also a parent in the first graph's vertexSet
            // Если родитель из второго графа также является родителем в vertexSet первого графа
            if (first.vertexSet.containsKey(allSecondParents.get(currentParentNumber))) {

                Set<Integer> conflictFirstVertices = new HashSet<>();
                int index = allFirstParents.size() - 1;
                // Identify conflicting vertices in the first graph's parent chain
                // Идентифицируем конфликтующие вершины в родительской цепочке первого графа
                while (index >= 0 && !Objects.equals(allFirstParents.get(index), allSecondParents.get(currentParentNumber))) {
                    conflictFirstVertices.add(allFirstParents.get(index--));
                }

                VertexSet<T> parentVertexSet = first.vertexSet.get(allSecondParents.get(currentParentNumber));
                // If there are unmatched vertices in the parent's vertexSet, continue DFS
                // Если в VertexSet родителя есть несопоставленные вершины, продолжаем DFS
                if (!Sets.difference(parentVertexSet.vertices, usedVertices).isEmpty()) {
                    usedVertices.addAll(parentVertexSet.vertices);
                    result.addAll(
                            dfs(
                                    first,
                                    second,
                                    Sets.union( // Union of current conflicts and conflicts from first graph parents
                                            conflictVertices,
                                            conflictFirstVertices
                                    ),
                                   parentVertexSet
                            )
                    );
                }
            } else {
                // If no match found for parent, add to conflict
                // Если совпадение для родителя не найдено, добавляем в конфликт
                conflictVertices.add(allSecondParents.get(currentParentNumber));
            }
            currentParentNumber--;
        }

        // If no results found, perform a DFS with all conflicts
        // Если результатов не найдено, выполняем DFS со всеми конфликтами
        if (result.isEmpty()) {
            result.addAll(
                    dfs(
                            first,
                            second,
                            Sets.union(conflictVertices, new HashSet<>(allFirstParents)),
                            null
                    )
            );
        }

        return result;
    }

    /**
     * Performs a Depth-First Search (DFS) to explore potential subgraph matches.
     * This method recursively builds VertexSet objects by combining current vertex matches
     * with matches from their children, considering conflict labels.
     * <p>
     * Выполняет поиск в глубину (DFS) для исследования потенциальных совпадений подграфов.
     * Этот метод рекурсивно строит объекты VertexSet, объединяя совпадения текущих вершин
     * с совпадениями их потомков, учитывая конфликтующие метки.
     *
     * @param first          GraphContainer for the first graph. / GraphContainer для первого графа.
     * @param second         GraphContainer for the second graph. / GraphContainer для второго графа.
     * @param conflictVertices Set of labels that are currently in conflict. / Набор меток, которые в настоящее время конфликтуют.
     * @param parentVertexSet The VertexSet from the parent in the DFS traversal. / VertexSet от родителя при обходе DFS.
     * @param <T>            The type of LabelVertex. / Тип LabelVertex.
     * @return A set of VertexSet objects representing valid common subgraphs found through this path. / Набор объектов VertexSet, представляющих допустимые общие подграфы, найденные по этому пути.
     */
    private <T extends LabelVertex<?>> Set<VertexSet<T>> dfs(
            GraphContainer<T> first,
            GraphContainer<T> second,
            Set<Integer> conflictVertices,
            VertexSet<T> parentVertexSet
            ) {
        // Build a VertexSet for the current vertex
        // Создаем VertexSet для текущей вершины
        VertexSet<T> curVertexSet = VertexSet
                .<T>builder()
                .conflictLabels(conflictVertices)
                .vertices(Set.of(first.vertex)) // Current vertex as a single-element set
                .build();

        // If a parent VertexSet exists, merge it with the current one
        // Если существует родительский VertexSet, объединяем его с текущим
        if (parentVertexSet != null) {
            curVertexSet = curVertexSet.merge(parentVertexSet);
        }

        // Create a copy of the parent's vertexSet map and add the current vertexSet
        // Создаем копию карты vertexSet родителя и добавляем текущий vertexSet
        Map<Integer, VertexSet<T>> curVertexSetMap = (Map<Integer, VertexSet<T>>)
                ((HashMap<Integer, VertexSet<T>>) first
                        .getVertexSet())
                        .clone();

        curVertexSetMap.put(labelFromVertex(first.vertex), curVertexSet);

        // Recursively find results for children of the current vertex
        // Рекурсивно находим результаты для потомков текущей вершины
        List<Set<VertexSet<T>>> list = first.graph
                .getChildren(first.vertex)
                .stream()
                .map(vertex -> findResult(
                                new GraphContainer<>(
                                        first.getAllParents(),
                                        first.graph,
                                        vertex,
                                        curVertexSetMap
                                ),
                                second
                        )
                )
                .toList();

        // Flatten the list of sets into a single set of VertexSet objects
        // Преобразуем список наборов в один набор объектов VertexSet
        Set<VertexSet<T>> vertexSets = list
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        // Perform brute-force combination of found VertexSets from children
        // Выполняем комбинации методом полного перебора из найденных VertexSets от потомков
        bruteforce(vertexSets, list, 0, curVertexSet);
        return vertexSets;
    }

    /**
     * Recursively explores combinations of VertexSets from children to find all valid merged VertexSets.
     * This method essentially performs a brute-force combination of results from subproblems.
     * <p>
     * Рекурсивно исследует комбинации VertexSets из потомков для поиска всех допустимых объединенных VertexSet.
     * Этот метод по существу выполняет комбинации результатов из подзадач методом полного перебора.
     *
     * @param vertexSets   The set to accumulate all valid merged VertexSets. / Набор для накопления всех допустимых объединенных VertexSet.
     * @param list         A list of sets, where each set contains VertexSets from a child's subproblem. / Список наборов, где каждый набор содержит VertexSets из подзадачи потомка.
     * @param index        The current index in the 'list' being processed. / Текущий индекс в обрабатываемом 'list'.
     * @param curVertexSet The current VertexSet being built through merging. / Текущий VertexSet, создаваемый путем объединения.
     * @param <T>          The type of LabelVertex. / Тип LabelVertex.
     */
    private <T extends LabelVertex<?>> void bruteforce(
            Set<VertexSet<T>> vertexSets,
            List<Set<VertexSet<T>>> list,
            int index,
            VertexSet<T> curVertexSet
    ) {
        // Base case: if all children's results have been processed, add the current merged VertexSet
        // Базовый случай: если все результаты потомков обработаны, добавляем текущий объединенный VertexSet
        if (index == list.size()) {
            vertexSets.add(curVertexSet);
            return;
        }

        // Option 1: Don't include any VertexSet from the current child's results
        // Вариант 1: Не включать ни один VertexSet из результатов текущего потомка
        bruteforce(vertexSets, list, index + 1, curVertexSet);

        // Option 2: Try to merge each VertexSet from the current child's results
        // Вариант 2: Пытаемся объединить каждый VertexSet из результатов текущего потомка
        for (var tmp : list.get(index)) {
            // Only merge if there's no intersection of vertices, maintaining validity
            // Объединяем только если нет пересечения вершин, сохраняя валидность
            if (!tmp.isEmptyIntersection(curVertexSet)) {
                bruteforce(vertexSets, list, index + 1, curVertexSet.merge(tmp));
            }
        }
    }

    /**
     * Computes a map of all parents for each vertex in the graph.
     * The key is a vertex number, and the value is a list of its parent vertex numbers.
     * This method first computes all children and then inverts the relationship to find parents.
     * <p>
     * Вычисляет карту всех родителей для каждой вершины в графе.
     * Ключом является номер вершины, а значением — список номеров ее родительских вершин.
     * Этот метод сначала вычисляет всех потомков, а затем инвертирует отношение для поиска родителей.
     *
     * @param graph The graph to analyze. / Граф для анализа.
     * @param <T>   The type of LabelVertex. / Тип LabelVertex.
     * @return A map where keys are vertex numbers and values are lists of their parent vertex numbers. / Карта, где ключами являются номера вершин, а значениями — списки номеров их родительских вершин.
     */
    private <T extends LabelVertex<?>> Map<Integer, List<Integer>> getAllParents(Graph<T> graph) {
        Map<Integer, Set<Integer>> allChildren = getAllChildren(graph);

        // LinkedHashMap is used to preserve insertion order, which might be important for deterministic results.
        // LinkedHashMap используется для сохранения порядка вставки, что может быть важно для детерминированных результатов.
        Map<Integer, List<Integer>> result = new LinkedHashMap<>();

        // Invert the child relationships to find parents
        // Инвертируем отношения потомков для поиска родителей
        allChildren.forEach(
                (key, set) -> {
                    set.forEach(
                            value -> {
                                if (!result.containsKey(value)) {
                                    result.put(value, new ArrayList<>());
                                }
                                result.get(value).add(key); // Add key (parent) to value's (child's) parent list
                            }
                    );
                }
        );

        // Ensure all vertices have an entry in the result map, even if they have no parents (roots)
        // Гарантируем, что все вершины имеют запись в карте результатов, даже если у них нет родителей (корни)
        allChildren
                .keySet()
                .forEach(
                        key -> {
                            if (!result.containsKey(key)) {
                                result.put(key, new ArrayList<>());
                            }
                        }
                );

        return result;
    }

    /**
     * Computes a map of all (direct and indirect) children for each vertex in the graph.
     * The key is a vertex's label, and the value is a set of labels of its children.
     * <p>
     * Вычисляет карту всех (прямых и косвенных) потомков для каждой вершины в графе.
     * Ключом является метка вершины, а значением — набор меток ее потомков.
     *
     * @param graph The graph to analyze. / Граф для анализа.
     * @param <T>   The type of LabelVertex. / Тип LabelVertex.
     * @return A map where keys are vertex labels and values are sets of their children's labels. / Карта, где ключами являются метки вершин, а значениями — наборы меток их потомков.
     */
    private <T extends LabelVertex<?>> Map<Integer, Set<Integer>> getAllChildren(Graph<T> graph) {

        Map<Integer, Set<Integer>> result = new LinkedHashMap<>(); // LinkedHashMap to maintain insertion order
        // Start recursive traversal from the graph's root
        // Начинаем рекурсивный обход от корня графа
        getAllChildren(graph.getVertex(graph.getRoot()), graph, result);

        return result;
    }

    /**
     * Recursive helper method to populate the map of all children for each vertex.
     * It performs a DFS-like traversal to find all direct and indirect children.
     * <p>
     * Рекурсивный вспомогательный метод для заполнения карты всех потомков для каждой вершины.
     * Он выполняет обход, подобный DFS, для поиска всех прямых и косвенных потомков.
     *
     * @param vertex The current vertex being processed. / Текущая обрабатываемая вершина.
     * @param graph  The graph being traversed. / Обходимый граф.
     * @param result The map to populate with vertex labels and their children's labels. / Карта для заполнения метками вершин и метками их потомков.
     * @param <T>    The type of LabelVertex. / Тип LabelVertex.
     */
    private <T extends LabelVertex<?>> void getAllChildren(T vertex, Graph<T> graph, Map<Integer, Set<Integer>> result) {
        int actualLabel = labelFromVertex(vertex);
        // Ensure the current vertex's label is in the result map
        // Убедимся, что метка текущей вершины находится в карте результатов
        if (!result.containsKey(actualLabel)) {
            result.put(actualLabel, new HashSet<>());
        }

        // Recursively process each child
        // Рекурсивно обрабатываем каждого потомка
        graph.getChildren(vertex.getNumber()).forEach(
                child -> {
                    // Add direct child's label
                    // Добавляем метку прямого потомка
                    result.get(actualLabel).add(labelFromVertex(child));
                    // Recurse for the child
                    getAllChildren(child, graph, result); //todo if graph can have cycles, add a 'used' set to prevent infinite loops / если в графе могут быть циклы, добавить набор 'used' для предотвращения бесконечных циклов
                    // Add all indirect children of the child to the current vertex's children set
                    // Добавляем всех косвенных потомков потомка в набор потомков текущей вершины
                    result.get(actualLabel).addAll(result.get(labelFromVertex(child)));
                }
        );
        // No explicit return value as it modifies the 'result' map
        // Нет явного возвращаемого значения, так как изменяется карта 'result'
    }


    /**
     * Checks if the graph adheres to the constraints required by this unique label method.
     * Specifically, it verifies that each vertex has exactly one label and that all labels are unique across the graph.
     * <p>
     * Проверяет, соответствует ли граф ограничениям, требуемым этим методом уникальных меток.
     * В частности, он проверяет, что каждая вершина имеет ровно одну метку и что все метки уникальны в графе.
     *
     * @param graph The graph to check. / Граф для проверки.
     * @param <T>   The type of LabelVertex. / Тип LabelVertex.
     * @throws GraphException If the graph violates the unique label constraints. / Если граф нарушает ограничения уникальных меток.
     */
    private <T extends LabelVertex<?>> void checkGraph(Graph<T> graph) {
        // Initialize min and max label counts per vertex
        // Инициализируем минимальное и максимальное количество меток на вершину
        int mn = LABEL_COUNT;
        int mx = LABEL_COUNT;

        // Iterate through vertices to find min/max label counts
        // Итерируем по вершинам, чтобы найти мин/макс количество меток
        for (var vertex : graph.getVertices()) {
            mn = Math.min(mn, vertex.getLabels().size());
            mx = Math.max(mx, vertex.getLabels().size());
        }

        // Check if each vertex has exactly LABEL_COUNT (which is 1) label
        // Проверяем, имеет ли каждая вершина ровно LABEL_COUNT (т.е. 1) метку
        if (mn < LABEL_COUNT || mx > LABEL_COUNT) {
            throw new GraphException("vertex should has only one label");
        }

        // Collect all unique label IDs from the graph
        // Собираем все уникальные идентификаторы меток из графа
        Set<Integer> allLabels = graph
                .getVertices()
                .stream()
                .map(LabelVertex::getLabels) // Get list of labels for each vertex
                .flatMap(List::stream)      // Flatten the list of lists into a single stream of labels
                .map(Label::getId)          // Get the ID of each label
                .collect(Collectors.toSet()); // Collect unique label IDs into a set

        // Check if the number of unique labels is equal to the number of vertices, ensuring all labels are unique
        // Проверяем, равно ли количество уникальных меток количеству вершин, гарантируя уникальность всех меток
        if (allLabels.size() < graph.getVertices().size()) {
            throw new GraphException("Labels should be unique");
        }
    }



    /**
     * Finds the set of labels that are common to both graphs.
     * <p>
     * Находит набор меток, которые являются общими для обоих графов.
     *
     * @param first  The first graph. / Первый граф.
     * @param second The second graph. / Второй граф.
     * @param <T>    The type of LabelVertex. / Тип LabelVertex.
     * @return A set of intersecting label IDs. / Набор пересекающихся идентификаторов меток.
     */
    private <T extends LabelVertex<?>> Set<Integer> getIntersectionLabels(Graph<T> first, Graph<T> second) {
        return Sets.intersection(
                getLabels(first),
                getLabels(second)
        );
    }

    /**
     * Extracts the label ID from a given LabelVertex.
     * Assumes each vertex has at least one label, and takes the first one.
     * <p>
     * Извлекает идентификатор метки из заданной LabelVertex.
     * Предполагается, что каждая вершина имеет хотя бы одну метку, и берется первая.
     *
     * @param vertex The LabelVertex to extract the label from. / LabelVertex, из которой извлекается метка.
     * @param <T>    The type of LabelVertex. / Тип LabelVertex.
     * @return The ID of the first label associated with the vertex. / Идентификатор первой метки, связанной с вершиной.
     */
    private static <T extends LabelVertex<?>> int labelFromVertex(T vertex) {
        return vertex.getLabels().get(0).getId();
    }


    /**
     * Collects all unique label IDs present in a given graph.
     * <p>
     * Собирает все уникальные идентификаторы меток, присутствующие в заданном графе.
     *
     * @param graph The graph to collect labels from. / Граф, из которого собираются метки.
     * @param <T>   The type of LabelVertex. / Тип LabelVertex.
     * @return A set of all unique label IDs in the graph. / Набор всех уникальных идентификаторов меток в графе.
     */
    private <T extends LabelVertex<?>> Set<Integer> getLabels(Graph<T> graph) {
        return graph
                .getVertices()
                .stream()
                .map(LabelVertex::getLabels) // Get list of labels for each vertex
                .flatMap(List::stream)      // Flatten the list of lists into a single stream of labels
                .map(Label::getId)          // Get the ID of each label
                .collect(Collectors.toSet()); // Collect unique label IDs into a set
    }


    /**
     * A helper class to bundle graph-related information needed during the recursive search.
     * <p>
     * Вспомогательный класс для объединения информации, связанной с графом, необходимой во время рекурсивного поиска.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    private static class GraphContainer<T extends LabelVertex<?>> {
        /**
         * Map of all parents for each vertex in the graph.
         * <p>
         * Карта всех родителей для каждой вершины в графе.
         */
        private Map<Integer, List<Integer>> allParents;
        /**
         * The graph itself.
         * <p>
         * Сам граф.
         */
        private Graph<T> graph;
        /**
         * The current vertex being processed in the recursive search.
         * <p>
         * Текущая вершина, обрабатываемая в рекурсивном поиске.
         */
        private T vertex;
        /**
         * A map of vertex labels to their corresponding VertexSet objects, used for memoization.
         * <p>
         * Карта меток вершин на соответствующие объекты VertexSet, используемая для мемоизации.
         */
        private Map<Integer, VertexSet<T>> vertexSet;

        /**
         * Constructor for GraphContainer when vertexSet is not yet available or needed.
         * <p>
         * Конструктор для GraphContainer, когда vertexSet еще недоступен или не нужен.
         *
         * @param allParents Map of all parents. / Карта всех родителей.
         * @param graph      The graph. / Граф.
         * @param vertex     The current vertex. / Текущая вершина.
         */
        public GraphContainer(Map<Integer, List<Integer>> allParents, Graph<T> graph, T vertex) {
            this.allParents = allParents;
            this.graph = graph;
            this.vertex = vertex;
        }
    }


    /**
     * Represents a set of matched vertices along with any labels that are in conflict.
     * This class is used during the recursive search to build up valid subgraph matches.
     * <p>
     * Представляет набор сопоставленных вершин вместе с любыми метками, которые находятся в конфликте.
     * Этот класс используется во время рекурсивного поиска для построения допустимых совпадений подграфов.
     */
    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class VertexSet<T extends LabelVertex<?>> {
        /**
         * The set of matched vertices.
         * <p>
         * Набор сопоставленных вершин.
         */
        private Set<T> vertices;
        /**
         * Labels that are currently in conflict, preventing certain merges.
         * <p>
         * Метки, которые в настоящее время находятся в конфликте, препятствуя определенным слияниям.
         */
        private Set<Integer> conflictLabels;

        /**
         * Merges this VertexSet with another, combining their vertices and conflict labels.
         * Conflicting vertices are filtered out during the merge.
         * <p>
         * Объединяет этот VertexSet с другим, комбинируя их вершины и конфликтующие метки.
         * Конфликтующие вершины отфильтровываются во время слияния.
         *
         * @param other The other VertexSet to merge with. / Другой VertexSet для объединения.
         * @return A new VertexSet representing the merged result. / Новый VertexSet, представляющий объединенный результат.
         */
        public VertexSet<T> merge(VertexSet<T> other) {
            // Union of conflict labels from both sets
            // Объединение конфликтующих меток из обоих наборов
            Set<Integer> commonConflicts = Sets.union(this.conflictLabels, other.conflictLabels);
            Set<Integer> used = new HashSet<>(); // Keep track of labels that caused conflict in merge

            // Combine vertices, filtering out those whose labels are in commonConflicts
            // Объединяем вершины, отфильтровывая те, чьи метки находятся в commonConflicts
            var vertices = Sets.union(this.vertices, other.vertices)
                    .stream()
                    .filter(vertex -> {
                        if (commonConflicts.contains(labelFromVertex(vertex))) {
                            used.add(labelFromVertex(vertex)); // Mark as used if it caused a conflict
                            return false; // Exclude conflicting vertex
                        }
                        return true; // Include non-conflicting vertex
                    })
                    .collect(Collectors.toSet());

            return new VertexSet<>(
                    vertices,
                    commonConflicts // The merged set of conflict labels
            );
        }

        /**
         * Checks if the set of vertices in this VertexSet has an empty intersection with another VertexSet.
         * <p>
         * Проверяет, имеет ли набор вершин в этом VertexSet пустое пересечение с другим VertexSet.
         *
         * @param other The other VertexSet to check intersection with. / Другой VertexSet для проверки пересечения.
         * @return True if there is no common vertex, false otherwise. / True, если нет общей вершины, иначе false.
         */
        public boolean isEmptyIntersection(VertexSet<T> other) {
            return Sets.intersection(vertices, other.vertices).isEmpty();
        }

        @Override
        public String toString() {
            return GSON.toJson(this);
        }
    }

}
