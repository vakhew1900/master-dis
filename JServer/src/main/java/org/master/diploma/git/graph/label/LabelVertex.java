package org.master.diploma.git.graph.label;

import org.master.diploma.git.graph.Vertex;
import org.master.diploma.git.label.GitLabel;
import org.master.diploma.git.label.Label;

import java.util.ArrayList;
import java.util.List;

public abstract class LabelVertex<T extends Label> extends Vertex {

    private List<T> labels = new ArrayList<>();

    public LabelVertex(){

    }

    public LabelVertex(List<T> labels) {
        this.labels = labels;
    }



    public List<T> getLabels() {
        return labels;
    }

    public void addLabels(List<T> labels) {
        this.labels.addAll(labels);
    }

    public void addLabel(T label) {
        labels.add(label);
    }

    public boolean contains(int labelId) {
        for (T label : labels) {
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

        if (!(vertex instanceof LabelVertex)) {
            return  result;
        }

        for (var label : getLabels()) {
            for (var otherLabel : ((LabelVertex)vertex).getLabels()) {
                result = result || label.equals(otherLabel);
            }
        }
        return result;
    }
}
