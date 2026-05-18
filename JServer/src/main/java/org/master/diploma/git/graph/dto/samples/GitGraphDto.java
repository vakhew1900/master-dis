package org.master.diploma.git.graph.dto.samples;

import io.swagger.v3.oas.annotations.media.Schema;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a Git commit graph in a serializable format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a Git commit graph")
public class GitGraphDto {

    public static class FIELDS {
        public static final String NODES = "nodes";
        public static final String LINKS = "links";
    }

    @SerializedName(FIELDS.NODES)
    @Schema(name = FIELDS.NODES, description = "List of nodes (commits) in the graph")
    private List<NodeDto> nodes;

    @SerializedName(FIELDS.LINKS)
    @Schema(name = FIELDS.LINKS, description = "List of links between commits in the graph")
    private List<LinkDto> links;
}
