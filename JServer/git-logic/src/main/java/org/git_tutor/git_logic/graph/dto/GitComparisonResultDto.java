package org.git_tutor.git_logic.graph.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import org.git_tutor.git_logic.graph.dto.merged_graph.MergedGraphComparisonResultDto;
import org.git_tutor.git_logic.graph.dto.two_graph.TwoGraphComparisonResultDto;

/**
 * Base interface for Git comparison result Data Transfer Objects.
 * This allows different visualization strategies (Two-Graph vs Merged-Graph) 
 * to be handled by a unified reporting system.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TwoGraphComparisonResultDto.class, name = "two_graph"),
        @JsonSubTypes.Type(value = MergedGraphComparisonResultDto.class, name = "merged_graph")
})
public interface GitComparisonResultDto {
    // Marker interface for comparison result
    @NotNull
    String getType();
}
