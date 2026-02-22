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
 * Data Transfer Object (DTO) representing a Git commit graph in a serializable format.
 * This class is used to transfer graph data, including comparison severities, for visualization or API responses.
 * <p>
 * Объект передачи данных (DTO), представляющий граф коммитов Git в сериализуемом формате.
 * Этот класс используется для передачи данных графа, включая статусы сравнения, для визуализации или ответов API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitGraphDto {

    /**
     * Defines constants for the field names used in JSON serialization.
     * <p>
     * Определяет константы для имен полей, используемых при JSON-сериализации.
     */
    public static class FIELDS {
        public static final String NODES = "nodes";
        public static final String LINKS = "links";
    }

    /**
     * List of nodes (commits) in the graph.
     * <p>
     * Список узлов (коммитов) в графе.
     */
    @SerializedName(FIELDS.NODES)
    private List<NodeDto> nodes;

    /**
     * List of links (parent-child relationships) between commits in the graph.
     * <p>
     * Список связей (отношений родитель-потомок) между коммитами в графе.
     */
    @SerializedName(FIELDS.LINKS)
    private List<LinkDto> links;

    /**
     * A constant representing an undefined or unmatched vertex number.
     * Used internally for comparison logic when a vertex has no corresponding match.
     * <p>
     * Константа, представляющая неопределенный или несопоставленный номер вершины.
     * Используется во внутренней логике сравнения, когда вершина не имеет соответствующей пары.
     */
    private static final int UNDEFINED_VERTEX_NUMBER = -1;

    /**
     * Converts a CommitGraph entity to a GitGraphDto with comparison information.
     * <p>
     * Преобразует сущность CommitGraph в GitGraphDto с информацией о сравнении.
     *
     * @param commitGraph the graph to convert / граф для преобразования
     * @param result      the comparison result / результат сравнения
     * @param isFirst     true if converting the first graph, false for the second / true, если преобразуется первый граф; false для второго
     * @return the resulting DTO / результирующий DTO
     */
    public static GitGraphDto from(CommitGraph commitGraph, GraphCompareResult result, boolean isFirst) {
        final Map<Integer, Integer> matchingVertices = result.getMatchingVertices(); // G1 -> G2 mapping / Отображение G1 -> G2
        final Map<Integer, GraphCompareResult.LabelError> labelErrors = result.getLabelErrors(); // G1 -> LabelError mapping / Отображение G1 -> LabelError

        // Create G2 -> G1 mapping only if processing the second graph
        // Создаем отображение G2 -> G1 только при обработке второго графа
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
     * <p>
     * Определяет статус серьезности узла на основе результата сравнения.
     *
     * @param currentGraphVertexNumber The vertex number in the graph currently being processed. / Номер вершины в текущем обрабатываемом графе.
     * @param isFirst                  True if processing the first graph, false for the second. / True, если обрабатывается первый граф; false для второго.
     * @param matchingVertices         Map of G1 vertex numbers to G2 vertex numbers. / Карта номеров вершин G1 к номерам вершин G2.
     * @param g2ToG1                   Map of G2 vertex numbers to G1 vertex numbers. / Карта номеров вершин G2 к номерам вершин G1.
     * @param labelErrors              Map of G1 vertex numbers to their LabelError. / Карта номеров вершин G1 к их LabelError.
     * @return A string representing the severity (EXTRA, MODIFIED, IDENTICAL). / Строка, представляющая серьезность (EXTRA, MODIFIED, IDENTICAL).
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
            // Если сопоставлено, проверяем наличие ошибок меток на основе номера вершины G1
            GraphCompareResult.LabelError error = labelErrors.get(correspondingG1VertexNumber);
            if (error != null && (!error.getExtraLabels().isEmpty() || !error.getMissingLabels().isEmpty())) {
                return NodeDto.SEVERITY_MODIFIED;
            } else {
                return NodeDto.SEVERITY_IDENTICAL;
            }
        }
    }

    /**
     * Data Transfer Object for a single node (commit) in the Git graph.
     * Contains details about the commit and its comparison severity.
     * <p>
     * Объект передачи данных для одного узла (коммита) в графе Git.
     * Содержит подробную информацию о коммите и его статусе сравнения.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeDto {
        /** Severity status indicating the node is extra/unmatched in one of the graphs. <p> Статус серьезности, указывающий, что узел является лишним/несопоставленным в одном из графов. */
        public static final String SEVERITY_EXTRA = "EXTRA";
        /** Severity status indicating the node is matched but has differences in labels. <p> Статус серьезности, указывающий, что узел сопоставлен, но имеет различия в метках. */
        public static final String SEVERITY_MODIFIED = "MODIFIED";
        /** Severity status indicating the node is matched and identical. <p> Статус серьезности, указывающий, что узел сопоставлен и идентичен. */
        public static final String SEVERITY_IDENTICAL = "IDENTICAL";

        /**
         * Defines constants for the field names used in JSON serialization.
         * <p>
         * Определяет константы для имен полей, используемых при JSON-сериализации.
         */
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

        /**
         * Unique identifier for the node (typically commit hash) for visualization.
         * <p>
         * Уникальный идентификатор узла (обычно хеш коммита) для визуализации.
         */
        @SerializedName(FIELDS.ID)
        private String id;

        /**
         * Ordinal number of the commit in the graph.
         * <p>
         * Порядковый номер коммита в графе.
         */
        @SerializedName(FIELDS.NUMBER)
        private int number;

        /**
         * Full SHA hash of the commit.
         * <p>
         * Полный SHA-хеш коммита.
         */
        @SerializedName(FIELDS.HASH)
        private String hash;

        /**
         * Commit message.
         * <p>
         * Сообщение коммита.
         */
        @SerializedName(FIELDS.MESSAGE)
        private String message;

        /**
         * Commit creation date in ISO 8601 format.
         * <p>
         * Дата создания коммита в формате ISO 8601.
         */
        @SerializedName(FIELDS.COMMIT_DATE)
        private String commitDate;

        /**
         * Authoring date of the code in ISO 8601 format.
         * <p>
         * Дата написания кода автором в формате ISO 8601.
         */
        @SerializedName(FIELDS.AUTHOR_DATE)
        private String authorDate;

        /**
         * Author information.
         * <p>
         * Информация об авторе.
         */
        @SerializedName(FIELDS.AUTHOR)
        private AuthorDto author;

        /**
         * List of changed files or diff statistics.
         * <p>
         * Список измененных файлов или статистика diff.
         */
        @SerializedName(FIELDS.DIFFS)
        private List<String> diffs;

        /**
         * Comparison severity status of the node (EXTRA, MODIFIED, IDENTICAL).
         * <p>
         * Статус серьезности сравнения узла (EXTRA, MODIFIED, IDENTICAL).
         */
        @SerializedName(FIELDS.SEVERITY)
        private String severity;

        /**
         * Creates a NodeDto from a Commit object and a comparison severity.
         * <p>
         * Создает NodeDto из объекта Commit и статуса серьезности сравнения.
         *
         * @param commit   The commit object. / Объект коммита.
         * @param severity The comparison severity status. / Статус серьезности сравнения.
         * @return A new NodeDto instance. / Новый экземпляр NodeDto.
         */
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

    /**
     * Data Transfer Object for author information.
     * <p>
     * Объект передачи данных для информации об авторе.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorDto {
        /**
         * Defines constants for the field names used in JSON serialization.
         * <p>
         * Определяет константы для имен полей, используемых при JSON-сериализации.
         */
        public static class FIELDS {
            public static final String NAME = "name";
            public static final String EMAIL = "email";
        }

        /**
         * Author's name.
         * <p>
         * Имя автора.
         */
        @SerializedName(FIELDS.NAME)
        private String name;

        /**
         * Author's email.
         * <p>
         * Email автора.
         */
        @SerializedName(FIELDS.EMAIL)
        private String email;
    }

    /**
     * Data Transfer Object for a link (edge) between two commits in the Git graph.
     * <p>
     * Объект передачи данных для связи (ребра) между двумя коммитами в графе Git.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkDto {
        /**
         * Defines constants for the field names used in JSON serialization.
         * <p>
         * Определяет константы для имен полей, используемых при JSON-сериализации.
         */
        public static class FIELDS {
            public static final String SOURCE = "source";
            public static final String TARGET = "target";
        }

        /**
         * Hash of the source (parent) commit.
         * <p>
         * Хеш исходного (родительского) коммита.
         */
        @SerializedName(FIELDS.SOURCE)
        private String source;

        /**
         * Hash of the target (child) commit.
         * <p>
         * Хеш целевого (дочернего) коммита.
         */
        @SerializedName(FIELDS.TARGET)
        private String target;
    }
}
