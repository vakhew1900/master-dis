package org.master.diploma.git.generator;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.master.diploma.git.graph.Graph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.SimpleLabel;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class  GraphGeneratorEntity<T extends Graph<LabelVertex<SimpleLabel>>> {

    private T first;
    private T second;
    private GraphCompareResult graphCompareResult;
}
