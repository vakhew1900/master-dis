package org.master.diploma.git.graph.dto.converter;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.samples.GitGraphDto;
import org.master.diploma.git.graph.dto.samples.LinkDto;
import org.master.diploma.git.graph.dto.samples.NodeDto;
import org.master.diploma.git.graph.dto.samples.DiffDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
