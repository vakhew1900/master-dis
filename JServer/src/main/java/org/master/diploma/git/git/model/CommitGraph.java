package org.master.diploma.git.git.model;

import org.master.diploma.git.git.exception.IncorrectCommitGraphVertexTypeException;
import org.master.diploma.git.graph.simple.SimpleGraph;
import org.master.diploma.git.graph.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CommitGraph extends SimpleGraph {


    public CommitGraph(List<Commit> commits, Map<Integer, Set<Integer>> adjacencyMatrix) {
        super(
                commits.stream()
                        .map(commit -> (Vertex) commit)  // Явное преобразование
                        .collect(Collectors.toList()),
                adjacencyMatrix
        );

    }
}
