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
 * Uses prefixed vertex numbers for IDs to guarantee uniqueness even with identical hashes.
 */
public class MergedGraphConverter extends GitGraphConverter {

    private final Map<Integer, Integer> g1ToG2;
    private final Map<Integer, Integer> g2ToG1;
    private final CommitGraph targetGraph;

    public MergedGraphConverter(GraphCompareResult result, CommitGraph targetGraph) {
        super(result);
        this.targetGraph = targetGraph;
        this.g1ToG2 = result.getMatchingVertices();
        this.g2ToG1 = result.getMatchingVertices().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (a, b) -> a));
    }


    @Override
    public GitGraphDto convert(CommitGraph currentGraph) {

        List<NodeDto> nodes = new ArrayList<>();
        Set<Integer> processedG2Nodes = new HashSet<>();

        for (Commit studentCommit : currentGraph.getVertices().stream().map(Vertex::asCommit).toList()) {
            Integer g2Number = g1ToG2.get(studentCommit.getNumber());
            String id = "g1_" + studentCommit.getNumber();
            if (g2Number != null) {
                Commit referenceCommit = targetGraph.getVertex(g2Number);
                nodes.add(createMergedNode(id, studentCommit, referenceCommit));
                processedG2Nodes.add(g2Number);
            } else {
                nodes.add(createCurrentOnlyNode(id, studentCommit));
            }
        }

        for (Commit referenceCommit : targetGraph.getVertices().stream().map(Vertex::asCommit).toList()) {
            if (!processedG2Nodes.contains(referenceCommit.getNumber())) {
                String id = "g2_" + referenceCommit.getNumber();
                nodes.add(createTargetOnlyNode(id, referenceCommit));
            }
        }

        List<LinkDto> links = buildMergedLinks(currentGraph, targetGraph);
        return new GitGraphDto(nodes, links);
    }

    private NodeDto createMergedNode(String id, Commit student, Commit reference) {
        String severity = getSeverity(student);
        List<DiffDto> diffs = createDiffs(student, reference);
        NodeDto node = NodeDto.from(student, severity, diffs);
        node.setId(id);
        if (!student.getHash().equals(reference.getHash())) {
            node.setHash(student.getHash().substring(0, 7) + " / " + reference.getHash().substring(0, 7));
        }
        return node;
    }

    private String getSeverity(Commit commit) {
        GraphCompareResult.LabelError error = result.getLabelErrors().get(commit.getNumber());
        if (error == null || (error.getExtraLabels().isEmpty() && error.getMissingLabels().isEmpty())) {
            return NodeDto.SEVERITY_IDENTICAL;
        }
        return NodeDto.SEVERITY_MODIFIED;
    }

    private List<DiffDto> createDiffs(Commit student, Commit reference) {
        GraphCompareResult.LabelError error = result.getLabelErrors().get(student.getNumber());
        Set<Integer> extraLabels = error != null ? new HashSet<>(error.getExtraLabels()) : Collections.emptySet();
        Set<Integer> missingLabels = error != null ? new HashSet<>(error.getMissingLabels()) : Collections.emptySet();

        List<DiffDto> diffs = new ArrayList<>();
        // Add student labels (Identical or Extra)
        for (GitLabel label : student.getLabels()) {
            String state = extraLabels.contains(label.getId()) ? DiffDto.STATE_EXTRACT : DiffDto.STATE_CORRECT;
            diffs.add(new DiffDto(label.getLabelInfo().getValue(), state));
        }
        // Add missing reference labels
        for (GitLabel label : reference.getLabels()) {
            if (missingLabels.contains(label.getId())) {
                diffs.add(new DiffDto(label.getLabelInfo().getValue(), DiffDto.STATE_MISSED));
            }
        }
        return diffs;
    }

    private NodeDto createCurrentOnlyNode(String id, Commit student) {
        List<DiffDto> diffs = student.getLabels().stream()
                .map(l -> new DiffDto(l.getLabelInfo().getValue(), DiffDto.STATE_EXTRACT))
                .collect(Collectors.toList());
        NodeDto node = NodeDto.from(student, NodeDto.SEVERITY_EXTRA, diffs);
        node.setId(id);
        return node;
    }

    private NodeDto createTargetOnlyNode(String id, Commit reference) {
        List<DiffDto> diffs = reference.getLabels().stream()
                .map(l -> new DiffDto(l.getLabelInfo().getValue(), DiffDto.STATE_MISSED))
                .collect(Collectors.toList());
        NodeDto node = NodeDto.from(reference, NodeDto.SEVERITY_MISSED, diffs);
        node.setId(id);
        return node;
    }

    private List<LinkDto> buildMergedLinks(CommitGraph studentGraph, CommitGraph referenceGraph) {
        Set<LinkDto> links = new HashSet<>();
        for (Map.Entry<Integer, Set<Integer>> entry : studentGraph.getAdjacencyMatrix().entrySet()) {
            String sourceId = "g1_" + entry.getKey();
            for (Integer targetNum : entry.getValue()) {
                String targetId = "g1_" + targetNum;
                links.add(new LinkDto(sourceId, targetId));
            }
        }

        for (Map.Entry<Integer, Set<Integer>> entry : referenceGraph.getAdjacencyMatrix().entrySet()) {
            String sourceId = getMergedId(entry.getKey(), g2ToG1);
            for (Integer targetNum : entry.getValue()) {
                String targetId = getMergedId(targetNum, g2ToG1);
                links.add(new LinkDto(sourceId, targetId));
            }
        }
        return new ArrayList<>(links);
    }

    private String getMergedId(int g2Number, Map<Integer, Integer> mapping) {
        Integer g1Number = mapping.get(g2Number);
        if (g1Number != null) {
            return "g1_" + g1Number;
        }
        return "g2_" + g2Number;
    }
}
