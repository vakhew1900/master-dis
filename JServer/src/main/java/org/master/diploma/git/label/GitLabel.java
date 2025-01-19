package org.master.diploma.git.label;

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

    public GitLabelInfo getLabelInfo() {
        return labelInfo;
    }
}

