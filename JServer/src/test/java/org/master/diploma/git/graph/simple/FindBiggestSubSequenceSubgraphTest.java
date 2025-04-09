package org.master.diploma.git.graph.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphHelper;
import org.master.diploma.git.graph.label.LabelGraph;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.graph.label.SimpleLabelVertex;
import org.master.diploma.git.label.SimpleLabel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FindBiggestSubSequenceSubgraphTest {


    @Test
    public void simpleTest(){
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3))
        );

        Map<Integer, Set<Integer>>  adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        GraphHelper.DpElement expected = new GraphHelper.DpElement(
                3,
                Map.of(0,0,1,1,2,2)
        );
        GraphHelper.DpElement result = GraphHelper.findBiggestSubSequenceSubgraph(first, second);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void equalGraphTest(){
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>>  adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        int expected = 7;
        GraphHelper.DpElement result = GraphHelper.findBiggestSubSequenceSubgraph(first, second);
        Assertions.assertEquals(expected, result);
    }
}
