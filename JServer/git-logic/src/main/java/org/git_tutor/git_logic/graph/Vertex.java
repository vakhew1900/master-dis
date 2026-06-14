package org.git_tutor.git_logic.graph;

import org.git_tutor.git_logic.model.Commit;

public abstract class Vertex implements Cloneable {

    public abstract int getNumber();

    public Commit asCommit() {
        return (Commit) this;
    }

    @Override
    public Vertex clone() {
        try {
            return (Vertex) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public abstract boolean canRelate(Vertex vertex);

    public abstract String toGraphViz();
}
