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
 * <p>
 * Генератор, создающий простые метки для коммитов Git на основе их различий (diffs).
 * Он разбирает вывод Git diff, извлекает "ханки" (hunks) и присваивает уникальные идентификаторы каждому изменению.
 */
public class SimpleLabelGenerator extends LabelGenerator {

    private static int idCounter = 1;
    private static int numberCounter = 1;
    private static SimpleLabelGenerator instance;

    /**
     * Start of a diff section in Git output (e.g., "d" line).
     * <p>
     * Начало секции diff в выводе Git (например, строка, начинающаяся с "d").
     */
    private static final String DIFF_START = "d";

    /**
     * Start of a hunk header in Git diff output (e.g., "@@ -1,1 +1,1 @@").
     * <p>
     * Начало заголовка "ханка" (hunk header) в выводе Git diff (например, "@@ -1,1 +1,1 @@").
     */
    private static final String HUNK_START = "@";

    /**
     * Map to maintain consistent numbering for identical label information across different commits.
     * <p>
     * Карта для поддержания согласованной нумерации идентичной информации о метках в разных коммитах.
     */
    private Map<GitLabelInfo, Integer> labelNumbers = new HashMap<>();

    private SimpleLabelGenerator() {

    }

    static {
        instance = new SimpleLabelGenerator();
    }

    /**
     * Returns the singleton instance of SimpleLabelGenerator.
     * <p>
     * Возвращает единственный экземпляр SimpleLabelGenerator (синглтон).
     *
     * @return the instance / экземпляр
     */
    public static SimpleLabelGenerator getInstance() {
        return instance;
    }

    /**
     * Iterates through all vertices (commits) in the commit graph and generates labels for each commit.
     * <p>
     * Итерирует по всем вершинам (коммитам) в графе коммитов и генерирует метки для каждого коммита.
     *
     * @param commitGraph the graph of commits to process / граф коммитов для обработки
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
     * <p>
     * Извлекает различия (diffs) из коммита и создает соответствующие метки.
     *
     * @param commit the commit to label / коммит, для которого создаются метки
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
     * <p>
     * Разбирает необработанную строку diff и создает список GitLabels.
     * Он специально ищет "ханки" (разделы изменений кода) и игнорирует заголовки.
     *
     * @param diff      the raw diff string for a file / необработанная строка diff для файла
     * @param diffEntry information about the file being changed / информация об изменяемом файле
     * @return a list of generated GitLabels / список сгенерированных GitLabels
     */
    private List<GitLabel> createLabels(String diff, DiffEntry diffEntry) {
        List<String> diffList = List.of(diff.split("\n"));
        List<GitLabel> labels = new ArrayList<>();

        boolean isHunk = false;

        for (String changes : diffList) {
            // Check if we are starting a new file diff
            // Проверяем, начинаем ли мы новый diff файла
            if (changes.startsWith(DIFF_START)) {
                isHunk = false;
            }
            // Check if we found a hunk header
            // Проверяем, найден ли заголовок "ханка"
            if (changes.startsWith(HUNK_START)) {
                isHunk = true;
            } else if (isHunk) {
                // If we are within a hunk, every line represents a change label
                // Если мы находимся внутри "ханка", каждая строка представляет собой метку изменения
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
     * <p>
     * Присваивает или извлекает уникальный номер для данной информации о метке.
     * Это гарантирует, что идентичные изменения (одинаковое содержимое и контекст файла)
     * получают один и тот же номер в разных частях графа.
     *
     * @param labelInfo the label information to identify / информация о метке для идентификации
     * @return a unique number for the label / уникальный номер для метки
     */
    private int findNumber(GitLabelInfo labelInfo) {

        if (labelNumbers.containsKey(labelInfo)) {
            return labelNumbers.get(labelInfo);
        }

        int number = numberCounter++;
        labelNumbers.put(labelInfo, number);
        return number;
    }

}
