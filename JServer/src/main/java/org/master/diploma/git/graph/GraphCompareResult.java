package org.master.diploma.git.graph;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraphCompareResult {

    boolean invert = false;
    private Map<Integer, Integer> matchingVertices = new HashMap<>();

    private Map<Integer, LabelError> labelErrors;


    public static class LabelError {
        public static final String EXTRA = "extra_labels";
        public static final String MISSING = "missing_labels";

        @SerializedName(EXTRA)
        private List<Integer> extraLabels;

        @SerializedName(MISSING)
        private List<Integer> missingLabels;
    }

    public boolean isBigger (GraphCompareResult other) {
        //todo тут можно дополнить
        return matchingVertices.size() > other.matchingVertices.size();
    }
}
