package org.master.diploma.git.git.model;

import lombok.Data;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.master.diploma.git.graph.label.LabelVertex;
import org.master.diploma.git.label.GitLabel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Commit extends LabelVertex<GitLabel> implements Cloneable {

    private String hash;
    private String message;
    private Instant commitDate;
    private Instant authorDate;
    private String author;
    private String email;
    private List<String> diffs;
    private transient List<DiffEntry> diffEntries;
    private int number;
    private transient RevCommit revCommit;


    public Commit(RevCommit revCommit) {
        this.hash = revCommit.getName();
        this.message = revCommit.getShortMessage();
        this.author = revCommit.getAuthorIdent().getName();
        this.email = revCommit.getAuthorIdent().getEmailAddress();
        this.authorDate = revCommit.getAuthorIdent().getWhenAsInstant();
        this.commitDate = Instant.ofEpochSecond(revCommit.getCommitTime());
        this.revCommit = revCommit;
    }

    public Commit(
            RevCommit revCommit,
            List<DiffEntry> diffEntry,
            List<String> diffs,
            int number
    ) {
        this(revCommit);
        this.diffEntries = diffEntry;
        this.diffs = diffs;
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return number == commit.number &&
                Objects.equals(hash, commit.hash) &&
                Objects.equals(message, commit.message) &&
                Objects.equals(commitDate, commit.commitDate) &&
                Objects.equals(authorDate, commit.authorDate) &&
                Objects.equals(author, commit.author) &&
                Objects.equals(email, commit.email) &&
                Objects.equals(diffs, commit.diffs) &&
                Objects.equals(diffEntries, commit.diffEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                hash,
                message,
                commitDate,
                authorDate,
                author,
                email,
                diffs,
                diffEntries,
                number
        );
    }

    @Override
    public Commit clone() {
        Commit commit = new Commit(
                revCommit,
                this.diffEntries,
                this.diffs,
                this.number
        );
        List <GitLabel> gitLabels = new ArrayList<>(getLabels());
        commit.addLabels(gitLabels);
        return commit;
    }
}
