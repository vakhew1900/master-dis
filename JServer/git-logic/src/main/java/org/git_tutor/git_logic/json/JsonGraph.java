package org.git_tutor.git_logic.json;

import com.google.gson.annotations.SerializedName;
import org.git_tutor.git_logic.graph.Graph;
import org.git_tutor.git_logic.graph.Vertex;
import org.git_tutor.git_logic.graph.label.LabelGraph;
import org.git_tutor.git_logic.graph.label.LabelVertex;
import org.git_tutor.git_logic.graph.label.SimpleLabelVertex;
import org.git_tutor.git_logic.label.Label;
import org.git_tutor.git_logic.label.SimpleLabel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonGraph {

    public static final String EDGES = "edges";
    public static final String LABELS = "labels";
    public static final String VERTICES = "vertices";

    @SerializedName(VERTICES)
    private List<Integer> vertices;

    @SerializedName(EDGES)
    private Map<Integer, List<Integer>> edges;

    @SerializedName(LABELS)
    private Map<Integer, List<Integer>> labels;

    public <T extends LabelVertex<?>> JsonGraph(Graph<T> graph) {
        vertices = graph.getVertices().stream().map(Vertex::getNumber).toList();
        edges = vertices
                .stream()
                .collect(
                        Collectors.toMap(
                                vertex -> vertex,
                                graph::getChildrenNumbers
                        )
                );

        labels = vertices
                .stream()
                .collect(
                        Collectors.toMap(
                                vertex -> vertex,
                                vertex -> graph.getVertex(vertex).getLabels().stream().map(Label::getId).toList()
                        )
                );
    }


    public Graph<SimpleLabelVertex> toGraph() {
        Graph<SimpleLabelVertex> graph = new LabelGraph<>();


        vertices.forEach(
                vertexNumber -> {
                    var vertex = new SimpleLabelVertex(vertexNumber);
                    graph.addVertex(vertex);
                }
        );

        labels.forEach(
                (vertexNumber, labels) -> {
                    graph.getVertex(vertexNumber).addLabels(
                            labels
                                    .stream()
                                    .map(SimpleLabel::new)
                                    .collect(Collectors.toList())
                    );
                }
        );

        edges.forEach(
                (parent, children) -> {

                    children.forEach(
                            child -> {
                                graph.addEdge(parent, child);
                            }
                    );
                }
        );

        return graph;
    }
}
