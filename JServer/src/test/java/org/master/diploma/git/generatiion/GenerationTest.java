package org.master.diploma.git.generatiion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.master.diploma.git.generator.GraphGeneratorEntity;
import org.master.diploma.git.generator.TreeGenerator;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.graph.simple.SimpleGraph;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.master.diploma.git.label.SimpleLabel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class GenerationTest {

    protected abstract SubgraphMethodExecutor getSubgraphMethodExecutor();
    int count = 1;

    @ParameterizedTest(name = "Test #{index}: {0}")
    @MethodSource("provideGraphPairs")
    void compareGraph(GraphGeneratorEntity<SimpleGraph<LabelVertex<SimpleLabel>>> graphGeneratorEntity) {

        var first = graphGeneratorEntity.getFirst();
        var second = graphGeneratorEntity.getSecond();
        var compareGraphResult = getSubgraphMethodExecutor().execute(first, second);
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
                .map(_unused -> Arguments.of(generate(500, 2, 7)))
                .toList();
    }


    private static GraphGeneratorEntity<Graph<LabelVertex<SimpleLabel>>> generate(int vertexCount, int maxLabelCount, int diffCount) {
        TreeGenerator treeGenerator = new TreeGenerator();
        return treeGenerator.generateGraphGeneratorEntity(vertexCount, maxLabelCount, diffCount);
    }
}
