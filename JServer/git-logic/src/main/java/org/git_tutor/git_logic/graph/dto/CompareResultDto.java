package org.git_tutor.git_logic.graph.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.git_tutor.git_logic.graph.GraphCompareResult;
import org.git_tutor.git_logic.model.CommitGraph;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO representing the mapping between matched vertices in two graphs.
 * Used for interactive highlighting in the visualization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing the mapping between matched vertices in two graphs.")
public class CompareResultDto {

    public static class FIELDS {
        public static final String MATCHED_HASHES = "matched_hashes_1_to_2";
    }

    @SerializedName(FIELDS.MATCHED_HASHES)
    @Schema(name = FIELDS.MATCHED_HASHES, description = "Map of commit hashes from graph 1 to graph 2")
    private Map<String, String> matchedHashes1To2;

    /**
     * Creates a CompareResultDto from raw GraphCompareResult and the original graphs.
     * Converts vertex numbers to commit hashes for the frontend.
     * Uses hashes as the primary mapping key to remain compatible with all view modes.
     */
    public static CompareResultDto from(CommitGraph g1, CommitGraph g2, GraphCompareResult result) {
        Map<String, String> mapping = new HashMap<>();
        result.getMatchingVertices().forEach((n1, n2) -> {
            String hash1 = g1.getVertex(n1).getHash();
            String hash2 = g2.getVertex(n2).getHash();
            mapping.put(hash1, hash2);
        });
        return new CompareResultDto(mapping);
    }
}
