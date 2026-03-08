package org.master.diploma.git.report;

/**
 * Helper class containing report configuration constants.
 */
public class ReportConfigurations {

    private static final String BASE_PATH = "/";
    private static final String TWO_COMPARE_DIR = "two_compare_graph/";
    private static final String MERGED_DIR = "merged_graph/";
    private static final String COMMON_DIR = "common/";

    public static ReportContext twoGraphContext(String outputPath) {
        return ReportContext.builder()
                .templatePath(BASE_PATH + TWO_COMPARE_DIR + "report_template.html")
                .stylePath(BASE_PATH + TWO_COMPARE_DIR + "report_style.css")
                .scriptPath(BASE_PATH + TWO_COMPARE_DIR + "report_script.js")
                .commonStylePath(BASE_PATH + COMMON_DIR + "common_style.css")
                .outputPath(outputPath)
                .build();
    }

    public static ReportContext mergedGraphContext(String outputPath) {
        return ReportContext.builder()
                .templatePath(BASE_PATH + MERGED_DIR + "report_template.html")
                .stylePath(BASE_PATH + MERGED_DIR + "report_style.css")
                .scriptPath(BASE_PATH + MERGED_DIR + "report_script.js")
                .commonStylePath(BASE_PATH + COMMON_DIR + "common_style.css")
                .outputPath(outputPath)
                .build();
    }
}
