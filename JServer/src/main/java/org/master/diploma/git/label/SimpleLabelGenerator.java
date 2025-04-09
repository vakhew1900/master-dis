package org.master.diploma.git.label;

import org.eclipse.jgit.diff.DiffEntry;
import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleLabelGenerator extends LabelGenerator {

    private static int idCounter = 1;
    private static int numberCounter = 1;
    private static SimpleLabelGenerator instance;

    private static final String DIFF_START = "d";
    private static final String HUNK_START = "@";
    private Map<GitLabelInfo, Integer> labelNumbers = new HashMap<>();

    private SimpleLabelGenerator() {

    }

    static {
        instance = new SimpleLabelGenerator();
    }

    public static SimpleLabelGenerator getInstance() {
        return instance;
    }

    @Override
    public void makeLabelForGitGraph(CommitGraph commitGraph) {

        commitGraph.getVertices().forEach(
                vertex -> {
                    addLabels(vertex.asCommit());
                }
        );
    }

    protected void addLabels(Commit commit) {
        for (int i = 0; i < commit.getDiffs().size(); i++) {
            List<GitLabel> labels = createLabels(commit.getDiffs().get(i), commit.getDiffEntries().get(i));
            commit.addLabels(labels);
        }
    }

    private List<GitLabel> createLabels(String diff, DiffEntry diffEntry) {
        List<String> diffList = List.of(diff.split("\n"));
        List<GitLabel> labels = new ArrayList<>();

        boolean isHunk = false;

        for (String changes : diffList) {
            if (changes.startsWith(DIFF_START)) {
                isHunk = false;
            }
            if (changes.startsWith(HUNK_START)) {
                isHunk = true;
            } else if (isHunk) {
                int id = idCounter++;
                GitLabelInfo labelInfo = new GitLabelInfo(changes, diffEntry);
                int number = findNumber(labelInfo);
                labels.add(new GitLabel(id, number, labelInfo));
            }
        }
        return labels;
    }

    private int findNumber(GitLabelInfo labelInfo) {

        if (labelNumbers.containsKey(labelInfo)) {
            return labelNumbers.get(labelInfo);
        }

        labelNumbers.put(labelInfo, numberCounter++);
        return numberCounter;
    }

}
