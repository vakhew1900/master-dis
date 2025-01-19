package org.master.diploma.git.label;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.Objects;

public class GitLabelInfo {

    private String value;
    private String oldFileName;
    private String newFileName;
    private DiffEntry.ChangeType changeType;

    public GitLabelInfo(String value, String oldFileName, String newFileName, DiffEntry.ChangeType changeType) {
        this.value = value;
        this.oldFileName = oldFileName;
        this.newFileName = newFileName;
        this.changeType = changeType;
    }

    public GitLabelInfo(String value, DiffEntry diffEntry){
        this(value, diffEntry.getOldPath(), diffEntry.getNewPath(), diffEntry.getChangeType());
    }

    public String getValue() {
        return value;
    }

    public String getOldFileName() {
        return oldFileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public DiffEntry.ChangeType getChangeType() {
        return changeType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GitLabelInfo labelInfo = (GitLabelInfo) o;
        return Objects.equals(value, labelInfo.value) && Objects.equals(oldFileName, labelInfo.oldFileName) && Objects.equals(newFileName, labelInfo.newFileName) && changeType == labelInfo.changeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, oldFileName, newFileName, changeType);
    }
}
