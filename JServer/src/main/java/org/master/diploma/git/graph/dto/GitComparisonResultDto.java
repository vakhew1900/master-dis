package org.master.diploma.git.graph.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import org.master.diploma.git.graph.dto.merged_graph.MergedGraphComparisonResultDto;
import org.master.diploma.git.graph.dto.two_graph.TwoGraphComparisonResultDto;

/**
 * Base interface for Git comparison result Data Transfer Objects.
 * This allows different visualization strategies (Two-Graph vs Merged-Graph) 
 * to be handled by a unified reporting system.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TwoGraphComparisonResultDto.class, name = "two_graph"),
    @JsonSubTypes.Type(value = MergedGraphComparisonResultDto.class, name = "merged_graph")
})
@Schema(
    oneOf = {TwoGraphComparisonResultDto.class, MergedGraphComparisonResultDto.class},
    discriminatorProperty = "type"
)
public interface GitComparisonResultDto {
    // Marker interface for comparison result DTOs
}
