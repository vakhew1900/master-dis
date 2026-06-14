package org.git_tutor.git_logic.graph.dto.samples;

import io.swagger.v3.oas.annotations.media.Schema;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.git_tutor.git_logic.model.Commit;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Data Transfer Object for a single node (commit) in the Git graph.
 * Contains details about the commit and its comparison severity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for a single node (commit) in the Git graph")
public class NodeDto {
    /** Severity status indicating the node is matched but moved. */
    public static final String SEVERITY_MOVABLE = "MOVABLE";
    /** Severity status indicating the node is missed in the student's work. */
    public static final String SEVERITY_MISSED = "MISSED";
    /** Severity status indicating the node is extra/unmatched in one of the graphs. */
    public static final String SEVERITY_EXTRA = "EXTRA";
    /** Severity status indicating the node is matched but has differences in labels. */
    public static final String SEVERITY_MODIFIED = "MODIFIED";
    /** Severity status indicating the node is matched and identical. */
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
    @Schema(name = FIELDS.ID, description = "Unique identifier (commit hash)")
    private String id;

    @SerializedName(FIELDS.NUMBER)
    @Schema(name = FIELDS.NUMBER, description = "Ordinal number of the commit")
    private int number;

    @SerializedName(FIELDS.HASH)
    @Schema(name = FIELDS.HASH, description = "Full SHA hash of the commit")
    private String hash;

    @SerializedName(FIELDS.MESSAGE)
    @Schema(name = FIELDS.MESSAGE, description = "Commit message")
    private String message;

    @SerializedName(FIELDS.COMMIT_DATE)
    @Schema(name = FIELDS.COMMIT_DATE, description = "Commit creation date (ISO 8601)")
    private String commitDate;

    @SerializedName(FIELDS.AUTHOR_DATE)
    @Schema(name = FIELDS.AUTHOR_DATE, description = "Authoring date (ISO 8601)")
    private String authorDate;

    @SerializedName(FIELDS.AUTHOR)
    @Schema(name = FIELDS.AUTHOR, description = "Author information")
    private AuthorDto author;

    @SerializedName(FIELDS.DIFFS)
    @Schema(name = FIELDS.DIFFS, description = "List of changed files or diff stats")
    private List<DiffDto> diffs;

    @SerializedName(FIELDS.SEVERITY)
    @Schema(name = FIELDS.SEVERITY, description = "Comparison severity status (EXTRA, MODIFIED, IDENTICAL)")
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
