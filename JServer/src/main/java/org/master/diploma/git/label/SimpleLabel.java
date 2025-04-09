package org.master.diploma.git.label;

import java.util.Objects;

public class SimpleLabel extends Label{

    private int id;

    public SimpleLabel(int id){
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimpleLabel that = (SimpleLabel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
