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
public class HtmlReportGenerator {

    private static final Logger logger = LogManager.getLogger(HtmlReportGenerator.class);
    private static final String TEMPLATE_PATH = "/report_template.html";
    private final Gson gson;

    public HtmlReportGenerator() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Generates an HTML report from the comparison result.
     *
     * @param result     The comparison result DTO.
     * @param outputPath The path where to save the generated HTML file.
     * @throws IOException If an error occurs during file operations.
     */
    public void generateReport(GitComparisonResultDto result, String outputPath) throws IOException {
        String json = gson.toJson(result);
        String template = readTemplate();

        String html = template.replace("{{DATA}}", json);

        Path path = Path.of(outputPath);
        Files.writeString(path, html, StandardCharsets.UTF_8);
        logger.info("Report generated successfully at: " + path.toAbsolutePath());
    }

    /**
     * Generates the report and attempts to open it in the default browser.
     *
     * @param result     The comparison result DTO.
     * @param outputPath The path where to save the generated HTML file.
     */
    public void generateAndOpenReport(GitComparisonResultDto result, String outputPath) {
        try {
            generateReport(result, outputPath);
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

    private String readTemplate() throws IOException {
        try (InputStream is = getClass().getResourceAsStream(TEMPLATE_PATH)) {
            if (is == null) {
                throw new FileNotFoundException("Template not found: " + TEMPLATE_PATH);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining(""));
            }
        }
    }
}
