package org.master.diploma.git.graph;

import org.master.diploma.git.git.model.Commit;

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
}
