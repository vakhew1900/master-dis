package org.git_tutor.git_logic.graph.dto.converter;

import org.git_tutor.git_logic.model.CommitGraph;
import org.git_tutor.git_logic.graph.GraphCompareResult;
import org.git_tutor.git_logic.graph.dto.samples.GitGraphDto;

/**
 * Abstract converter to transform CommitGraph into GitGraphDto.
 * Subclasses define specific logic for reference or student graphs.
 */
public abstract class GitGraphConverter {

    protected final GraphCompareResult result;

    protected GitGraphConverter(GraphCompareResult result) {
        this.result = result;
    }

    /**
     * Template method that performs the conversion.
     */
    public abstract GitGraphDto convert(CommitGraph commitGraph);
}
