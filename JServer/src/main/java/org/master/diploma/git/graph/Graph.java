package org.master.diploma.git.graph;

import java.util.List;

public abstract class Graph {

    public abstract List<Vertex> getVertices();
    public abstract Vertex getVertex(int number);
    public abstract void removeVertex(int number);

    public abstract List<Integer> getParentNumbers();
    public abstract List<Vertex> getParents();

    public abstract List<Integer> getChildrenNumbers();
    public abstract List<Vertex> getChildren();
}
