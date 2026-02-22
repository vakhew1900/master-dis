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

    @SerializedName("nodes")
    private List<NodeDto> nodes;

    @SerializedName("links")
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
        @SerializedName("id")
        private String id;

        @SerializedName("number")
        private int number;

        @SerializedName("hash")
        private String hash;

        @SerializedName("message")
        private String message;

        @SerializedName("commitDate")
        private String commitDate;

        @SerializedName("authorDate")
        private String authorDate;

        @SerializedName("author")
        private AuthorDto author;

        @SerializedName("diffs")
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
        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkDto {
        @SerializedName("source")
        private String source;

        @SerializedName("target")
        private String target;
    }
}
