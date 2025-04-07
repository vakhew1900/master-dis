package org.master.diploma.git.graph.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindBiggestSubSequenceSubgraphTest {

    @Test
    public void equalGraphTest(){
        List<SimpleVertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2),
                new SimpleVertex(3),
                new SimpleVertex(4),
                new SimpleVertex(5),
                new SimpleVertex(6)
        );

        Map<Integer, Set<Integer>>  adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<SimpleVertex> first = new SimpleGraph<>(vertices, adjacentMatrix);
        Graph<SimpleVertex> second = ((SimpleGraph) first).clone();

        int expected = 7;
        int result = GraphHelper.findBiggestSubSequenceSubgraph(first, second);
        Assertions.assertEquals(expected, result);
    }
}
