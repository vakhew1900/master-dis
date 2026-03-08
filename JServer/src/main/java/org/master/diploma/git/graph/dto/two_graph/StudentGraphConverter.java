package org.master.diploma.git.graph.dto.two_graph;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.samples.DiffDto;
import org.master.diploma.git.graph.dto.samples.NodeDto;
import org.master.diploma.git.label.GitLabel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Converter for the first graph (Student's graph - G1).
 * Marks extra elements (not present in reference) as EXTRACT.
 */
public class StudentGraphConverter extends GitGraphConverter {

    public StudentGraphConverter(GraphCompareResult result) {
        super(result);
    }

    @Override
    protected String getSeverity(int vertexNumber) {
        if (!result.getMatchingVertices().containsKey(vertexNumber)) {
            return NodeDto.SEVERITY_EXTRA;
        }
        GraphCompareResult.LabelError error = result.getLabelErrors().get(vertexNumber);
        if (error != null && !error.getExtraLabels().isEmpty()) {
            return NodeDto.SEVERITY_MODIFIED;
        }
        return NodeDto.SEVERITY_IDENTICAL;
    }

    @Override
    protected List<DiffDto> buildDiffs(Commit commit) {
        List<DiffDto> resultDiffs = new ArrayList<>();
        Set<Integer> extraIds = new HashSet<>();
        
        GraphCompareResult.LabelError error = result.getLabelErrors().get(commit.getNumber());
        if (error != null) {
            extraIds.addAll(error.getExtraLabels());
        }

        boolean isMatched = result.getMatchingVertices().containsKey(commit.getNumber());

        for (GitLabel label : commit.getLabels()) {
            String state = DiffDto.STATE_CORRECT;
            if (!isMatched || extraIds.contains(label.getId())) {
                state = DiffDto.STATE_EXTRACT;
            }
            resultDiffs.add(new DiffDto(label.getLabelInfo().getValue(), state));
        }

        return resultDiffs;
    }
}
