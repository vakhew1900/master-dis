package org.master.diploma.git.graph.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.Vertex;

import java.util.*;

public class SimpleGraphTest {


    //--------------------------- getVertices ---------------------------

    @Nested
    public class GetVerticesTest {
        @Test
        public void typeTest() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(0),
                    new SimpleVertex(1),
                    new SimpleVertex(2)
            );

            Graph<Vertex> graph = new SimpleGraph<>(vertices, Map.of());

            Assertions.assertEquals(vertices, graph.getVertices());
        }

        @Test
        public void emptyTest() {
            List<Vertex> vertices = List.of();
            Graph<Vertex> graph = new SimpleGraph<>(vertices, Map.of());
            Assertions.assertEquals(vertices, graph.getVertices());
        }

        @Test
        public void oneVertexTest() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(0)
            );
            Graph<Vertex> graph = new SimpleGraph<>(vertices, Map.of());
            Assertions.assertEquals(vertices, graph.getVertices());
        }

    }

   @Nested
   public class GetVertexTest {
       @Test
       public void typeTest() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(0),
                   new SimpleVertex(1),
                   new SimpleVertex(2)
           );

           Graph<Vertex> graph = new SimpleGraph<>(vertices, Map.of());

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

           Graph<Vertex> graph = new SimpleGraph<>(vertices, Map.of());

           for (int i = 0; i < vertices.size(); i++) {
               Assertions.assertEquals(vertices.get(i), graph.getVertex(2 - i));
           }
       }

       @Test
       public void incorrectNumberTest() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(2),
                   new SimpleVertex(1),
                   new SimpleVertex(0)
           );

           Graph<Vertex> graph = new SimpleGraph<>(vertices, Map.of());
           Assertions.assertThrows(NullPointerException.class, () -> graph.getVertex(4));
       }
   }

   @Nested
   public class AddEdgeTest {

       @Test
       public void typeTest() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(0),
                   new SimpleVertex(1),
                   new SimpleVertex(2)
           );

           Graph<Vertex> graph = new SimpleGraph<>(vertices, new HashMap<>());

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
                   ((SimpleGraph<?>) graph).getAdjacencyMatrix()
           );
       }

       @Test
       public void addMultiEdgeTest() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(0),
                   new SimpleVertex(1),
                   new SimpleVertex(2)
           );

           Graph<Vertex> graph = new SimpleGraph<>(vertices, new HashMap<>());

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
                   ((SimpleGraph<?>) graph).getAdjacencyMatrix()
           );
       }

       @Test
       public void typeTest2() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(0),
                   new SimpleVertex(1),
                   new SimpleVertex(2)
           );

           Graph<Vertex> graph = new SimpleGraph<>(vertices, new HashMap<>());

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
                   ((SimpleGraph<?>) graph).getAdjacencyMatrix()
           );
       }
   }


   @Nested
   public class RemoveEdgeTest {
       @Test
       public void typeTest() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(0),
                   new SimpleVertex(1),
                   new SimpleVertex(2)
           );

           Graph<Vertex> graph = new SimpleGraph<>(
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
                   ((SimpleGraph<?>) graph).getAdjacencyMatrix()
           );
       }

       @Test
       public void removeNotExistingEdgeTest() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(0),
                   new SimpleVertex(1),
                   new SimpleVertex(2)
           );

           Graph<Vertex> graph = new SimpleGraph<>(
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
                   ((SimpleGraph<?>) graph).getAdjacencyMatrix()
           );
       }
   }



   @Nested
   public class GetParentTest {

       @Test
       public void typeTest() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(0),
                   new SimpleVertex(1),
                   new SimpleVertex(2)
           );

              Graph<Vertex> graph = new SimpleGraph<>(vertices, new HashMap<>());

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
       public void typeTest2() {
           List<Vertex> vertices = List.of(
                   new SimpleVertex(2),
                   new SimpleVertex(1),
                   new SimpleVertex(0)
           );

              Graph<Vertex> graph = new SimpleGraph<>(vertices, new HashMap<>());

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

   }

    @Nested
    public class GetChildrenTest {

        @Test
        public void typeTest() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(0),
                    new SimpleVertex(1),
                    new SimpleVertex(2),
                    new SimpleVertex(3)
            );

               Graph<Vertex> graph = new SimpleGraph<>(vertices, new HashMap<>());

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
        public void typeTest2() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(3),
                    new SimpleVertex(2),
                    new SimpleVertex(1),
                    new SimpleVertex(0)
            );

               Graph<Vertex> graph = new SimpleGraph<>(vertices, new HashMap<>());

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
    }



    @Nested
    public class AddVertexTest {

        @Test
        public void typeTest() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(0),
                    new SimpleVertex(1),
                    new SimpleVertex(2)
            );

           Graph<Vertex> graph = new SimpleGraph<>(new ArrayList<>(vertices), new HashMap<>());
            Vertex vertex = new SimpleVertex(3);
            graph.addVertex(vertex);
            Assertions.assertEquals(vertex, graph.getVertex(3));
        }

    }

    @Nested
    public class RemoveVertexTest {
        @Test
        public void vertexWithoutParentAndChildTest() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(0),
                    new SimpleVertex(1),
                    new SimpleVertex(2),
                    new SimpleVertex(3)
            );

            Graph<Vertex> graph = new SimpleGraph<>(new ArrayList<>(vertices), new HashMap<>());
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
            Assertions.assertEquals(expectedAdjancyMap, ((SimpleGraph<?>) graph).getAdjacencyMatrix());
        }


        @Test
        public void vertexIsRootTest() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(0),
                    new SimpleVertex(1),
                    new SimpleVertex(2),
                    new SimpleVertex(3)
            );

           Graph<Vertex> graph = new SimpleGraph<>(new ArrayList<>(vertices), new HashMap<>());
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
            Assertions.assertEquals(expectedAdjancyMap, ((SimpleGraph<?>) graph).getAdjacencyMatrix());
        }


        @Test
        public void vertexHasNoChildrenTest() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(0),
                    new SimpleVertex(1),
                    new SimpleVertex(2),
                    new SimpleVertex(3)
            );

           Graph<Vertex> graph = new SimpleGraph<>(new ArrayList<>(vertices), new HashMap<>());
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
            Assertions.assertEquals(expectedAdjancyMap, ((SimpleGraph<?>) graph).getAdjacencyMatrix());
        }

        @Test
        public void typeTest() {
            List<Vertex> vertices = List.of(
                    new SimpleVertex(0),
                    new SimpleVertex(1),
                    new SimpleVertex(2),
                    new SimpleVertex(3),
                    new SimpleVertex(4),
                    new SimpleVertex(5)
            );

           Graph<Vertex> graph = new SimpleGraph<>(new ArrayList<>(vertices), new HashMap<>());
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
            Assertions.assertEquals(expectedAdjancyMap, ((SimpleGraph<?>) graph).getAdjacencyMatrix());
        }
    }


    @Nested
    public class GetTransientClosure {

        @Test
        public void simpleTest(){
            List<Vertex> vertices = List.of(
                    new SimpleVertex(1),
                    new SimpleVertex(2),
                    new SimpleVertex(3)
            );

            Map<Integer, Set<Integer>> adjancyMap = Map.of(
                    1, Set.of(2),
                    2, Set.of(3)
            );

            Graph<Vertex> graph = new SimpleGraph<>(vertices, adjancyMap);
            Set<Map.Entry<Integer, Integer>> expectedTransienClosure = Set.of(
                    new AbstractMap.SimpleEntry<>(1, 2),
                    new AbstractMap.SimpleEntry<>(1, 3),
                    new AbstractMap.SimpleEntry<>(2, 3)
            );

            Assertions.assertEquals(expectedTransienClosure, graph.getTransitiveClosure());
        }

        @Test
        public void graphIsTreeTest(){
            List<Vertex> vertices = List.of(
                    new SimpleVertex(1),
                    new SimpleVertex(2),
                    new SimpleVertex(3),
                    new SimpleVertex(4),
                    new SimpleVertex(5),
                    new SimpleVertex(6),
                    new SimpleVertex(7),
                    new SimpleVertex(8)
            );

            Map<Integer, Set<Integer>> adjancyMap = Map.of(
                    1, Set.of(2, 3),
                    2, Set.of(4, 5),
                    3, Set.of(6, 7),
                    4, Set.of(8)

            );

            Graph<Vertex> graph = new SimpleGraph<>(vertices, adjancyMap);
            Set<Map.Entry<Integer, Integer>> expectedTransienClosure = Set.of(
                    new AbstractMap.SimpleEntry<>(1, 2),
                    new AbstractMap.SimpleEntry<>(1, 3),
                    new AbstractMap.SimpleEntry<>(1, 4),
                    new AbstractMap.SimpleEntry<>(1, 5),
                    new AbstractMap.SimpleEntry<>(1, 6),
                    new AbstractMap.SimpleEntry<>(1, 7),
                    new AbstractMap.SimpleEntry<>(1, 8),
                    new AbstractMap.SimpleEntry<>(2, 4),
                    new AbstractMap.SimpleEntry<>(2, 5),
                    new AbstractMap.SimpleEntry<>(2, 8),
                    new AbstractMap.SimpleEntry<>(3, 6),
                    new AbstractMap.SimpleEntry<>(3, 7),
                    new AbstractMap.SimpleEntry<>(4, 8)
            );

            Assertions.assertEquals(expectedTransienClosure, graph.getTransitiveClosure());
        }
    }

}
