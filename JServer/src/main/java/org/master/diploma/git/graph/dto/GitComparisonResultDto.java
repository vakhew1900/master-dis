package org.master.diploma.git.graph.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.converter.ReferenceGraphConverter;
import org.master.diploma.git.graph.dto.converter.StudentGraphConverter;

/**
 * Data Transfer Object (DTO) representing the comprehensive result of comparing two Git commit graphs.
 * It contains the DTO representations of both graphs and the raw comparison result.
 * <p>
 * Объект передачи данных (DTO), представляющий полный результат сравнения двух графов коммитов Git.
 * Он содержит DTO-представления обоих графов и необработанный результат сравнения.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitComparisonResultDto {

    /**
     * Defines constants for the field names used in JSON serialization.
     * <p>
     * Определяет константы для имен полей, используемых при JSON-сериализации.
     */
    public static class FIELDS {
        public static final String FIRST_GRAPH = "first_graph";
        public static final String SECOND_GRAPH = "second_graph";
        public static final String COMPARE_RESULT = "compare_result";
    }

    /**
     * The Data Transfer Object for the first Git graph, including comparison statuses for its nodes.
     * <p>
     * Объект передачи данных для первого графа Git, включая статусы сравнения для его узлов.
     */
    @SerializedName(FIELDS.FIRST_GRAPH)
    private GitGraphDto firstGraph;

    /**
     * The Data Transfer Object for the second Git graph, including comparison statuses for its nodes.
     * <p>
     * Объект передачи данных для второго графа Git, включая статусы сравнения для его узлов.
     */
    @SerializedName(FIELDS.SECOND_GRAPH)
    private GitGraphDto secondGraph;

    /**
     * The raw result of the graph comparison, detailing matching vertices and label errors.
     * <p>
     * Необработанный результат сравнения графов, детализирующий сопоставленные вершины и ошибки меток.
     */
    @SerializedName(FIELDS.COMPARE_RESULT)
    private GraphCompareResult compareResult;

    /**
     * Constructs a GitComparisonResultDto by transforming CommitGraphs and the comparison result.
     * <p>
     * Создает GitComparisonResultDto, преобразуя CommitGraph и результат сравнения.
     *
     * @param commitGraph1       the first graph to transform / первый граф для преобразования
     * @param commitGraph2       the second graph to transform / второй граф для преобразования
     * @param graphCompareResult the result of the comparison between the two graphs / результат сравнения между двумя графами
     */
    public GitComparisonResultDto(CommitGraph commitGraph1, CommitGraph commitGraph2, GraphCompareResult graphCompareResult) {
        this.firstGraph = new StudentGraphConverter(graphCompareResult).convert(commitGraph1);
        this.secondGraph = new ReferenceGraphConverter(graphCompareResult).convert(commitGraph2);
        this.compareResult = graphCompareResult;
    }
}
