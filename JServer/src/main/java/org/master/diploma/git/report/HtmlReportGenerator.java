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
 * Generator for HTML reports to visualize comparison results.
 */
public class HtmlReportGenerator implements ReportGenerator {

    private static final Logger logger = LogManager.getLogger(HtmlReportGenerator.class);
    private final Gson gson;

    public HtmlReportGenerator() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void generateReport(GitComparisonResultDto result, ReportContext context) throws IOException {
        String json = gson.toJson(result);
        String template = readResource(context.getTemplatePath());
        String commonCss = context.getCommonStylePath() != null ? readResource(context.getCommonStylePath()) : "";
        String commonJs = context.getCommonScriptPath() != null ? readResource(context.getCommonScriptPath()) : "";
        String css = readResource(context.getStylePath());
        String js = readResource(context.getScriptPath());

        String html = template
                .replace("{{COMMON_CSS}}", commonCss)
                .replace("{{COMMON_JS}}", commonJs)
                .replace("{{CSS}}", css)
                .replace("{{JS}}", js)
                .replace("{{DATA}}", json);

        Path path = Path.of(context.getOutputPath());
        Files.writeString(path, html, StandardCharsets.UTF_8);
        logger.info("Report generated successfully at: " + path.toAbsolutePath());
    }

    @Override
    public void generateAndOpenReport(GitComparisonResultDto result, ReportContext context) {
        try {
            generateReport(result, context);
            File htmlFile = new File(context.getOutputPath());
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
