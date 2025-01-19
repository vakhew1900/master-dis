package org.master.diploma.git.graph;

import org.master.diploma.git.git.model.Commit;

public abstract class Vertex {

    public abstract int getNumber();


    public Commit asCommit(){
        return (Commit) this;
    }
}
