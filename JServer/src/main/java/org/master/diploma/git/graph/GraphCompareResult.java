package org.master.diploma.git.graph;

import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;
import lombok.*;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.Label;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphCompareResult {

    boolean invert = false;
    private Map<Integer, Integer> matchingVertices = new HashMap<>();

    private Map<Integer, LabelError> labelErrors;
    int errorCount = 0;

    @Getter
    @Setter
    public static class LabelError {
        public static final String EXTRA = "extra_labels";
        public static final String MISSING = "missing_labels";

        @SerializedName(EXTRA)
        private List<Integer> extraLabels;

        @SerializedName(MISSING)
        private List<Integer> missingLabels;

        public static LabelError createLabelError(LabelVertex<?> firstVertex, LabelVertex<?> secondVertex) {
            Set<? extends Label> firstLabels = new HashSet<>(firstVertex.getLabels());
            Set<? extends Label> secondLabels = new HashSet<>(secondVertex.getLabels());
            LabelError labelError = new LabelError();

            labelError.setExtraLabels(
                    diffLabels(firstLabels, secondLabels)
            );

            labelError.setMissingLabels(
                    diffLabels(secondLabels, firstLabels)
            );

            return labelError;
        }

        private static List<Integer> diffLabels(
                Set<? extends Label> firstLabels,
                Set<? extends Label> secondLabels
        ) {
            return Sets
                    .difference(firstLabels, secondLabels)
                    .stream()
                    .map(Label::getId)
                    .collect(Collectors.toList());
        }
    }

    public void add(GraphCompareResult other) {
        this.matchingVertices.putAll(other.matchingVertices);
        this.labelErrors.putAll(other.labelErrors);
    }

    public boolean isBigger(GraphCompareResult other) {
        //todo тут можно дополнить
        if (matchingVertices.size() > other.matchingVertices.size()) {
            return true;
        }

        if (matchingVertices.size() == other.matchingVertices.size() && errorCount < other.errorCount) {
            return true;
        }

        return false;
    }

    public <T extends LabelVertex<?>> void fillLabelError(Graph<T> first, Graph<T> second) {
        matchingVertices.forEach(
                (firstNumber, secondNumber) -> {
                    T firstVertex = first.getVertex(firstNumber);
                    T secondVertex = second.getVertex(secondNumber);
                    LabelError labelError = LabelError.createLabelError(firstVertex, secondVertex);

                    int index = (invert) ? secondNumber : firstNumber;
                    errorCount += labelError.extraLabels.size() + labelError.missingLabels.size();
                    labelErrors.put(index, labelError);
                }
        );
    }
}
