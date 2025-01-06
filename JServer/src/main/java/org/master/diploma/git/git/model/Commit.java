package org.master.diploma.git.git.model;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.master.diploma.git.graph.Vertex;

import java.time.Instant;
import java.util.List;

public class Commit extends Vertex {

    private String hash;
    private String message;
    private Instant commitDate;
    private Instant authorDate;
    private String author;
    private String email;
    private List<String> diffs;
    private transient  DiffEntry diffEntry;


    public Commit(RevCommit revCommit) {
        this.hash = revCommit.getName();
        this.message = revCommit.getShortMessage();
        this.author =  revCommit.getAuthorIdent().getName();
        this.email = revCommit.getAuthorIdent().getEmailAddress();
        this.authorDate = revCommit.getAuthorIdent().getWhenAsInstant();
        this.commitDate = Instant.ofEpochSecond(revCommit.getCommitTime());
    }

    public Commit(RevCommit revCommit, DiffEntry diffEntry, List<String> diffs){
        this(revCommit);
        this.diffEntry = diffEntry;
        this.diffs = diffs;
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

    public DiffEntry getDiffEntry() {
        return diffEntry;
    }

    public void setDiffEntry(DiffEntry diffEntry) {
        this.diffEntry = diffEntry;
    }
}
