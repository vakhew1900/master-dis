package org.git_tutor.git_logic.generation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.git_tutor.git_logic.git.generator.GraphGeneratorEntity;
import org.git_tutor.git_logic.git.generator.TreeGenerator;
import org.git_tutor.git_logic.git.graph.Graph;
import org.git_tutor.git_logic.git.graph.label.LabelVertex;
import org.git_tutor.git_logic.git.graph.simple.SimpleGraph;
import org.git_tutor.git_logic.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.git_tutor.git_logic.git.label.SimpleLabel;
import org.git_tutor.git_logic.git.metrics.Metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class GenerationTest {

    protected abstract SubgraphMethodExecutor getSubgraphMethodExecutor();
    private Metrics metrics = new Metrics(0, 0, 0, 0);


    @AfterAll
    public void afterAll(){
        System.out.println(metrics.toCompactTable());
    }

    @ParameterizedTest(name = "Test #{index}: {0}")
    @MethodSource("provideGraphPairs")
    void compareGraph(GraphGeneratorEntity<SimpleGraph<LabelVertex<SimpleLabel>>> graphGeneratorEntity) {

        SimpleGraph<LabelVertex<SimpleLabel>> first = graphGeneratorEntity.getFirst();
        SimpleGraph<LabelVertex<SimpleLabel>> second = graphGeneratorEntity.getSecond();
        var compareGraphResult = getSubgraphMethodExecutor().execute(first, second);

        metrics.add(Metrics.findMetric(first, second, graphGeneratorEntity.getGraphCompareResult(), compareGraphResult));
        Assertions.assertEquals(graphGeneratorEntity.getGraphCompareResult(), compareGraphResult);
    }

    public static Stream<Arguments> provideGraphPairs() {
        List<Arguments> arguments = new ArrayList<>();

        arguments.addAll(simpleGraphTests());
        //  arguments.addAll(bigGraphTests());


        return arguments.stream();
    }

    private static Collection<? extends Arguments> bigGraphTests() {
        return  List.of(Arguments.of());
    }

    private static Collection<? extends Arguments> simpleGraphTests() {
        final  int count = 100;
        return IntStream
                .range(0, count)
                .boxed()
                .map(_unused -> Arguments.of(generate(20, 2, 4)))
                .toList();
    }


    private static GraphGeneratorEntity<Graph<LabelVertex<SimpleLabel>>> generate(int vertexCount, int maxLabelCount, int diffCount) {
        TreeGenerator treeGenerator = new TreeGenerator();
        return treeGenerator.generateGraphGeneratorEntity(vertexCount, maxLabelCount, diffCount);
    }
}
