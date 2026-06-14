package org.git_tutor.git_logic.generator;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.git_tutor.git_logic.graph.Graph;
import org.git_tutor.git_logic.graph.GraphCompareResult;
import org.git_tutor.git_logic.graph.label.LabelVertex;
import org.git_tutor.git_logic.label.SimpleLabel;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class  GraphGeneratorEntity<T extends Graph<LabelVertex<SimpleLabel>>> {

    private T first;
    private T second;
    private GraphCompareResult graphCompareResult;
}
