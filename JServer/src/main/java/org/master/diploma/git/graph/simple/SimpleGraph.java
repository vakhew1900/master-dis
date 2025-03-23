package org.master.diploma.git.graph.simple;

import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.exception.IncorrectVertexNumberException;

import java.util.*;

public class SimpleGraph<T extends Vertex> extends Graph<T> implements Cloneable {

    private List<T> vertices;
    private Map<Integer, Integer> numberToIndex;
    private Map<Integer, Set<Integer>> adjacencyMatrix; // здесь уже не индексы вершин а номера

    public SimpleGraph(List<T> vertices, Map<Integer, Set<Integer>> adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.vertices = vertices;
        this.numberToIndex = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) {
            numberToIndex.put(vertices.get(i).getNumber(), i);
        }
    }

    @Override
    public List<T> getVertices() {
        return vertices;
    }

    @Override
    public T getVertex(int number) {
        return vertices.get(getIndexByNumber(number));
    }

    @Override
    public void addVertex(T vertex) {
        if (adjacencyMatrix.containsKey(vertex.getNumber())) {
            throw new IncorrectVertexNumberException("Vertex number should be more than adjacency matrix size()");
        }
        vertices.add(vertex);
        numberToIndex.put(vertex.getNumber(), vertices.size() - 1);
        adjacencyMatrix.put(vertex.getNumber(), new HashSet<>());
    }

    @Override
    public void removeVertex(int number) {
        if (getVertex(number) == null) {
            return;
        }
        List<Integer> children = getChildrenNumbers(number);
        List<Integer> parents = getParentNumbers(number);

        // удаляем всех детей вершины
        children.forEach(
                child -> removeEdge(number, child)
        );

        // удаляем у родителей вершины текущую вершину
        parents.forEach(
                parent -> removeEdge(parent, number)
        );

        // добавляем связь parent-child
        parents.forEach(
                parent -> {
                    children.forEach(
                            child -> {
                                addEdge(parent, child);
                            }
                    );
                }
        );

        int deleteIndex = -1;

        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).getNumber() == number) {
                deleteIndex = i;
                break;
            }
        }

        vertices.remove(deleteIndex);
        adjacencyMatrix.remove(number);
    }


    @Override
    public void addEdge(int parent, int children) {
        validateVertex(parent);
        adjacencyMatrix
                .get(parent)
                .add(children);
    }

    @Override
    public void removeEdge(int parent, int children) {
        validateVertex(parent);
        adjacencyMatrix
                .get(parent)
                .remove(children);
    }

    private void validateVertex(int number) {
        var vertex = getVertex(number);

        if (Objects.isNull(vertex)) {
            throw new IncorrectVertexNumberException("Vertex is not exist in graph");
        }

        if (!adjacencyMatrix.containsKey(number)) {
            adjacencyMatrix.put(number, new HashSet<>());
        }
    }

    @Override
    public List<Integer> getParentNumbers(int vertexNumber) {
        List<Integer> numbers = new ArrayList<>();

        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            if (adjacencyMatrix.containsKey(i) && adjacencyMatrix.get(i).contains(vertexNumber)) { // строка содержит номер нашей вершины
                numbers.add(i); // вершина с номером i является родителем
            }
        }
        return numbers;
    }

    @Override
    public List<T> getParents(int vertexNumber) {
        List<T> vertices = new ArrayList<>();

        getParentNumbers(vertexNumber).forEach(
                parent -> {
                    vertices.add(getVertex(parent));
                }
        );

        return vertices;
    }

    @Override
    public List<Integer> getChildrenNumbers(int vertexNumber) {
        validateVertex(vertexNumber);
        return new ArrayList<>(adjacencyMatrix.get(vertexNumber));
    }

    @Override
    public List<T> getChildren(int vertexNumber) {
        List<T> vertices = new ArrayList<>();
        getChildrenNumbers(vertexNumber).forEach(
                children -> {
                    vertices.add(getVertex(children));
                }
        );

        return vertices;
    }

    private int getIndexByNumber(int number) {
        return numberToIndex.get(number);
    }

    public Map<Integer, Set<Integer>> getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    @Override
    public SimpleGraph<T> clone() {
        List<T> tmpVertices = (List<T>) vertices.stream().map(T::clone).toList();
        Map<Integer, Set<Integer>> tmpAdjancyMatrix = cloneAdjancyMatrix();
        return new SimpleGraph<T>(tmpVertices, tmpAdjancyMatrix);
    }

    private Map<Integer, Set<Integer>> cloneAdjancyMatrix() {
        Map<Integer, Set<Integer>> map = new HashMap<>();

        adjacencyMatrix.forEach(
                (key, value) -> {
                    map.put(key, new HashSet<>(value));
                }
        );
        return map;
    }

    @Override
    public int getRoot() {
        for (int i = 0; i < vertices.size(); i++) {
            if (this.getParents(i).isEmpty()) {
                return i;
            }
        }

        throw new IncorrectVertexNumberException("Root number not found");
    }
}
