package org.master.diploma.git.graph.dto.two_graph;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.GitGraphCompareResult;
import org.master.diploma.git.graph.dto.converter.GitGraphConverter;
import org.master.diploma.git.graph.dto.samples.DiffDto;
import org.master.diploma.git.graph.dto.samples.NodeDto;
import org.master.diploma.git.label.GitLabel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converter for the second graph (Reference graph - G2).
 * Marks missing elements (present in reference, but not in student's work) as MISSED.
 */
public class ReferenceGraphConverter extends TwoGraphConverter {

    private final Map<Integer, Integer> g2ToG1;

    public ReferenceGraphConverter(GitGraphCompareResult result) {
        super(result);
        this.g2ToG1 = result.getMatchingVertices().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (a, b) -> a));
    }

    @Override
    protected String getSeverity(int vertexNumber) {
        Integer g1Number = g2ToG1.get(vertexNumber);
        if (g1Number == null) {
            return NodeDto.SEVERITY_EXTRA;
        }
        if (result.getMovableVertices().contains(vertexNumber)) {
            return NodeDto.SEVERITY_MOVABLE;
        }
        GraphCompareResult.LabelError error = result.getLabelErrors().get(g1Number);
        if (error != null && !error.getMissingLabels().isEmpty()) {
            return NodeDto.SEVERITY_MODIFIED;
        }
        return NodeDto.SEVERITY_IDENTICAL;
    }

    @Override
    protected List<DiffDto> buildDiffs(Commit commit) {
        List<DiffDto> resultDiffs = new ArrayList<>();
        Set<Integer> missingIds = new HashSet<>();
        
        Integer g1Number = g2ToG1.get(commit.getNumber());
        if (g1Number != null) {
            GraphCompareResult.LabelError error = result.getLabelErrors().get(g1Number);
            if (error != null) {
                missingIds.addAll(error.getMissingLabels());
            }
        }

        for (GitLabel label : commit.getLabels()) {
            String state = DiffDto.STATE_CORRECT;
            if (g1Number == null || missingIds.contains(label.getId())) {
                state = DiffDto.STATE_MISSED;
            }
            resultDiffs.add(new DiffDto(label.getLabelInfo().getValue(), state));
        }

        return resultDiffs;
    }
}
