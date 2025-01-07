package org.master.diploma.git.graph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.graph.simple.SimpleGraph;
import org.master.diploma.git.graph.simple.SimpleVertex;

import java.util.List;

public class SimpleGraphTest {


    //--------------------------- getVertices ---------------------------

    @Test
    public void getVerticesTypeTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(vertices, List.of());

        Assertions.assertEquals(vertices, graph.getVertices());
    }

    @Test
    public void getVerticesEmptyTest() {
        List<Vertex> vertices = List.of();
        Graph graph = new SimpleGraph(vertices, List.of());
        Assertions.assertEquals(vertices, graph.getVertices());
    }

    @Test
    public void getVerticesOneVertexTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0)
        );
        Graph graph = new SimpleGraph(vertices, List.of());
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

        Graph graph = new SimpleGraph(vertices, List.of());

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

        Graph graph = new SimpleGraph(vertices, List.of());

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

        Graph graph = new SimpleGraph(vertices, List.of());
        Assertions.assertThrows(NullPointerException.class, ()-> graph.getVertex(4));
    }
}
