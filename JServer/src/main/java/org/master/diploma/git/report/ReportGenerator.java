package org.master.diploma.git.report;

import org.master.diploma.git.graph.dto.GitComparisonResultDto;

import java.io.IOException;

/**
 * Interface for generating reports from Git comparison results.
 */
public interface ReportGenerator {

    /**
     * Generates a report from the comparison result.
     *
     * @param result     The comparison result DTO.
     * @param outputPath The path where to save the generated report.
     * @throws IOException If an error occurs during file operations.
     */
    void generateReport(GitComparisonResultDto result, String outputPath) throws IOException;

    /**
     * Generates the report and attempts to open it.
     *
     * @param result     The comparison result DTO.
     * @param outputPath The path where to save the generated report.
     */
    void generateAndOpenReport(GitComparisonResultDto result, String outputPath);
}
