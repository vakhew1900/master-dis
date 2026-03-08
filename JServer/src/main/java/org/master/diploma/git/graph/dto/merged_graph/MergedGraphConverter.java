package org.master.diploma.git.graph.dto.merged_graph;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.dto.converter.GitGraphConverter;
import org.master.diploma.git.graph.dto.samples.DiffDto;
import org.master.diploma.git.graph.dto.samples.GitGraphDto;
import org.master.diploma.git.graph.dto.samples.LinkDto;
import org.master.diploma.git.graph.dto.samples.NodeDto;
import org.master.diploma.git.label.GitLabel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Converter that merges two Git graphs into a single representation.
 */
public class MergedGraphConverter extends GitGraphConverter {

    private final Map<Integer, Integer> g1ToG2;
    private final Map<Integer, Integer> g2ToG1;
    private final  CommitGraph referenceGraph;

    public MergedGraphConverter(GraphCompareResult result, CommitGraph referenceGraph) {
        super(result);
        this.referenceGraph = referenceGraph;
        this.g1ToG2 = result.getMatchingVertices();
        this.g2ToG1 = result.getMatchingVertices().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (a, b) -> a));
    }


    @Override
    public GitGraphDto convert(CommitGraph studentGraph) {

        List<NodeDto> nodes = new ArrayList<>();
        Set<Integer> processedG2Nodes = new HashSet<>();

        for (Commit studentCommit : studentGraph.getVertices().stream().map(Vertex::asCommit).toList()) {
            Integer g2Number = g1ToG2.get(studentCommit.getNumber());
            if (g2Number != null) {
                Commit referenceCommit = referenceGraph.getVertex(g2Number);
                nodes.add(createMergedNode(studentCommit, referenceCommit));
                processedG2Nodes.add(g2Number);
            } else {
                nodes.add(createStudentOnlyNode(studentCommit));
            }
        }

        for (Commit referenceCommit : referenceGraph.getVertices().stream().map(Vertex::asCommit).toList()) {
            if (!processedG2Nodes.contains(referenceCommit.getNumber())) {
                nodes.add(createReferenceOnlyNode(referenceCommit));
            }
        }

        List<LinkDto> links = buildMergedLinks(studentGraph, referenceGraph);
        return new GitGraphDto(nodes, links);
    }

    private NodeDto createMergedNode(Commit student, Commit reference) {
        GraphCompareResult.LabelError error = result.getLabelErrors().get(student.getNumber());
        Set<Integer> extraLabels = error != null ? new HashSet<>(error.getExtraLabels()) : Collections.emptySet();
        Set<Integer> missingLabels = error != null ? new HashSet<>(error.getMissingLabels()) : Collections.emptySet();

        List<DiffDto> diffs = new ArrayList<>();
        for (GitLabel label : student.getLabels()) {
            String state = extraLabels.contains(label.getId()) ? DiffDto.STATE_EXTRACT : DiffDto.STATE_CORRECT;
            diffs.add(new DiffDto(label.getLabelInfo().getValue(), state));
        }
        for (GitLabel label : reference.getLabels()) {
            if (missingLabels.contains(label.getId())) {
                diffs.add(new DiffDto(label.getLabelInfo().getValue(), DiffDto.STATE_MISSED));
            }
        }

        String severity = (extraLabels.isEmpty() && missingLabels.isEmpty()) 
                ? NodeDto.SEVERITY_IDENTICAL : NodeDto.SEVERITY_MODIFIED;

        return NodeDto.from(student, severity, diffs);
    }

    private NodeDto createStudentOnlyNode(Commit student) {
        List<DiffDto> diffs = student.getLabels().stream()
                .map(l -> new DiffDto(l.getLabelInfo().getValue(), DiffDto.STATE_EXTRACT))
                .collect(Collectors.toList());
        return NodeDto.from(student, NodeDto.SEVERITY_EXTRA, diffs);
    }

    private NodeDto createReferenceOnlyNode(Commit reference) {
        List<DiffDto> diffs = reference.getLabels().stream()
                .map(l -> new DiffDto(l.getLabelInfo().getValue(), DiffDto.STATE_MISSED))
                .collect(Collectors.toList());
        return NodeDto.from(reference, NodeDto.SEVERITY_EXTRA, diffs);
    }

    private List<LinkDto> buildMergedLinks(CommitGraph studentGraph, CommitGraph referenceGraph) {
        Set<LinkDto> links = new HashSet<>();
        for (Map.Entry<Integer, Set<Integer>> entry : studentGraph.getAdjacencyMatrix().entrySet()) {
            Commit source = studentGraph.getVertex(entry.getKey());
            for (Integer targetNum : entry.getValue()) {
                Commit target = studentGraph.getVertex(targetNum);
                links.add(new LinkDto(source.getHash(), target.getHash()));
            }
        }
        for (Map.Entry<Integer, Set<Integer>> entry : referenceGraph.getAdjacencyMatrix().entrySet()) {
            Commit source = referenceGraph.getVertex(entry.getKey());
            String sourceId = getMergedId(source, g2ToG1, studentGraph);
            for (Integer targetNum : entry.getValue()) {
                Commit target = referenceGraph.getVertex(targetNum);
                String targetId = getMergedId(target, g2ToG1, studentGraph);
                links.add(new LinkDto(sourceId, targetId));
            }
        }
        return new ArrayList<>(links);
    }

    private String getMergedId(Commit commit, Map<Integer, Integer> mapping, CommitGraph otherGraph) {
        Integer otherNum = mapping.get(commit.getNumber());
        if (otherNum != null) {
            return otherGraph.getVertex(otherNum).getHash();
        }
        return commit.getHash();
    }
}
