package org.master.diploma.git.git.model;

import org.master.diploma.git.git.exception.IncorrectCommitGraphVertexTypeException;
import org.master.diploma.git.graph.simple.SimpleGraph;
import org.master.diploma.git.graph.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommitGraph extends SimpleGraph {


    public CommitGraph(List<Vertex> vertices, Map<Integer, Set<Integer>> adjacencyMatrix) {
        super(vertices, adjacencyMatrix);
        vertices.forEach(
                vertex -> {
                    if (!(vertex instanceof Commit)) {
                        throw new IncorrectCommitGraphVertexTypeException();
                    }
                }
        );
    }
}
