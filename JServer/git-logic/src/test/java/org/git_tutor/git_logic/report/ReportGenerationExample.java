//package org.master.diploma.git.report;
//
//import org.junit.jupiter.api.Test;
//import org.master.diploma.git.git.GitHelper;
//import org.master.diploma.git.git.model.CommitGraph;
//import org.master.diploma.git.graph.GraphCompareResult;
//import org.master.diploma.git.graph.dto.GitComparisonResultDto;
//import org.master.diploma.git.graph.subgraphmethod.BranchMethodExecutor;
//
//import java.io.File;
//
//public class ReportGenerationExample {
//
//    private static final String REPOSITORIES_DIRECTORY = "src/test/resources/repositories/";
//
//    @Test
//    public void generateReportTest() {
//        String repositoryPath = "test-1";
//        String fullPath = new File(REPOSITORIES_DIRECTORY + repositoryPath).getAbsolutePath();
//
//        System.out.println("Loading graphs from: " + fullPath);
//
//        // 1. Load graphs
//        CommitGraph studentGraph = GitHelper.createCommitGraph(fullPath);
//        CommitGraph referenceGraph = GitHelper.createCommitGraph(fullPath);
//
//        // 2. Compare graphs
//        BranchMethodExecutor executor = new BranchMethodExecutor();
//        GraphCompareResult compareResult = executor.execute(studentGraph, referenceGraph);
//
//        // 3. Create DTO
//        GitComparisonResultDto resultDto = new GitComparisonResultDto(studentGraph, referenceGraph, compareResult);
//
//        // 4. Generate report
//        HtmlReportGenerator generator = new HtmlReportGenerator();
//        String outputPath = "comparison_report.html";
//
//        // Use generateReport instead of generateAndOpenReport for CI/CLI environment
//        // but we can use generateAndOpenReport if we want to see it.
//        // Let's use generateReport to avoid browser popping up during automated tests.
//        try {
//            generator.generateReport(resultDto, outputPath);
//            System.out.println("Report generated: " + new File(outputPath).getAbsolutePath());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
