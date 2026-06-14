package org.git_tutor.git_logic.model;

import org.git_tutor.git_logic.graph.label.LabelGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommitGraph extends LabelGraph<Commit> {

    public CommitGraph(List<Commit> commits, Map<Integer, Set<Integer>> adjacencyMatrix) {
        super(commits, adjacencyMatrix);
    }
}
