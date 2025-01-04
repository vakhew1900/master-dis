package org.master.diploma.git.git;

import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public final class GitHelper {

    private static final Logger logger = LogManager.getLogger(GitHelper.class);

    public static List<RevCommit> getAllCommits(String path) {
        List<RevCommit> commits = new ArrayList<>();
        File repoDir = new File(path);

        try {
            if (!repoDir.exists() || !repoDir.isDirectory()) {
                System.err.println("Указанный путь не является директорией или не существует.");
                return commits;
            }

            try (Git git = Git.open(repoDir)) {
                Repository repository = git.getRepository();

                // Получаем все ссылки на HEADS (все ветки)
                List<Ref> allHeads = git.branchList().call();

                try (RevWalk revWalk = new RevWalk(repository)) {

                    // Добавляем все HEADs в RevWalk, чтобы охватить коммиты из всех веток
                    for (Ref head : allHeads) {
                        if (head.getTarget() == null) {
                            continue; // если ссылка повреждена, пропускаем ее
                        }
                        ObjectId headCommitId = head.getTarget().getObjectId();
                        revWalk.markStart(revWalk.parseCommit(headCommitId));
                    }

                    for (RevCommit commit : revWalk) {
                        commits.add(commit);
                    }
                    revWalk.dispose();
                }

            }

            return commits;
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void printDiff(RevCommit commit, String repositoryPath)  {
        try (Git git = Git.open(new File(repositoryPath))) {
            Repository repository = git.getRepository();

            var out = new ByteArrayOutputStream();
            try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
                diffFormatter.setRepository(repository);
                //diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
                diffFormatter.setDetectRenames(true);
                diffFormatter.setContext(3);

                if (commit.getParentCount() > 0) {
                    for (RevCommit parent : commit.getParents()) {
                        List<DiffEntry> diffs = diffFormatter.scan(parent.getTree(), commit.getTree());
                        for (DiffEntry diff : diffs) {
                            System.out.println("    " + diff.getChangeType() + " " + diff.getOldPath() + " -> " + diff.getNewPath());

                            diffFormatter.format(diff);
                            System.out.println(out.toString());
                        }
                    }
                } else {
                    CanonicalTreeParser newTreeParser = new CanonicalTreeParser();

                    try (ObjectReader reader = repository.newObjectReader()) {
                        newTreeParser.reset(reader, commit.getTree().getId());
                    }

                    CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
                    List<DiffEntry> diffs = diffFormatter.scan(oldTreeParser, newTreeParser);
                    for (DiffEntry diff : diffs) {
                        System.out.println("    " + diff.getChangeType() + " " + diff.getOldPath() + " -> " + diff.getNewPath());
                        diffFormatter.format(diff);
                        System.out.println(out.toString());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
