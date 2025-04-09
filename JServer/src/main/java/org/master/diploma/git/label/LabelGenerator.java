package org.master.diploma.git.label;

import org.master.diploma.git.git.model.CommitGraph;

public abstract class LabelGenerator {

    public abstract void makeLabelForGitGraph(CommitGraph commitGraph);
}
