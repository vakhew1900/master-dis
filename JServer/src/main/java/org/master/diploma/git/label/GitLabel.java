package org.master.diploma.git.label;


import java.util.Objects;

public class GitLabel extends Label {

    private int id;
    private int number;
    private GitLabelInfo labelInfo;

    public GitLabel(int id, int number, GitLabelInfo labelInfo) {
        this.id = id;
        this.number = number;
        this.labelInfo = labelInfo;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if (this.getClass().equals(obj.getClass())) {
            return this.number == ((GitLabel) obj).getNumber();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number);
    }

    public GitLabelInfo getLabelInfo() {
        return labelInfo;
    }
}

