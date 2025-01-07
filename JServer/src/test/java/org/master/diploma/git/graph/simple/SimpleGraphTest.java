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
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(vertices, new HashMap<>());

        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);


        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                0, Set.of(1, 2),
                1, Set.of(2)
                //  2, new HashSet<>()
        );

        Assertions.assertEquals(
                expectedAdjancyMap,
                ((SimpleGraph) graph).getAdjacencyMatrix()
        );
    }

    @Test
    public void addMultiEdgeTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(vertices, new HashMap<>());

        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);


        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                0, Set.of(1, 2),
                1, Set.of(2)
                //  2, new HashSet<>()
        );

        Assertions.assertEquals(
                expectedAdjancyMap,
                ((SimpleGraph) graph).getAdjacencyMatrix()
        );
    }

    @Test
    public void addEdgeTypeTestц() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(vertices, new HashMap<>());

        graph.addEdge(2, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);


        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                0, Set.of(2),
                1, Set.of(2),
                2, Set.of(1)
        );

        Assertions.assertEquals(
                expectedAdjancyMap,
                ((SimpleGraph) graph).getAdjacencyMatrix()
        );
    }


    //------------------- removeEdge -------------------------------

    @Test
    public void removeEdgeTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(
                vertices,
                new HashMap<>()
        );

        graph.addEdge(2, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);

        graph.removeEdge(1, 2);
        graph.removeEdge(2, 1);

        graph.removeEdge(1, 0);

        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                0, Set.of(2),
                1, Set.of(),
                2, Set.of()
        );

        Assertions.assertEquals(
                expectedAdjancyMap,
                ((SimpleGraph) graph).getAdjacencyMatrix()
        );
    }

    @Test
    public void removeNotExistingEdgeTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(
                vertices,
                new HashMap<>()
        );

        graph.addEdge(2, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);


        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                0, Set.of(2),
                1, Set.of(2),
                2, Set.of(1)
        );

        graph.removeEdge(2, 3);
        Assertions.assertThrows(NullPointerException.class, () -> graph.removeEdge(3, 2));
        Assertions.assertEquals(
                expectedAdjancyMap,
                ((SimpleGraph) graph).getAdjacencyMatrix()
        );
    }


    //-------------------------- getParents -------------------------------------

    @Test
    public void getParentsTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(vertices, new HashMap<>());

        graph.addEdge(2, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);

        Assertions.assertEquals(List.of(0, 1), graph.getParentNumbers(2));
        Assertions.assertEquals(List.of(2), graph.getParentNumbers(1));
        Assertions.assertEquals(List.of(), graph.getParentNumbers(0));

        Assertions.assertEquals(
                List.of(vertices.get(0), vertices.get(1)),
                graph.getParents(2)
        );

        Assertions.assertEquals(
                List.of(vertices.get(2)),
                graph.getParents(1)
        );

        Assertions.assertEquals(
                List.of(),
                graph.getParents(0)
        );
    }

    @Test
    public void getParentsTest2() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(2),
                new SimpleVertex(1),
                new SimpleVertex(0)
        );

        Graph graph = new SimpleGraph(vertices, new HashMap<>());

        graph.addEdge(2, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);

        Assertions.assertEquals(List.of(0, 1), graph.getParentNumbers(2));
        Assertions.assertEquals(List.of(2), graph.getParentNumbers(1));
        Assertions.assertEquals(List.of(), graph.getParentNumbers(0));

        Assertions.assertEquals(
                List.of(vertices.get(2), vertices.get(1)),
                graph.getParents(2)
        );

        Assertions.assertEquals(
                List.of(vertices.get(0)),
                graph.getParents(1)
        );

        Assertions.assertEquals(
                List.of(),
                graph.getParents(0)
        );
    }


    //----------------------- getChildren ------------------------------

    @Test
    public void getChildrenTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2),
                new SimpleVertex(3)
        );

        Graph graph = new SimpleGraph(vertices, new HashMap<>());

        graph.addEdge(2, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);
        graph.addEdge(0, 1);

        Assertions.assertEquals(List.of(1, 2), graph.getChildrenNumbers(0));
        Assertions.assertEquals(List.of(2), graph.getChildrenNumbers(1));
        Assertions.assertEquals(List.of(1), graph.getChildrenNumbers(2));
        Assertions.assertEquals(List.of(), graph.getChildrenNumbers(3));

        Assertions.assertEquals(
                List.of(vertices.get(1), vertices.get(2)),
                graph.getChildren(0)
        );

        Assertions.assertEquals(
                List.of(vertices.get(2)),
                graph.getChildren(1)
        );

        Assertions.assertEquals(
                List.of(vertices.get(1)),
                graph.getChildren(2)
        );

        Assertions.assertEquals(
                List.of(),
                graph.getChildren(3)
        );
    }

    @Test
    public void getChildrenTest2() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(3),
                new SimpleVertex(2),
                new SimpleVertex(1),
                new SimpleVertex(0)
        );

        Graph graph = new SimpleGraph(vertices, new HashMap<>());

        graph.addEdge(2, 1);
        graph.addEdge(1, 2);
        graph.addEdge(0, 2);
        graph.addEdge(0, 1);

        Assertions.assertEquals(List.of(1, 2), graph.getChildrenNumbers(0));
        Assertions.assertEquals(List.of(2), graph.getChildrenNumbers(1));
        Assertions.assertEquals(List.of(1), graph.getChildrenNumbers(2));
        Assertions.assertEquals(List.of(), graph.getChildrenNumbers(3));

        Assertions.assertEquals(
                List.of(vertices.get(2), vertices.get(1)),
                graph.getChildren(0)
        );

        Assertions.assertEquals(
                List.of(vertices.get(1)),
                graph.getChildren(1)
        );

        Assertions.assertEquals(
                List.of(vertices.get(2)),
                graph.getChildren(2)
        );

        Assertions.assertEquals(
                List.of(),
                graph.getChildren(3)
        );
    }


    // ----------------------- addVertex -------------------------------

    @Test
    public void addVertexTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2)
        );

        Graph graph = new SimpleGraph(new ArrayList<>(vertices), new HashMap<>());
        Vertex vertex = new SimpleVertex(3);
        graph.addVertex(vertex);
        Assertions.assertEquals(vertex, graph.getVertex(3));
    }

    // ----------------------- removeVertex -------------------------------

    @Test
    public void removeVertexWithoutParentAndChildTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2),
                new SimpleVertex(3)
        );

        Graph graph = new SimpleGraph(new ArrayList<>(vertices), new HashMap<>());
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);

        graph.removeVertex(3);

        List<Vertex> expectedVertices = List.of(
                vertices.get(0),
                vertices.get(1),
                vertices.get(2)
        );

        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                0, Set.of(1, 2),
                1, Set.of(2)
        );

        Assertions.assertEquals(expectedVertices, graph.getVertices());
        Assertions.assertEquals(expectedAdjancyMap, ((SimpleGraph) graph).getAdjacencyMatrix());
    }


    @Test
    public void removeVertexIsRootTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2),
                new SimpleVertex(3)
        );

        Graph graph = new SimpleGraph(new ArrayList<>(vertices), new HashMap<>());
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        graph.removeVertex(0);

        List<Vertex> expectedVertices = List.of(
                vertices.get(1),
                vertices.get(2),
                vertices.get(3)
        );

        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                1, Set.of(2),
                2, Set.of(3)
        );

        Assertions.assertEquals(expectedVertices, graph.getVertices());
        Assertions.assertEquals(expectedAdjancyMap, ((SimpleGraph) graph).getAdjacencyMatrix());
    }


    @Test
    public void removeVertexHasNoChildrenTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2),
                new SimpleVertex(3)
        );

        Graph graph = new SimpleGraph(new ArrayList<>(vertices), new HashMap<>());
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);

        graph.removeVertex(3);

        List<Vertex> expectedVertices = List.of(
                vertices.get(0),
                vertices.get(1),
                vertices.get(2)
        );

        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                0, Set.of(1, 2),
                1, Set.of(2),
                2, Set.of()
        );

        Assertions.assertEquals(expectedVertices, graph.getVertices());
        Assertions.assertEquals(expectedAdjancyMap, ((SimpleGraph) graph).getAdjacencyMatrix());
    }

    @Test
    public void removeVertexTest() {
        List<Vertex> vertices = List.of(
                new SimpleVertex(0),
                new SimpleVertex(1),
                new SimpleVertex(2),
                new SimpleVertex(3),
                new SimpleVertex(4),
                new SimpleVertex(5)
        );

        Graph graph = new SimpleGraph(new ArrayList<>(vertices), new HashMap<>());
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);

        graph.removeVertex(3);

        List<Vertex> expectedVertices = List.of(
                vertices.get(0),
                vertices.get(1),
                vertices.get(2),
                vertices.get(4),
                vertices.get(5)
        );

        Map<Integer, Set<Integer>> expectedAdjancyMap = Map.of(
                0, Set.of(1, 2),
                1, Set.of(4, 5),
                2, Set.of(4, 5)
        );

        Assertions.assertEquals(expectedVertices, graph.getVertices());
        Assertions.assertEquals(expectedAdjancyMap, ((SimpleGraph) graph).getAdjacencyMatrix());
    }
}
