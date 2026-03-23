package org.master.diploma.git.graph.dto.converter;

import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GitGraphCompareResult;
import org.master.diploma.git.graph.dto.samples.GitGraphDto;

/**
 * Abstract converter to transform CommitGraph into GitGraphDto.
 * Subclasses define specific logic for reference or student graphs.
 */
public abstract class GitGraphConverter {

    protected final GitGraphCompareResult result;

    protected GitGraphConverter(GitGraphCompareResult result) {
        this.result = result;
    }

    /**
     * Template method that performs the conversion.
     */
    public abstract GitGraphDto convert(CommitGraph commitGraph);
}
