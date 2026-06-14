package org.git_tutor.git_logic;

import org.git_tutor.git_logic.graph.dto.merged_graph.MergedGraphResultBuilder;
import org.git_tutor.git_logic.model.CommitGraph;
import org.git_tutor.git_logic.graph.GraphCompareResult;
import org.git_tutor.git_logic.graph.dto.GitComparisonResultDto;
import org.git_tutor.git_logic.graph.dto.two_graph.TwoGraphResultBuilder;
import org.git_tutor.git_logic.graph.subgraphmethod.BranchMethodExecutor;
import org.git_tutor.git_logic.graph.subgraphmethod.SubgraphMethodExecutor;
import org.git_tutor.git_logic.label.LabelGenerator;
import org.git_tutor.git_logic.label.SimpleLabelGenerator;
import org.git_tutor.git_logic.report.HtmlReportGenerator;
import org.git_tutor.git_logic.report.ReportGenerator;

import java.io.IOException;

import static org.git_tutor.git_logic.report.ReportConfigurations.mergedGraphContext;
import static org.git_tutor.git_logic.report.ReportConfigurations.twoGraphContext;

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

    public void run(String studentRepoPath, String referenceRepoPath, String baseOutputPath) throws IOException {
        System.out.println("Processing student repository: " + studentRepoPath);
        CommitGraph studentGraph = GitHelper.createCommitGraph(studentRepoPath);
        
        System.out.println("Processing reference repository: " + referenceRepoPath);
        CommitGraph referenceGraph = GitHelper.createCommitGraph(referenceRepoPath);

        System.out.println("Generating labels using " + labelGenerator.getClass().getSimpleName() + "...");
        labelGenerator.makeLabelForGitGraph(studentGraph);
        labelGenerator.makeLabelForGitGraph(referenceGraph);

        System.out.println("Comparing graphs using " + methodExecutor.getClass().getSimpleName() + "...");
        GraphCompareResult compareResult = methodExecutor.execute(studentGraph, referenceGraph);

        System.out.println("Generating reports...");
        
        // Report 1: Two Graph Comparison
        GitComparisonResultDto twoGraphDto = new TwoGraphResultBuilder().build(studentGraph, referenceGraph, compareResult);
        String twoGraphPath = baseOutputPath.replace(".html", "_two.html");
        if (twoGraphPath.equals(baseOutputPath)) twoGraphPath += "_two.html";
        reportGenerator.generateAndOpenReport(twoGraphDto,twoGraphContext(twoGraphPath));

        // Report 2: Merged Graph Comparison
        GitComparisonResultDto mergedGraphDto =
                new MergedGraphResultBuilder().build(studentGraph, referenceGraph, compareResult);
        String mergedGraphPath = baseOutputPath.replace(".html", "_merged.html");
        if (mergedGraphPath.equals(baseOutputPath)) mergedGraphPath += "_merged.html";
        reportGenerator.generateAndOpenReport(mergedGraphDto,mergedGraphContext(mergedGraphPath));
        
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
        ReportGenerator reportGenerator = new HtmlReportGenerator();
        LabelGenerator labelGenerator = SimpleLabelGenerator.getInstance();

        GitGraphComparisonApp app = new GitGraphComparisonApp(executor, reportGenerator, labelGenerator);
        try {
            app.run(studentPath, referencePath, outputPath);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
