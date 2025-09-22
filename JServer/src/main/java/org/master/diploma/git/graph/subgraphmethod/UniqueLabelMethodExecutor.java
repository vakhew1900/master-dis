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


public class UniqueLabelMethodExecutor extends SubgraphMethodExecutor {


    public static final Gson GSON = new Gson();

    private static final int LABEL_COUNT = 1;

    @Override
    public <T extends LabelVertex<?>> GraphCompareResult execute(Graph<T> g1, Graph<T> g2) {
        checkGraph(g1);
        checkGraph(g2);

        var first = g1.clone();
        var second = g2.clone();

        Set<Integer> intersectionLabels = getIntersectionLabels(first, second);
        Consumer<Graph<T>> removeDiffLabels = (graph) -> {
            List<T> removedVertex = new ArrayList<>();
            for (T v : graph.getVertices()) {
                if (!intersectionLabels.contains(labelFromVertex(v))) {
                    removedVertex.add(v);
                }
            }

            removedVertex.forEach(
                    graph::removeVertex
            );
        };

        removeDiffLabels.accept(first);
        removeDiffLabels.accept(second);

        Map<Integer, List<Integer>> firstAllParents = getAllParents(first);
        Map<Integer, List<Integer>> secondAllParents = getAllParents(second);


        VertexSet<T> currentVertexSet = findResult(
                new GraphContainer<>(
                        firstAllParents,
                        first,
                        first.getVertex(first.getRoot()),
                        new HashMap<>()
                ),
                new GraphContainer<>(
                        secondAllParents,
                        second,
                        second.getVertex(second.getRoot())
                )
        )
                .stream()
                .max(Comparator.comparingInt(vertexSet -> vertexSet.vertices.size()))
                .get();


        Map<Integer, Integer> firstLabelToVertex = first
                .getVertices()
                .stream()
                .collect(
                        Collectors.toMap(
                                UniqueLabelMethodExecutor::labelFromVertex,
                                Vertex::getNumber
                        )
                );

        Map<Integer, Integer> secondLabelToVertex = second
                .getVertices()
                .stream()
                .collect(
                        Collectors.toMap(
                                UniqueLabelMethodExecutor::labelFromVertex,
                                Vertex::getNumber
                        )
                );


        Map<Integer, Integer> map = currentVertexSet
                .vertices
                .stream()
                        .
                collect(Collectors.toMap(
                                Vertex::getNumber,
                                vertex -> secondLabelToVertex.get(labelFromVertex(vertex))
                        )
                );


        GraphCompareResult result = GraphCompareResult
                .builder()
                .matchingVertices(map)
                .labelErrors(new HashMap<>())
                .build();
        result.fillLabelError(g1, g2);
        return result;
    }

    private <T extends LabelVertex<?>> Set<VertexSet<T>> findResult(
            GraphContainer<T> first,
            GraphContainer<T> second
    ) {

        int currentVertexNumber = labelFromVertex(first.vertex);
        int currentParentNumber = second.allParents.getOrDefault(currentVertexNumber, Collections.emptyList()).size() - 1;
        var allParents = second.allParents.get(currentVertexNumber);

        Set<Integer> conflictVertex = new HashSet<>();
        while (currentParentNumber >= 0
                && !first.vertexSet.containsKey(allParents.get(currentParentNumber))
        ) {
            conflictVertex.add(allParents.get(currentParentNumber--));
        }


        VertexSet<T> curVertexSet = VertexSet
                .<T>builder()
                .conflictLabels(conflictVertex)
                .vertices(Set.of(first.vertex))
                .build();

        if (currentParentNumber >= 0) {
            curVertexSet = curVertexSet.merge(first.vertexSet.get(allParents.get(currentParentNumber)));
        }
        Map<Integer, VertexSet<T>> curVertexSetMap = (Map<Integer, VertexSet<T>>)
                ((HashMap<Integer, VertexSet<T>>) first
                        .getVertexSet())
                        .clone();

        curVertexSetMap.put(labelFromVertex(first.vertex), curVertexSet);

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


        Set<VertexSet<T>> vertexSets = list
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());


        bruteforce(vertexSets, list, 0, curVertexSet);

        return vertexSets;
    }

    private <T extends LabelVertex<?>> void bruteforce(
            Set<VertexSet<T>> vertexSets,
            List<Set<VertexSet<T>>> list,
            int index,
            VertexSet<T> curVertexSet
    ) {

        if (index == list.size()) {
            vertexSets.add(curVertexSet);
            return;
        }

        bruteforce(vertexSets, list, index + 1, curVertexSet);


        for (var tmp : list.get(index)) {
            if (!tmp.isEmptyIntersection(curVertexSet)) {
                    bruteforce(vertexSets, list, index + 1, curVertexSet.merge(tmp));
            }
        }
    }

    private <T extends LabelVertex<?>> Map<Integer, List<Integer>> getAllParents(Graph<T> graph) {
        Map<Integer, Set<Integer>> allChildren = getAllChildren(graph);

        Map<Integer, List<Integer>> result = new LinkedHashMap<>(); // Linked нужна для сохранения порядка

        allChildren.forEach(
                (key, set) -> {
                    set.forEach(
                            value -> {
                                if (!result.containsKey(value)) {
                                    result.put(value, new ArrayList<>());
                                }
                                result.get(value).add(key);
                            }
                    );
                }
        );

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

    private <T extends LabelVertex<?>> Map<Integer, Set<Integer>> getAllChildren(Graph<T> graph) {

        Map<Integer, Set<Integer>> result = new LinkedHashMap<>();
        getAllChildren(graph.getVertex(graph.getRoot()), graph, result);

        return result;
    }

    private <T extends LabelVertex<?>> void getAllChildren(T vertex, Graph<T> graph, Map<Integer, Set<Integer>> result) {
        int actualLabel = labelFromVertex(vertex);
        if (!result.containsKey(actualLabel)) {
            result.put(actualLabel, new HashSet<>());
        }

        graph.getChildren(vertex.getNumber()).forEach(
                child -> {
                    result.get(actualLabel).add(labelFromVertex(child));
                    getAllChildren(child, graph, result); //todo если в графе могут быть циклы то добавить used
                    result.get(actualLabel).addAll(result.get(labelFromVertex(child)));
                }
        );

        return;
    }


    private <T extends LabelVertex<?>> void checkGraph(Graph<T> graph) {

        int mn = LABEL_COUNT;
        int mx = LABEL_COUNT;

        for (var vertex : graph.getVertices()) {
            mn = Math.min(mn, vertex.getLabels().size());
            mx = Math.max(mx, vertex.getLabels().size());
        }

        if (mn < LABEL_COUNT || mx > LABEL_COUNT) {
            throw new GraphException("vertex should has only one label");
        }

        Set<Integer> firstLabels = graph
                .getVertices()
                .stream()
                .map(v -> v.getLabels())
                .flatMap(list -> list.stream())
                .map(Label::getId)
                .collect(Collectors.toSet());

        if (firstLabels.size() < graph.getVertices().size()) {
            throw new GraphException("Labels should be unique");
        }
    }

    private <T extends LabelVertex<?>> Set<Integer> getExtraLabels(Graph<T> first, Graph<T> second) {
        Set<Integer> firstLabels = getLabels(first);
        Set<Integer> secondLabels = getLabels(second);

        return Sets.difference(
                Sets.union(firstLabels, secondLabels),
                Sets.intersection(firstLabels, secondLabels)
        );
    }

    private <T extends LabelVertex<?>> Set<Integer> getIntersectionLabels(Graph<T> first, Graph<T> second) {
        return Sets.intersection(
                getLabels(first),
                getLabels(second)
        );
    }

    private static <T extends LabelVertex<?>> int labelFromVertex(T vertex) {
        return vertex.getLabels().get(0).getId();
    }


    private <T extends LabelVertex<?>> Set<Integer> getLabels(Graph<T> graph) {
        return graph
                .getVertices()
                .stream()
                .map(vertex -> vertex.getLabels())
                .flatMap(List::stream)
                .map(Label::getId)
                .collect(Collectors.toSet());
    }


    @Getter
    @Setter
    @AllArgsConstructor
    private static class GraphContainer<T extends LabelVertex<?>> {
        private Map<Integer, List<Integer>> allParents;
        private Graph<T> graph;
        private T vertex;
        private Map<Integer, VertexSet<T>> vertexSet;

        public GraphContainer(Map<Integer, List<Integer>> allParents, Graph<T> graph, T vertex) {
            this.allParents = allParents;
            this.graph = graph;
            this.vertex = vertex;
        }
    }


    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class VertexSet<T extends LabelVertex<?>> {
        private Set<T> vertices;
        private Set<Integer> conflictLabels;

        public VertexSet<T> merge(VertexSet<T> other) {
                Set<Integer> commonConflicts = Sets.union(this.conflictLabels, other.conflictLabels);
                Set<Integer> used = new HashSet<>();

                var vertices = Sets.union(this.vertices, other.vertices)
                        .stream()
                        .filter(vertex ->  {
                            if (commonConflicts.contains(labelFromVertex(vertex))){
                                used.add(labelFromVertex(vertex));
                                return false;
                            }
                            return true;
                        })
                        .collect(Collectors.toSet());

                return new VertexSet<>(
                        vertices,
                        Sets.difference(commonConflicts, used)
                );
        }



        public boolean isEmptyIntersection(VertexSet<T> other) {
            return Sets.intersection(vertices, other.vertices).isEmpty();
        }

        @Override
        public String toString() {
            return GSON.toJson(this);
        }
    }

}
