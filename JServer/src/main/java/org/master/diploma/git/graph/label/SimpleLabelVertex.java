package org.master.diploma.git.graph.label;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.master.diploma.git.label.SimpleLabel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SimpleLabelVertex extends LabelVertex<SimpleLabel> {

    private int number;

    public SimpleLabelVertex(int number, SimpleLabel simpleLabel) {
        super(new ArrayList<>(List.of(simpleLabel)));
        this.number = number;
    }
}
