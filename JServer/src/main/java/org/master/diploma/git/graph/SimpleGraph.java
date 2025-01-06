package org.master.diploma.git.graph;

import java.util.*;

public class SimpleGraph extends Graph {

    private List<Vertex> vertices;
    private Map<Integer, Integer> numberToIndex;
    private List<Set<Integer>> adjacencyMatrix; // здесь уже не индексы вершин а номера

    public SimpleGraph(List<Vertex> vertices, List<Set<Integer>> adjacencyMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.vertices = vertices;
        this.numberToIndex = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) {
            numberToIndex.put(vertices.get(i).getNumber(), i);
        }
    }

    @Override
    public List<Vertex> getVertices() {
        return vertices;
    }

    @Override
    public Vertex getVertex(int number) {
        return vertices.get(getIndexByNumber(number));
    }

    @Override
    public void removeVertex(int number) {
    }

    @Override
    public List<Integer> getParentNumbers(int vertexNumber) {
        List<Integer> numbers = new ArrayList<>();

        for (int i = 0; i < adjacencyMatrix.size(); i++) {
            if (adjacencyMatrix.get(i).contains(vertexNumber)) { // строка содержит номер нашей вершины
                numbers.add(i); // вершина с номером i является родителем
            }
        }
        return numbers;
    }

    @Override
    public List<Vertex> getParents(int vertexNumber) {
        List<Vertex> vertices = new ArrayList<>();

        getParentNumbers(vertexNumber).forEach(
                parent -> {
                    vertices.add(getVertex(parent));
                }
        );

        return vertices;
    }

    @Override
    public List<Integer> getChildrenNumbers(int vertexNumber) {
        return new ArrayList<>(adjacencyMatrix.get(vertexNumber));
    }

    @Override
    public List<Vertex> getChildren(int vertexNumber) {
        List<Vertex> vertices = new ArrayList<>();
        getParentNumbers(vertexNumber).forEach(
                children -> {
                    vertices.add(getVertex(children));
                }
        );

        return vertices;
    }

    private int getIndexByNumber(int number) {
        return numberToIndex.get(number);
    }
}
