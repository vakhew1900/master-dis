package org.master.diploma.git.graph.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object representing a Git commit graph in a serializable format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitGraphDto {

    public static class FIELDS {
        public static final String NODES = "nodes";
        public static final String LINKS = "links";
    }

    @SerializedName(FIELDS.NODES)
    private List<NodeDto> nodes;

    @SerializedName(FIELDS.LINKS)
    private List<LinkDto> links;

    private static final int UNDEFINED_VERTEX_NUMBER = -1;

    /**
     * Converts a CommitGraph entity to a GitGraphDto with comparison information.
     *
     * @param commitGraph the graph to convert
     * @param result      the comparison result
     * @param isFirst     true if converting the first graph, false for the second
     * @return the resulting DTO
     */
    public static GitGraphDto from(CommitGraph commitGraph, GraphCompareResult result, boolean isFirst) {
        final Map<Integer, Integer> matchingVertices = result.getMatchingVertices(); // G1 -> G2 mapping
        final Map<Integer, GraphCompareResult.LabelError> labelErrors = result.getLabelErrors(); // G1 -> LabelError mapping

        // Create G2 -> G1 mapping only if processing the second graph
        final Map<Integer, Integer> g2ToG1 = isFirst ? Collections.emptyMap() :
                matchingVertices.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (a, b) -> a));

        List<NodeDto> nodes = commitGraph.getVertices().stream()
                .map(vertex -> {
                    String severity = getNodeSeverity(
                            vertex.getNumber(),
                            isFirst,
                            matchingVertices,
                            g2ToG1,
                            labelErrors
                    );
                    return NodeDto.from(vertex.asCommit(), severity);
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

    /**
     * Determines the severity status of a node based on the comparison result.
     *
     * @param currentGraphVertexNumber The vertex number in the graph currently being processed.
     * @param isFirst                  True if processing the first graph, false for the second.
     * @param matchingVertices         Map of G1 vertex numbers to G2 vertex numbers.
     * @param g2ToG1                   Map of G2 vertex numbers to G1 vertex numbers.
     * @param labelErrors              Map of G1 vertex numbers to their LabelError.
     * @return A string representing the severity (EXTRA, MODIFIED, IDENTICAL).
     */
    private static String getNodeSeverity(
            int currentGraphVertexNumber,
            boolean isFirst,
            Map<Integer, Integer> matchingVertices,
            Map<Integer, Integer> g2ToG1,
            Map<Integer, GraphCompareResult.LabelError> labelErrors
    ) {
        final int correspondingG1VertexNumber;
        final boolean isMatched;

        if (isFirst) {
            correspondingG1VertexNumber = currentGraphVertexNumber;
            isMatched = matchingVertices.containsKey(currentGraphVertexNumber);
        } else {
            correspondingG1VertexNumber = g2ToG1.getOrDefault(currentGraphVertexNumber, UNDEFINED_VERTEX_NUMBER);
            isMatched = (correspondingG1VertexNumber != UNDEFINED_VERTEX_NUMBER);
        }

        if (!isMatched) {
            return NodeDto.SEVERITY_EXTRA;
        } else {
            // If matched, check for label errors based on the G1 vertex number
            GraphCompareResult.LabelError error = labelErrors.get(correspondingG1VertexNumber);
            if (error != null && (!error.getExtraLabels().isEmpty() || !error.getMissingLabels().isEmpty())) {
                return NodeDto.SEVERITY_MODIFIED;
            } else {
                return NodeDto.SEVERITY_IDENTICAL;
            }
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeDto {
        public static final String SEVERITY_EXTRA = "EXTRA";
        public static final String SEVERITY_MODIFIED = "MODIFIED";
        public static final String SEVERITY_IDENTICAL = "IDENTICAL";

        public static class FIELDS {
            public static final String ID = "id";
            public static final String NUMBER = "number";
            public static final String HASH = "hash";
            public static final String MESSAGE = "message";
            public static final String COMMIT_DATE = "commitDate";
            public static final String AUTHOR_DATE = "authorDate";
            public static final String AUTHOR = "author";
            public static final String DIFFS = "diffs";
            public static final String SEVERITY = "severity";
        }

        @SerializedName(FIELDS.ID)
        private String id;

        @SerializedName(FIELDS.NUMBER)
        private int number;

        @SerializedName(FIELDS.HASH)
        private String hash;

        @SerializedName(FIELDS.MESSAGE)
        private String message;

        @SerializedName(FIELDS.COMMIT_DATE)
        private String commitDate;

        @SerializedName(FIELDS.AUTHOR_DATE)
        private String authorDate;

        @SerializedName(FIELDS.AUTHOR)
        private AuthorDto author;

        @SerializedName(FIELDS.DIFFS)
        private List<String> diffs;

        @SerializedName(FIELDS.SEVERITY)
        private String severity;

        public static NodeDto from(Commit commit, String severity) {
            return NodeDto.builder()
                    .id(commit.getHash())
                    .number(commit.getNumber())
                    .hash(commit.getHash())
                    .message(commit.getMessage())
                    .commitDate(DateTimeFormatter.ISO_INSTANT.format(commit.getCommitDate()))
                    .authorDate(DateTimeFormatter.ISO_INSTANT.format(commit.getAuthorDate()))
                    .author(new AuthorDto(commit.getAuthor(), commit.getEmail()))
                    .diffs(commit.getDiffs())
                    .severity(severity)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorDto {
        public static class FIELDS {
            public static final String NAME = "name";
            public static final String EMAIL = "email";
        }

        @SerializedName(FIELDS.NAME)
        private String name;

        @SerializedName(FIELDS.EMAIL)
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkDto {
        public static class FIELDS {
            public static final String SOURCE = "source";
            public static final String TARGET = "target";
        }

        @SerializedName(FIELDS.SOURCE)
        private String source;

        @SerializedName(FIELDS.TARGET)
        private String target;
    }
}
