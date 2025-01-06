package org.master.diploma.git.git;

import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.master.diploma.git.git.model.Commit;

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

    public static Commit revCommitToCommit(RevCommit commit, String repositoryPath)  {
        try (Git git = Git.open(new File(repositoryPath))) {
            Repository repository = git.getRepository();

            var out = new ByteArrayOutputStream();
            try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
                diffFormatter.setRepository(repository);
                diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
                diffFormatter.setDetectRenames(true);
                diffFormatter.setContext(3);

                List<DiffEntry> diffEntries = List.of();
                if (commit.getParentCount() > 0) {
                    for (RevCommit parent : commit.getParents()) {
                        diffEntries.addAll(diffFormatter.scan(parent.getTree(), commit.getTree()));
                    }
                } else {
                    CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
                    try (ObjectReader reader = repository.newObjectReader()) {
                        newTreeParser.reset(reader, commit.getTree().getId());
                    }
                    CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
                    diffEntries = diffFormatter.scan(oldTreeParser, newTreeParser);
                }

                List<String> diffs = new ArrayList<>();
                for (DiffEntry diffEntry : diffEntries) {
                    System.out.println("    " + diffEntry.getChangeType() + " " + diffEntry.getOldPath() + " -> " + diffEntry.getNewPath());
                    diffFormatter.format(diffEntry);
                    diffs.add(out.toString());
                }

               return new Commit(commit, diffEntries, diffs);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
