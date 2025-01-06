package org.master.diploma.git.graph;

import java.util.List;

public abstract class Graph {

    public abstract List<Vertex> getVertices();
    public abstract Vertex getVertex(int number);
    public abstract void removeVertex(int number);

    public abstract List<Integer> getParentNumbers(int vertexNumber);
    public abstract List<Vertex> getParents(int vertexNumber);

    public abstract List<Integer> getChildrenNumbers(int vertexNumber);
    public abstract List<Vertex> getChildren(int vertexNumber);
}
