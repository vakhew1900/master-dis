package org.master.diploma.git.graph.label;

import org.master.diploma.git.graph.simple.SimpleVertex;
import org.master.diploma.git.label.SimpleLabel;

import java.util.ArrayList;
import java.util.List;

public class SimpleLabelVertex extends LabelVertex<SimpleLabel> {

    private int number;

    public SimpleLabelVertex(int number, SimpleLabel simpleLabel) {
        super(new ArrayList<>(List.of(simpleLabel)));
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }
}
