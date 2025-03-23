package org.master.diploma.git.graph.label;

import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.label.GitLabel;

import java.util.ArrayList;
import java.util.List;

public abstract class LabelVertex extends Vertex {

    private List<GitLabel> labels = new ArrayList<>();

    public List<GitLabel> getLabels() {
        return labels;
    }

    public void addLabels(List<GitLabel> labels) {
        this.labels.addAll(labels);
    }

    public void addLabel(GitLabel label) {
        labels.add(label);
    }

    public boolean contains(int labelId) {
        for (GitLabel label : labels) {
            if (label.getId() == labelId) {
                return true;
            }
        }
        return false;
    }

    public void removeLabel(int labelId) {
        int deleteIndex = -1;
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).getId() == labelId) {
                deleteIndex = i;
                break;
            }
        }

        if (deleteIndex != -1) {
            labels.remove(deleteIndex);
        }
    }

    @Override
    public boolean canRelate(Vertex vertex) {
        boolean result = false;

        for (var label : getLabels()) {
            for (var otherLabel : getLabels()) {
                result = result || label.equals(otherLabel);
            }
        }
        return result;
    }
}
