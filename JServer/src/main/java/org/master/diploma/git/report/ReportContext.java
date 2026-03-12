package org.master.diploma.git.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Configuration context for report generation.
 */
@Getter
@Builder
@AllArgsConstructor
public class ReportContext {
    private final String templatePath;
    private final String stylePath;
    private final String scriptPath;
    private final String commonStylePath; // New: for shared styles
    private final String commonScriptPath; // New: for shared JS
    private final String outputPath;
}
