package org.master.diploma.git.graph.dto;

import com.google.gson.Gson;
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


    private static final Gson GSON = new Gson();
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
     * DTO containing mapping of matched commit hashes between the two graphs.
     */
    @SerializedName(FIELDS.COMPARE_RESULT)
    private CompareResultDto compareResult;

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
        this.compareResult = CompareResultDto.from(commitGraph1, commitGraph2, graphCompareResult);

        // Apply post-processing to identify MOVABLE nodes
        new GitComparisonPostProcessor().postProcess(this, commitGraph1, commitGraph2);
    }


    @Override
    public String toString() {
       return GSON.toJson(this);
    }
}
