package org.master.diploma.git.graph.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.Vertex;

import java.util.*;

public class SimpleGraphTest {


    //--------------------------- getVertices ---------------------------

    @Test
    public void getVerticesTypeTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(vertices, Map.of());

        Assertions.assertEquals(vertices, graph.getVertices());
    }

    @Test
    public void getVerticesEmptyTest() {
        List<Vertex> vertices = List.of();
        Graph graph = new SimpleGraph(vertices, Map.of());
        Assertions.assertEquals(vertices, graph.getVertices());
    }

    @Test
    public void getVerticesOneVertexTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0)
        );
        Graph graph = new SimpleGraph(vertices, Map.of());
        Assertions.assertEquals(vertices, graph.getVertices());
    }

    //------------------------- getVertex ---------------------------

    @Test
    public void getVertexTypeTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(vertices, Map.of());

        for (int i = 0; i < vertices.size(); i++) {
            Assertions.assertEquals(vertices.get(i), graph.getVertex(i));
        }
    }

    @Test
    public void getVertexTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(2),
                new SimpleVertex(1),
                new SimpleVertex(0)
        );

        Graph graph = new SimpleGraph(vertices, Map.of());

        for (int i = 0; i < vertices.size(); i++) {
            Assertions.assertEquals(vertices.get(i), graph.getVertex(2 - i));
        }
    }

    @Test
    public void getVertexIncorrectNumber() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(2),
                new SimpleVertex(1),
                new SimpleVertex(0)
        );

        Graph graph = new SimpleGraph(vertices, Map.of());
        Assertions.assertThrows(NullPointerException.class, () -> graph.getVertex(4));
    }

    //-------------------------------- addEdge -------------------------------------------

    @Test
    public void addEdgeTypeTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(2),
                new SimpleVertex(1),
                new SimpleVertex(0)
        );

        Graph graph = new SimpleGraph(vertices, new HashMap<>());

        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);


        Map<Integer, Set<Integer>> expectedAdjancyMap =    Map.of(
                0, Set.of(1, 2),
                1, Set.of(2)
              //  2, new HashSet<>()
        );

        Assertions.assertEquals(
                expectedAdjancyMap,
                ((SimpleGraph) graph).getAdjacencyMatrix()
        );
    }
}
