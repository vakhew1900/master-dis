package org.master.diploma.git.graph.dto.samples;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for a link (edge) between two commits in the Git graph.
 * <p>
 * Объект передачи данных для связи (ребра) между двумя коммитами в графе Git.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto {
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
