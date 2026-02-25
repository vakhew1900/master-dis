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
     * @throws IOException If an error occurs during file operations.
     */
    void generateReport(GitComparisonResultDto result) throws IOException;

    /**
     * Generates the report and attempts to open it.
     *
     * @param result     The comparison result DTO.
     */
    void generateAndOpenReport(GitComparisonResultDto result);
}
