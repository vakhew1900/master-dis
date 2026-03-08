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
     * @param context    The report configuration context.
     * @throws IOException If an error occurs during file operations.
     */
    void generateReport(GitComparisonResultDto result, ReportContext context) throws IOException;

    /**
     * Generates the report and attempts to open it.
     *
     * @param result     The comparison result DTO.
     * @param context    The report configuration context.
     */
    void generateAndOpenReport(GitComparisonResultDto result, ReportContext context);
}
