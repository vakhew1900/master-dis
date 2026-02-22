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
import org.master.diploma.git.git.model.CommitGraph;

/**
 * Вспомогательный класс для работы с Git репозиториями через библиотеку JGit.
 * Отвечает за извлечение коммитов, построение графа зависимостей и вычисление diff-ов.
 */
public final class GitHelper {

    private static final Logger logger = LogManager.getLogger(GitHelper.class);
    
    /** Начальный порядковый номер для индексации коммитов. */
    public static final int START_COMMIT_NUMBER = 0;
    
    /** Константа для обозначения некорректного или отсутствующего номера коммита. */
    public static final int INCORRECT_COMMIT_NUMBER = -1;

    /**
     * Создает структуру графа коммитов (CommitGraph) на основе пути к репозиторию.
     * 
     * @param path Путь к локальному Git репозиторию.
     * @return Объект CommitGraph, содержащий список коммитов и карту смежности.
     */
    public static CommitGraph createCommitGraph(String path) {
        // 1. Получаем все "сырые" коммиты из JGit (RevCommit)
        List<RevCommit> revCommits = getAllRevCommits(path);
        
        // 2. Преобразуем их в наши внутренние объекты Commit (с diff-ами и номерами)
        List<Commit> commits = getAllCommits(revCommits, path);
        
        // 3. Строим матрицу смежности, связывая номера родительских и дочерних коммитов
        Map<Integer, Set<Integer>> map = createAdjacencyMatrix(createHashToNumberMap(commits), revCommits);
        
        return new CommitGraph(commits, map);
    }

    /**
     * Извлекает все коммиты изо всех веток репозитория.
     * 
     * @param path Путь к репозиторию.
     * @return Список всех найденных RevCommit.
     */
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

                    // Собираем все достижимые коммиты
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

    /**
     * Преобразует объект RevCommit из JGit в кастомный объект Commit, вычисляя разницу (diff).
     * 
     * @param commit Исходный коммит.
     * @param repositoryPath Путь к репозиторию для выполнения diff.
     * @param number Порядковый номер, присваиваемый коммиту.
     * @return Объект Commit с заполненными данными о разнице и хешем.
     */
    public static Commit revCommitToCommit(RevCommit commit, String repositoryPath, int number) {
        try (Git git = Git.open(new File(repositoryPath))) {
            Repository repository = git.getRepository();

            var out = new ByteArrayOutputStream();
            try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
                // Настройки форматирования diff
                diffFormatter.setRepository(repository);
                diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL); // Игнорируем пробелы
                diffFormatter.setDetectRenames(true); // Детектируем переименования
                diffFormatter.setContext(0); // Минимум контекста вокруг изменений

                List<DiffEntry> diffEntries = new ArrayList<>();
                
                // Получаем список изменений (DiffEntry)
                if (commit.getParentCount() > 0) {
                    // Если есть родители, сравниваем с ними
                    for (RevCommit parent : commit.getParents()) {
                        diffEntries.addAll(diffFormatter.scan(parent.getTree(), commit.getTree()));
                    }
                } else {
                    // Если родителей нет (первый коммит), сравниваем с "пустым" деревом
                    CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
                    try (ObjectReader reader = repository.newObjectReader()) {
                        newTreeParser.reset(reader, commit.getTree().getId());
                    }
                    CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
                    diffEntries = diffFormatter.scan(oldTreeParser, newTreeParser);
                }

                // Преобразуем DiffEntry в текстовое представление (патчи)
                List<String> diffs = new ArrayList<>();
                for (DiffEntry diffEntry : diffEntries) {
                    diffFormatter.format(diffEntry);
                    diffs.add(out.toString());
                    out.reset(); // Очищаем буфер для следующего файла
                }

                return new Commit(commit, diffEntries, diffs, number);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке коммита " + commit.name() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Преобразует список RevCommit в список Commit, присваивая им порядковые номера.
     * 
     * @param revCommits Список "сырых" коммитов.
     * @param path Путь к репозиторию.
     * @return Список кастомных объектов Commit.
     */
    public static List<Commit> getAllCommits(List<RevCommit> revCommits, String path) {
        AtomicInteger number = new AtomicInteger(START_COMMIT_NUMBER);
        return revCommits
                .stream()
                .map(revCommit -> revCommitToCommit(revCommit, path, number.getAndIncrement()))
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
