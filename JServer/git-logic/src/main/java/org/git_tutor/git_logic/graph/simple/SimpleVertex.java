package org.git_tutor.git_logic.graph.simple;

import org.git_tutor.git_logic.graph.Vertex;

public class SimpleVertex extends Vertex {

    int number;

    public SimpleVertex(int number) {
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public SimpleVertex clone() {
        return new SimpleVertex(number);
    }

    @Override
    public boolean canRelate(Vertex vertex) {
        return true;
    }

    @Override
    public String toGraphViz() {
          return getNumber() + " [label=\"" + getNumber() + "\"];\n";
    }


}
