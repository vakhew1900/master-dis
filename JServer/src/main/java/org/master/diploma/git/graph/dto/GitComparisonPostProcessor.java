package org.master.diploma.git.graph.dto;

import org.master.diploma.git.git.model.CommitGraph;

/**
 * Base abstract class for post-processing Git comparison results.
 * Different strategies are implemented for two-graph vs merged-graph views.
 */
public abstract class GitComparisonPostProcessor<T> {

    /**
     * Performs post-processing on the comparison DTO/result.
     *
     * @param result the comparison result object (DTO or raw) to update
     * @param first  the first commit graph
     * @param second  the second commit graph
     */
    public abstract void postProcess(T result, CommitGraph first, CommitGraph second);
}
