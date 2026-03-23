package org.master.diploma.git.graph.dto.two_graph;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.converter.GitGraphConverter;
import org.master.diploma.git.graph.dto.samples.DiffDto;
import org.master.diploma.git.graph.dto.samples.GitGraphDto;
import org.master.diploma.git.graph.dto.samples.LinkDto;
import org.master.diploma.git.graph.dto.samples.NodeDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base abstract class for converters used in the two-graph side-by-side view.
 */
public abstract class TwoGraphConverter extends GitGraphConverter {

    protected TwoGraphConverter(GraphCompareResult result) {
        super(result);
    }

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
