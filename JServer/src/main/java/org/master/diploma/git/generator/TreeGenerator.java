package org.master.diploma.git.generator;

import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.label.LabelGraph;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.graph.label.SimpleLabelVertex;
import org.master.diploma.git.label.SimpleLabel;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeGenerator {

    public static final int MAX_LABEL_NUMBER = 1 << 16;

    protected int maxParentCount() {
        return 1;
    }

    public GraphGeneratorEntity<Graph<LabelVertex<SimpleLabel>>> generateGraphGeneratorEntity(int vertexCount, int maxLabelCount, int diffCount) {
        Graph<LabelVertex<SimpleLabel>> first = generateGraph(vertexCount, maxLabelCount);
        Graph<LabelVertex<SimpleLabel>> second = first.clone();
        GraphCompareResult graphCompareResult = createCompareResult(vertexCount);
        var graphGeneratorEntity = new GraphGeneratorEntity<>(first, second, graphCompareResult);
        makeDiffs(graphGeneratorEntity, diffCount);
        return graphGeneratorEntity;
    }

    private void makeDiffs(
            GraphGeneratorEntity<Graph<LabelVertex<SimpleLabel>>> graphGeneratorEntity,
            int diffCount
    ) {

        while (diffCount > 0) {
            diffCount--;
            DiffGenerator.diff(graphGeneratorEntity);
        }
    }


    public Graph<LabelVertex<SimpleLabel>> generateGraph(int vertexCount, int maxLabelCount) {
        int vertexNum = 1;

        Graph<LabelVertex<SimpleLabel>> graph = new LabelGraph<>();
        while (vertexNum <= vertexCount) {

            LabelVertex<SimpleLabel> labelVertex = generateVertex(vertexNum, maxLabelCount);
            boolean isEmptyGraph = graph.getVertices().isEmpty();
            graph.addVertex(labelVertex);

            if (!isEmptyGraph) {
                int parentCount = RandomUtils.nextInt(1, maxParentCount());
                Set<Integer> parents = RandomUtils.ints(
                        1,
                        vertexNum - 1,
                        parentCount
                );

                parents.forEach(
                        parent -> {
                            graph.addEdge(parent, labelVertex.getNumber());
                        }
                );
            }
            vertexNum++;
        }

        return graph;
    }

    public static LabelVertex<SimpleLabel> generateVertex(int vertexNum, int maxLabelCount) {
        LabelVertex<SimpleLabel> labelVertex = new SimpleLabelVertex(vertexNum);
        int labelCount = RandomUtils.nextInt(1, maxLabelCount);
        while (labelCount > 0) {
            int curLabel = RandomUtils.nextInt(1, MAX_LABEL_NUMBER + 1);
            labelVertex.addLabel(new SimpleLabel(curLabel));
            labelCount--;
        }
        return labelVertex;
    }


    protected GraphCompareResult createCompareResult(int vertexCount) {
        Map<Integer, Integer> matchingVertices = IntStream
                .range(1, vertexCount + 1)
                .boxed()
                .collect(Collectors.toMap(
                            x -> x,
                            x -> x
                        )
                );

        Map<Integer, GraphCompareResult.LabelError> labelErrors = IntStream
                .range(1, vertexCount + 1)
                .boxed()
                .collect(Collectors.toMap(
                        x -> x,
                        x -> new GraphCompareResult.LabelError()
                )
        );

        return new GraphCompareResult(false, matchingVertices, labelErrors);
    }
}
