package org.master.diploma.git.graph.dto.converter;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.GitGraphDto;
import org.master.diploma.git.graph.dto.LinkDto;
import org.master.diploma.git.graph.dto.NodeDto;
import org.master.diploma.git.graph.dto.DiffDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract converter to transform CommitGraph into GitGraphDto.
 * Subclasses define specific logic for reference or student graphs.
 */
public abstract class GitGraphConverter {

    protected final GraphCompareResult result;

    protected GitGraphConverter(GraphCompareResult result) {
        this.result = result;
    }

    /**
     * Template method that performs the conversion.
     */
    public GitGraphDto convert(CommitGraph commitGraph) {
        List<NodeDto> nodes = commitGraph.getVertices().stream()
                .map(vertex -> {
                    Commit commit = vertex.asCommit();
                    String severity = getSeverity(commit.getNumber());
                    List<DiffDto> diffs = buildDiffs(commit);
                    return NodeDto.from(commit, severity, diffs);
                })
                .collect(Collectors.toList());

        List<LinkDto> links = new ArrayList<>();
        Map<Integer, Set<Integer>> adjMatrix = commitGraph.getAdjacencyMatrix();

        for (Map.Entry<Integer, Set<Integer>> entry : adjMatrix.entrySet()) {
            Commit sourceCommit = commitGraph.getVertex(entry.getKey());
            if (sourceCommit == null) continue;

            for (Integer targetNumber : entry.getValue()) {
                Commit targetCommit = commitGraph.getVertex(targetNumber);
                if (targetCommit != null) {
                    links.add(new LinkDto(sourceCommit.getHash(), targetCommit.getHash()));
                }
            }
        }

        return new GitGraphDto(nodes, links);
    }

    protected abstract String getSeverity(int vertexNumber);

    protected abstract List<DiffDto> buildDiffs(Commit commit);
}
