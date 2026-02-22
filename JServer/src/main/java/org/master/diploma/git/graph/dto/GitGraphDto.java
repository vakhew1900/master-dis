package org.master.diploma.git.graph.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    /**
     * Converts a CommitGraph entity to a GitGraphDto.
     *
     * @param commitGraph the graph to convert
     * @return the resulting DTO
     */
    public static GitGraphDto from(CommitGraph commitGraph) {
        List<NodeDto> nodes = commitGraph.getVertices().stream()
                .map(vertex -> NodeDto.from(vertex.asCommit()))
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeDto {
        public static class FIELDS {
            public static final String ID = "id";
            public static final String NUMBER = "number";
            public static final String HASH = "hash";
            public static final String MESSAGE = "message";
            public static final String COMMIT_DATE = "commitDate";
            public static final String AUTHOR_DATE = "authorDate";
            public static final String AUTHOR = "author";
            public static final String DIFFS = "diffs";
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

        public static NodeDto from(Commit commit) {
            return NodeDto.builder()
                    .id(commit.getHash())
                    .number(commit.getNumber())
                    .hash(commit.getHash())
                    .message(commit.getMessage())
                    .commitDate(DateTimeFormatter.ISO_INSTANT.format(commit.getCommitDate()))
                    .authorDate(DateTimeFormatter.ISO_INSTANT.format(commit.getAuthorDate()))
                    .author(new AuthorDto(commit.getAuthor(), commit.getEmail()))
                    .diffs(commit.getDiffs())
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
