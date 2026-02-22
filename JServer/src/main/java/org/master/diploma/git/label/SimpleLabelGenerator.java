package org.master.diploma.git.label;

import org.eclipse.jgit.diff.DiffEntry;
import org.master.diploma.git.git.model.Commit;
import org.master.diploma.git.git.model.CommitGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generator that creates simple labels for Git commits based on their diffs.
 * It parses Git diff output, extracts hunks, and assigns unique identifiers to each change.
 */
public class SimpleLabelGenerator extends LabelGenerator {

    private static int idCounter = 1;
    private static int numberCounter = 1;
    private static SimpleLabelGenerator instance;

    /**
     * Start of a diff section in Git output (e.g., "diff --git ...").
     */
    private static final String DIFF_START = "d";

    /**
     * Start of a hunk header in Git diff output (e.g., "@@ -1,1 +1,1 @@").
     */
    private static final String HUNK_START = "@";

    /**
     * Map to maintain consistent numbering for identical label information across different commits.
     */
    private Map<GitLabelInfo, Integer> labelNumbers = new HashMap<>();

    private SimpleLabelGenerator() {

    }

    static {
        instance = new SimpleLabelGenerator();
    }

    /**
     * Returns the singleton instance of SimpleLabelGenerator.
     *
     * @return the instance
     */
    public static SimpleLabelGenerator getInstance() {
        return instance;
    }

    /**
     * Iterates through all vertices in the commit graph and generates labels for each commit.
     *
     * @param commitGraph the graph of commits to process
     */
    @Override
    public void makeLabelForGitGraph(CommitGraph commitGraph) {

        commitGraph.getVertices().forEach(
                vertex -> {
                    addLabels(vertex.asCommit());
                }
        );
    }

    /**
     * Extracts diffs from a commit and creates corresponding labels.
     *
     * @param commit the commit to label
     */
    protected void addLabels(Commit commit) {
        for (int i = 0; i < commit.getDiffs().size(); i++) {
            List<GitLabel> labels = createLabels(commit.getDiffs().get(i), commit.getDiffEntries().get(i));
            commit.addLabels(labels);
        }
    }

    /**
     * Parses a raw diff string and creates a list of GitLabels.
     * It specifically looks for hunks (sections of code changes) and ignores headers.
     *
     * @param diff      the raw diff string for a file
     * @param diffEntry information about the file being changed
     * @return a list of generated GitLabels
     */
    private List<GitLabel> createLabels(String diff, DiffEntry diffEntry) {
        List<String> diffList = List.of(diff.split("\n"));
        List<GitLabel> labels = new ArrayList<>();

        boolean isHunk = false;

        for (String changes : diffList) {
            // Check if we are starting a new file diff
            if (changes.startsWith(DIFF_START)) {
                isHunk = false;
            }
            // Check if we found a hunk header
            if (changes.startsWith(HUNK_START)) {
                isHunk = true;
            } else if (isHunk) {
                // If we are within a hunk, every line represents a change label
                int id = idCounter++;
                GitLabelInfo labelInfo = new GitLabelInfo(changes, diffEntry);
                int number = findNumber(labelInfo);
                labels.add(new GitLabel(id, number, labelInfo));
            }
        }
        return labels;
    }

    /**
     * Assigns or retrieves a unique number for the given label info.
     * This ensures that identical changes (same content and file context) 
     * get the same number across different parts of the graph.
     *
     * @param labelInfo the label information to identify
     * @return a unique number for the label
     */
    private int findNumber(GitLabelInfo labelInfo) {

        if (labelNumbers.containsKey(labelInfo)) {
            return labelNumbers.get(labelInfo);
        }

        labelNumbers.put(labelInfo, numberCounter++);
        return numberCounter;
    }

}
