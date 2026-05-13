package org.master.diploma.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.master.diploma.backend.config.Constants;
import org.master.diploma.backend.service.ComparisonService;
import org.master.diploma.backend.service.ZipProcessingService;
import org.master.diploma.backend.support.FileHelper;
import org.master.diploma.git.graph.dto.GitComparisonResultDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping(Constants.Routes.COMPARISON)
@RequiredArgsConstructor
@Tag(name = "Comparison Operations", description = "Endpoints for direct repository comparison via file uploads")
public class ComparisonController {

    private final ComparisonService comparisonService;
    private final ZipProcessingService zipProcessingService;

    @PostMapping(value = Constants.Routes.COMPARE_FILES, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Compare two Git repositories provided as ZIP files")
    public ResponseEntity<GitComparisonResultDto> compareFiles(
            @RequestParam("reference") MultipartFile referenceFile,
            @RequestParam("student") MultipartFile studentFile,
            @RequestParam(defaultValue = "MERGED_GRAPH") ComparisonService.ReportType reportType,
            @RequestParam(defaultValue = "BRANCH") ComparisonService.ComparisonMethod method) throws IOException {

        Path tempDir = Files.createTempDirectory("jserver-cmp-");
        Path refPath = tempDir.resolve("reference");
        Path studentPath = tempDir.resolve("student");

        Files.createDirectories(refPath);
        Files.createDirectories(studentPath);

        try {
            zipProcessingService.unzip(referenceFile.getInputStream(), refPath);
            zipProcessingService.unzip(studentFile.getInputStream(), studentPath);

            GitComparisonResultDto result = comparisonService.compareDirectories(
                    refPath.toFile(),
                    studentPath.toFile(),
                    reportType,
                    method
            );

            return ResponseEntity.ok(result);
        } finally {
            FileHelper.deleteRecursive(tempDir.toFile());
        }
    }
}
