package org.master.diploma.git.graph.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.subgraphmethod.DpMethodHelper;
import org.master.diploma.git.graph.label.LabelGraph;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.graph.label.SimpleLabelVertex;
import org.master.diploma.git.label.SimpleLabel;

import java.util.*;

public class FindBiggestSubSequenceSubgraphTest {


    @Test
    public void simpleEqualGraphTest() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(0, 0, 1, 1, 2, 2)
        );
        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void simpleDeleteListTest() {
        List<LabelVertex<SimpleLabel>> vertices = new ArrayList<>(List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3))
        ));

        Map<Integer, Set<Integer>> adjacentMatrix = new HashMap<>(Map.of(
                0, Set.of(1),
                1, Set.of(2)
        )
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(2);

        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(0, 0, 1, 1)
        );
        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        Assertions.assertEquals(expected, result);
    }


    @Test
    public void deleteTransientTest() {
        List<LabelVertex<SimpleLabel>> vertices = new ArrayList<>(List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3))
        ));

        Map<Integer, Set<Integer>> adjacentMatrix = new HashMap<>(Map.of(
                0, Set.of(1),
                1, Set.of(2)
        )
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(1);

        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(0, 0, 2, 2)
        );
        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void equalGraphTest() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        1, 1,
                        2, 2,
                        3, 3,
                        4, 4,
                        5, 5,
                        6, 6,
                        0, 0
                )
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void removeOneListTest() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(6);

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        1, 1,
                        2, 2,
                        3, 3,
                        4, 4,
                        5, 5,
                        0, 0
                )
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void removeSeveralListTest() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(6);
        second.removeVertex(5);

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        1, 1,
                        2, 2,
                        3, 3,
                        4, 4,
                        0, 0
                )
        );
        Assertions.assertEquals(expected, result);
    }


    @Test
    public void removeOneTransitVertexTest() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(2);

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        1, 1,
                        6, 6,
                        3, 3,
                        4, 4,
                        5, 5,
                        0, 0
                )
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void removeOneTransitVertexTest2() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(1);

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        2, 2,
                        6, 6,
                        3, 3,
                        4, 4,
                        5, 5,
                        0, 0
                )
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void removeSeveralTransitVertexTest() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(1);
        second.removeVertex(2);

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        6, 6,
                        3, 3,
                        4, 4,
                        5, 5,
                        0, 0
                )
        );
        Assertions.assertEquals(expected, result);
    }


    @Test
    public void removeRootVertexTest() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(0);

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        6, 6,
                        3, 3,
                        4, 4,
                        5, 5,
                        1, 1,
                        2, 2
                )
        );
        Assertions.assertEquals(expected, result);
    }


    @Test
    public void removeSeveralTransitVertexTest2() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(1)),
                new SimpleLabelVertex(2, new SimpleLabel(1)),
                new SimpleLabelVertex(3, new SimpleLabel(1)),
                new SimpleLabelVertex(4, new SimpleLabel(1)),
                new SimpleLabelVertex(5, new SimpleLabel(1)),
                new SimpleLabelVertex(6, new SimpleLabel(1))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(0);

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        6, 6,
                        3, 3,
                        4, 4,
                        5, 5,
                        1, 1,
                        2, 2
                )
        );
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void simpleEqualGraphTest2() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(1)),
                new SimpleLabelVertex(2, new SimpleLabel(1))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(0, 0, 1, 1, 2, 2)
        );
        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(first, second);
        Assertions.assertEquals(expected, result);
    }


    @Test
    public void removeOneTransitVertexTest3() {
        List<LabelVertex<SimpleLabel>> vertices = List.of(
                new SimpleLabelVertex(0, new SimpleLabel(1)),
                new SimpleLabelVertex(1, new SimpleLabel(2)),
                new SimpleLabelVertex(2, new SimpleLabel(3)),
                new SimpleLabelVertex(3, new SimpleLabel(4)),
                new SimpleLabelVertex(4, new SimpleLabel(5)),
                new SimpleLabelVertex(5, new SimpleLabel(6)),
                new SimpleLabelVertex(6, new SimpleLabel(7))
        );

        Map<Integer, Set<Integer>> adjacentMatrix = Map.of(
                0, Set.of(1),
                1, Set.of(2, 3),
                2, Set.of(4, 5),
                3, Set.of(6)
        );

        Graph<LabelVertex<SimpleLabel>> first = new LabelGraph<>(vertices, adjacentMatrix);
        Graph<LabelVertex<SimpleLabel>> second = ((LabelGraph) first).clone();

        second.removeVertex(2);

        DpMethodHelper.DpElement result = DpMethodHelper.findBiggestSubSequenceSubgraph(second, first);
        DpMethodHelper.DpElement expected = new DpMethodHelper.DpElement(
                Map.of(
                        1, 1,
                        6, 6,
                        3, 3,
                        4, 4,
                        5, 5,
                        0, 0
                )
        );
        Assertions.assertEquals(expected, result);
    }

    
}
