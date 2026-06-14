package org.git_tutor.git_logic;

import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
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
import org.git_tutor.git_logic.model.Commit;
import org.git_tutor.git_logic.model.CommitGraph;
import org.git_tutor.git_logic.label.SimpleLabelGenerator;

/**
 * Вспомогательный класс для работы с Git репозиториями через библиотеку JGit.
 */
public final class GitHelper {

    private static final Logger logger = LogManager.getLogger(GitHelper.class);
    
    public static final int START_COMMIT_NUMBER = 0;
    public static final int INCORRECT_COMMIT_NUMBER = -1;

    public static CommitGraph createCommitGraph(String path) {
        return createCommitGraph(new File(path));
    }

    public static CommitGraph createCommitGraph(File repoDir) {
        List<RevCommit> revCommits = getAllRevCommits(repoDir);
        List<Commit> commits = getAllCommits(revCommits, repoDir);
        Map<Integer, Set<Integer>> map = createAdjacencyMatrix(createHashToNumberMap(commits), revCommits);

        var graph = new CommitGraph(commits, map);
        SimpleLabelGenerator.getInstance().makeLabelForGitGraph(graph);

        return graph;
    }

    public static List<RevCommit> getAllRevCommits(String path) {
        return getAllRevCommits(new File(path));
    }

    public static List<RevCommit> getAllRevCommits(File repoDir) {
        List<RevCommit> commits = new ArrayList<>();

        try {
            if (!repoDir.exists() || !repoDir.isDirectory()) {
                logger.error("Указанный путь не является директорией или не существует: " + repoDir.getAbsolutePath());
                return commits;
            }

            try (Git git = Git.open(repoDir)) {
                Repository repository = git.getRepository();

                // Получаем все ссылки на ветки (HEADs)
                List<Ref> allHeads = git.branchList().call();

                try (RevWalk revWalk = new RevWalk(repository)) {
                    // Настраиваем обход: начинаем со всех голов (всех веток)
                    for (Ref head : allHeads) {
                        if (head.getTarget() == null) {
                            continue; // Пропускаем битые ссылки
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
            logger.error("Ошибка при получении RevCommits: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Commit revCommitToCommit(RevCommit commit, File repositoryDir, int number) {
        try (Git git = Git.open(repositoryDir)) {
            Repository repository = git.getRepository();

            var out = new ByteArrayOutputStream();
            try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
                diffFormatter.setRepository(repository);
                diffFormatter.setDetectRenames(true);
                diffFormatter.setContext(0);

                List<DiffEntry> diffEntries = new ArrayList<>();
                
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
                    diffFormatter.format(diffEntry);
                    diffs.add(out.toString());
                    out.reset();
                }

                return new Commit(commit, diffEntries, diffs, number);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке коммита " + commit.name() + ": " + e.getMessage(), e);
        }
    }

    public static List<Commit> getAllCommits(List<RevCommit> revCommits, File repositoryDir) {
        AtomicInteger number = new AtomicInteger(START_COMMIT_NUMBER);
        return revCommits
                .stream()
                .map(revCommit -> revCommitToCommit(revCommit, repositoryDir, number.getAndIncrement()))
                .toList();
    }

    /**
     * Создает матрицу смежности (список связей) для графа коммитов.
     * Связи направлены от родителя к потомку.
     * 
     * @param hashToNumber Карта соответствия хеша коммита его порядковому номеру.
     * @param revCommits Список исходных коммитов для анализа связей.
     * @return Карта, где ключ - номер родителя, значение - набор номеров детей.
     */
    private static Map<Integer, Set<Integer>> createAdjacencyMatrix(Map<String, Integer> hashToNumber, List<RevCommit> revCommits) {
        Map<Integer, Set<Integer>> adjacencyMatrix = new HashMap<>();

        // Инициализируем карту для каждого коммита
        for (int i = 0; i < revCommits.size(); i++) {
            adjacencyMatrix.put(i, new HashSet<>());
        }

        Function<RevCommit, Integer> getNumber = (revCommit) -> hashToNumber.get(revCommit.name());

        revCommits.forEach(
                revCommit -> {
                    Integer curNumber = getNumber.apply(revCommit);
                    if (curNumber == null) return; // Если коммит не попал в список обработки
                    
                    if (revCommit.getParentCount() > 0) {
                        for (RevCommit parent : revCommit.getParents()) {
                            Integer parentNumber = getNumber.apply(parent);
                            if (parentNumber != null) {
                                // Добавляем текущий коммит как "ребенка" к родителю
                                adjacencyMatrix
                                        .get(parentNumber)
                                        .add(curNumber);
                            }
                        }
                    }
                }
        );

        return adjacencyMatrix;
    }

    /**
     * Создает карту для быстрого поиска порядкового номера коммита по его хешу.
     * 
     * @param commits Список коммитов.
     * @return Словарь, где ключ - SHA-1 хеш (строка), а значение - порядковый номер (int).
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
