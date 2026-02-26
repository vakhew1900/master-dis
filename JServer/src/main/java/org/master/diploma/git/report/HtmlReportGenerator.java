package org.master.diploma.git.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.master.diploma.git.graph.dto.GitComparisonResultDto;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Generator for HTML reports to visualize Git graph comparison results.
 */
public class HtmlReportGenerator implements ReportGenerator {

    private static final Logger logger = LogManager.getLogger(HtmlReportGenerator.class);
    private static final String TEMPLATE_PATH = "/report_template.html";
    private static final String STYLE_PATH = "/report_style.css";
    private static final String SCRIPT_PATH = "/report_script.js";
    private final Gson gson;
    private final String outputPath;

    public HtmlReportGenerator(String outputPath) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.outputPath = outputPath;
    }

    /**
     * Generates an HTML report from the comparison result.
     *
     * @param result     The comparison result DTO.
     * @throws IOException If an error occurs during file operations.
     */
    @Override
    public void generateReport(GitComparisonResultDto result) throws IOException {
        String json = gson.toJson(result);
        String template = readResource(TEMPLATE_PATH);
        String css = readResource(STYLE_PATH);
        String js = readResource(SCRIPT_PATH);

        String html = template
                .replace("{{CSS}}", css)
                .replace("{{JS}}", js)
                .replace("{{DATA}}", json);

        Path path = Path.of(outputPath);
        Files.writeString(path, html, StandardCharsets.UTF_8);
        logger.info("Report generated successfully at: " + path.toAbsolutePath());
    }

    /**
     * Generates the report and attempts to open it in the default browser.
     *
     * @param result     The comparison result DTO.
     */
    @Override
    public void generateAndOpenReport(GitComparisonResultDto result) {
        try {
            generateReport(result);
            File htmlFile = new File(outputPath);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(htmlFile.toURI());
            } else {
                logger.warn("Desktop is not supported, cannot open browser automatically.");
            }
        } catch (Exception e) {
            logger.error("Failed to generate or open report: " + e.getMessage(), e);
        }
    }

    private String readResource(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}
