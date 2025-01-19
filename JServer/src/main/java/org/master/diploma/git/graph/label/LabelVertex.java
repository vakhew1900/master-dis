package org.master.diploma.git.graph.label;

import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.label.GitLabel;

import java.util.List;

public abstract class LabelVertex extends Vertex {

    private List<GitLabel> labels;
    public List<GitLabel> getLabels() {
        return labels;
    }

    public void addLabels(List<GitLabel> labels) {
        this.labels.addAll(labels);
    }
    public void addLabel(GitLabel label){
        labels.add(label);
    }
}
