package org.git_tutor.git_logic.graph.label;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.git_tutor.git_logic.graph.Vertex;
import org.git_tutor.git_logic.label.SimpleLabel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SimpleLabelVertex extends LabelVertex<SimpleLabel> {

    private int number;

    public SimpleLabelVertex(int number) {
        this.number = number;
    }

    public SimpleLabelVertex(int number, SimpleLabel simpleLabel) {
        super(new ArrayList<>(List.of(simpleLabel)));
        this.number = number;
    }

    @Override
    public Vertex clone() {
        var vertex =  new SimpleLabelVertex();
        vertex.number = number;
        vertex.addLabels(getLabels());
        return vertex;
    }
}
