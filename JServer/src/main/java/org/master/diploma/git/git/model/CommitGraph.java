package org.master.diploma.git.git.model;

import org.master.diploma.git.graph.label.LabelGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommitGraph extends LabelGraph<Commit> {

    public CommitGraph(List<Commit> commits, Map<Integer, Set<Integer>> adjacencyMatrix) {
        super(commits, adjacencyMatrix);
    }
}
