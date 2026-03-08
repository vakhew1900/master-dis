package org.master.diploma.git.graph.dto.two_graph;

import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.converter.GitGraphConverter;

/**
 * Base abstract class for converters used in the two-graph side-by-side view.
 */
public abstract class TwoGraphConverter extends GitGraphConverter {

    protected TwoGraphConverter(GraphCompareResult result) {
        super(result);
    }
    
    // Common helper methods for two-graph conversion can be added here
}
