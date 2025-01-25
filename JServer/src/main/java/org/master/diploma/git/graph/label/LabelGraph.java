package org.master.diploma.git.graph.label;

import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.graph.simple.SimpleGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LabelGraph<T extends LabelVertex> extends SimpleGraph<T>  implements Cloneable{
    public LabelGraph(List<T> vertices, Map<Integer, Set<Integer>> adjacencyMatrix) {
        super(vertices, adjacencyMatrix);
    }

    protected LabelGraph<T> removeLabels(List<Integer> labelIdsParameter) {
        LabelGraph<T> subgraph = this.clone();
        List<Integer>  labelIds = new ArrayList<>(labelIdsParameter);


        subgraph.getVertices().forEach( // удаление всех меток из вершин
                vertex -> {

                    List<Integer> deletedLabelIndexes = new ArrayList<>();
                    for(int i = 0; i < labelIds.size(); i++){ // удаляем все метки в вершинах
                        int current = labelIds.get(i);
                        if (vertex.contains(current)) {
                            vertex.removeLabel(current);
                            deletedLabelIndexes.add(i);
                        }
                    }

                    deletedLabelIndexes.forEach( // удаляем все использованные метки
                            index -> labelIds.remove(index.intValue())
                    );
                }
        );

        List<Integer> removingVertexNumber = subgraph
                .getVertices()
                .stream()
                .filter(vertex -> vertex.getLabels().isEmpty())
                .map(Vertex::getNumber)
                .toList();

        removingVertexNumber.forEach(
                subgraph::removeVertex
        );

        return  subgraph;
    }



    @Override
    public LabelGraph<T> clone() {
       SimpleGraph<T> graphClone = super.clone();
       return new LabelGraph<T>(graphClone.getVertices(), ((SimpleGraph<T>) graphClone).getAdjacencyMatrix());
    }
}
