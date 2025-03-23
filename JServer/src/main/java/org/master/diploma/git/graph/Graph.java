package org.master.diploma.git.graph;

import java.util.List;

public abstract class Graph<T extends Vertex> {

    public abstract List<T> getVertices();
    public abstract T getVertex(int number);

    public abstract void addVertex(T vertex);
    public abstract void removeVertex(int number);

    public abstract void addEdge(int parent, int children);
    public abstract void removeEdge(int parent, int children);

    public abstract List<Integer> getParentNumbers(int vertexNumber);
    public abstract List<T> getParents(int vertexNumber);

    public abstract List<Integer> getChildrenNumbers(int vertexNumber);
    public abstract List<T> getChildren(int vertexNumber);
    public abstract int getRoot();
}
