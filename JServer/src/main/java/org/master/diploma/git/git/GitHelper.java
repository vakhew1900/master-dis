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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.master.diploma.git.git.model.Commit;

public final class GitHelper {

    private static final Logger logger = LogManager.getLogger(GitHelper.class);
    public static final int START_COMMIT_NUMBER = 0;
    public static final int INCORRECT_COMMIT_NUMBER = -1;

    public static List<RevCommit> getAllRevCommits(String path) {
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
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Commit revCommitToCommit(RevCommit commit, String repositoryPath, int number) {
        try (Git git = Git.open(new File(repositoryPath))) {
            Repository repository = git.getRepository();

            var out = new ByteArrayOutputStream();
            try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
                // настройки diffFormatter
                diffFormatter.setRepository(repository);
                diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
                diffFormatter.setDetectRenames(true);
                diffFormatter.setContext(3);

                List<DiffEntry> diffEntries = List.of();
                // получение всех diffEntries
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


                // Получить список diff`ов
                List<String> diffs = new ArrayList<>();
                for (DiffEntry diffEntry : diffEntries) {
                    diffFormatter.format(diffEntry);
                    diffs.add(out.toString());
                }

                return new Commit(commit, diffEntries, diffs, number);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Commit> getAllCommits(List<RevCommit> revCommits, String path) {
        AtomicInteger number = new AtomicInteger();
        return revCommits
                .stream()
                .map(revCommit -> revCommitToCommit(revCommit, path, number.getAndIncrement()))
                .toList();
    }

    public static Map<Integer, Set<Integer>> createAdjacencyMatrix(Map<String, Integer> hashToNumber, List<RevCommit> revCommits) {
        Map<Integer, Set<Integer>> adjacencyMatrix = new HashMap<>();

        for (int i = 0; i <= revCommits.size(); i++) { // инициализация матрицы. количество строк - количество коммитов
            adjacencyMatrix.put(i, new HashSet<>());
        }

        Function<RevCommit, Integer> getNumber = (revCommit) -> hashToNumber.get(revCommit.name());

        revCommits.forEach(
                revCommit -> {
                    int curNumber = getNumber.apply(revCommit);
                    if (revCommit.getParentCount() > 0) {
                        for (RevCommit parent : revCommit.getParents()) {
                            adjacencyMatrix
                                    .get(getNumber.apply(parent))   // добавляем в матрицу со строкой под номером родителя текущую вершину
                                    .add(curNumber);
                        }
                    }
                }
        );

        return adjacencyMatrix;
    }

    /**
     * @param commits - список коммитов
     * @return - словарь, где ключом является hash, а значением number
     */
    private static Map<String, Integer> createHashToNumberMap(List<Commit> commits) {
        Map<String, Integer> map = new HashMap<>();

        commits.forEach(
                commit -> {
                    map.put(commit.getHash(), commit.getNumber());
                }
        );

        return map;
    }
}
