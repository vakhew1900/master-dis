package org.master.diploma.git;

import org.master.diploma.git.git.GitHelper;
import org.master.diploma.git.git.model.CommitGraph;
import org.master.diploma.git.graph.GraphCompareResult;
import org.master.diploma.git.graph.dto.GitComparisonResultDto;
import org.master.diploma.git.graph.subgraphmethod.BranchMethodExecutor;
import org.master.diploma.git.graph.subgraphmethod.SubgraphMethodExecutor;
import org.master.diploma.git.label.LabelGenerator;
import org.master.diploma.git.label.SimpleLabelGenerator;
import org.master.diploma.git.report.HtmlReportGenerator;
import org.master.diploma.git.report.ReportGenerator;

import java.io.IOException;

/**
 * Console application to compare two Git repositories and generate a report.
 */
public class GitGraphComparisonApp {

    private final SubgraphMethodExecutor methodExecutor;
    private final ReportGenerator reportGenerator;
    private final LabelGenerator labelGenerator;

    public GitGraphComparisonApp(SubgraphMethodExecutor methodExecutor, ReportGenerator reportGenerator, LabelGenerator labelGenerator) {
        this.methodExecutor = methodExecutor;
        this.reportGenerator = reportGenerator;
        this.labelGenerator = labelGenerator;
    }

    public void run(String studentRepoPath, String referenceRepoPath) throws IOException {
        System.out.println("Processing student repository: " + studentRepoPath);
        CommitGraph studentGraph = GitHelper.createCommitGraph(studentRepoPath);
        
        System.out.println("Processing reference repository: " + referenceRepoPath);
        CommitGraph referenceGraph = GitHelper.createCommitGraph(referenceRepoPath);

        System.out.println("Generating labels using " + labelGenerator.getClass().getSimpleName() + "...");
        labelGenerator.makeLabelForGitGraph(studentGraph);
        labelGenerator.makeLabelForGitGraph(referenceGraph);

        System.out.println("Comparing graphs using " + methodExecutor.getClass().getSimpleName() + "...");
        GraphCompareResult compareResult = methodExecutor.execute(studentGraph, referenceGraph);

        System.out.println("Generating report...");
        GitComparisonResultDto resultDto = new GitComparisonResultDto(studentGraph, referenceGraph, compareResult);
        reportGenerator.generateAndOpenReport(resultDto);
        
        System.out.println("Done!");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java GitGraphComparisonApp <student_repo_path> <reference_repo_path> [output_html_path]");
            System.err.println("If output_html_path is not specified, 'report.html' will be generated in the current directory.");
            System.exit(1);
        }

        String studentPath = args[0];
        String referencePath = args[1];
        String outputPath = args.length >= 3 ? args[2] : "report.html";

        // Default implementation choices
        SubgraphMethodExecutor executor = new BranchMethodExecutor();
        ReportGenerator reportGenerator = new HtmlReportGenerator(outputPath);
        LabelGenerator labelGenerator = SimpleLabelGenerator.getInstance();

        GitGraphComparisonApp app = new GitGraphComparisonApp(executor, reportGenerator, labelGenerator);
        try {
            app.run(studentPath, referencePath);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
