package org.master.diploma.git.git.model;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.master.diploma.git.graph.label.LabelVertex;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Commit extends LabelVertex {

    private String hash;
    private String message;
    private Instant commitDate;
    private Instant authorDate;
    private String author;
    private String email;
    private List<String> diffs;
    private transient List<DiffEntry> diffEntries;
    private int number;


    public Commit(RevCommit revCommit) {
        this.hash = revCommit.getName();
        this.message = revCommit.getShortMessage();
        this.author = revCommit.getAuthorIdent().getName();
        this.email = revCommit.getAuthorIdent().getEmailAddress();
        this.authorDate = revCommit.getAuthorIdent().getWhenAsInstant();
        this.commitDate = Instant.ofEpochSecond(revCommit.getCommitTime());
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Instant commitDate) {
        this.commitDate = commitDate;
    }

    public Instant getAuthorDate() {
        return authorDate;
    }

    public void setAuthorDate(Instant authorDate) {
        this.authorDate = authorDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getDiffs() {
        return diffs;
    }

    public List<DiffEntry> getDiffEntries() {
        return diffEntries;
    }

    public void setDiffEntries(List<DiffEntry> diffEntries) {
        this.diffEntries = diffEntries;
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
}
