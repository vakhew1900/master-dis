package org.master.diploma.git.graph.dto.samples;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.master.diploma.git.git.model.Commit;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
public class NodeDto {
    /** Severity status indicating the node is matched but moved. <p> Статус серьезности, указывающий, что узел сопоставлен, но перемещен. */
    public static final String SEVERITY_MOVABLE = "MOVABLE";
    /** Severity status indicating the node is missed in the student's work. <p> Статус серьезности, указывающий, что узел отсутствует в работе студента. */
    public static final String SEVERITY_MISSED = "MISSED";
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
    private List<DiffDto> diffs;

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
     * @param diffs    The diffs/labels for the node. / Различия/метки для узла.
     * @return A new NodeDto instance. / Новый экземпляр NodeDto.
     */
    public static NodeDto from(Commit commit, String severity, List<DiffDto> diffs) {
        return NodeDto.builder()
                .id(commit.getHash())
                .number(commit.getNumber())
                .hash(commit.getHash())
                .message(commit.getMessage())
                .commitDate(DateTimeFormatter.ISO_INSTANT.format(commit.getCommitDate()))
                .authorDate(DateTimeFormatter.ISO_INSTANT.format(commit.getAuthorDate()))
                .author(new AuthorDto(commit.getAuthor(), commit.getEmail()))
                .diffs(diffs)
                .severity(severity)
                .build();
    }
}
