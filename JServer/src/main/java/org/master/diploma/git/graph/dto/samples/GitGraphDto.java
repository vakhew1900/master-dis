package org.master.diploma.git.graph.dto.samples;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
}
