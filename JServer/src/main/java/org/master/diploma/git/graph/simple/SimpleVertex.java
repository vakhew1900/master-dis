package org.master.diploma.git.graph.simple;

import org.master.diploma.git.graph.Vertex;

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
}
