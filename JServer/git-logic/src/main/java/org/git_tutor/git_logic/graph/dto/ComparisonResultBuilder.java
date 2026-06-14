package org.git_tutor.git_logic.graph.dto;

import org.git_tutor.git_logic.model.CommitGraph;
import org.git_tutor.git_logic.graph.GraphCompareResult;

/**
 * Base abstract class for building Git comparison result DTOs.
 * Acts as a factory strategy that can be injected into reporting services.
 *
 * @param <R> The type of the resulting GitComparisonResultDto.
 * @param <T> The type of object used for post-processing.
 */
public abstract class ComparisonResultBuilder<R extends GitComparisonResultDto, T> {

    protected final GitComparisonPostProcessor<T> postProcessor;

    protected ComparisonResultBuilder(GitComparisonPostProcessor<T> postProcessor) {
        this.postProcessor = postProcessor;
    }

    /**
     * Builds the final comparison result DTO using the raw comparison data.
     *
     * @param g1        The student commit graph.
     * @param g2        The reference commit graph.
     * @param rawResult The raw comparison result from the matching algorithm.
     * @return The fully constructed and post-processed DTO.
     */
    public abstract R build(CommitGraph g1, CommitGraph g2, GraphCompareResult rawResult);
}
