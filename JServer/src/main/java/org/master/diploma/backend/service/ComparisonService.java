package org.master.diploma.backend.service;

import lombok.RequiredArgsConstructor;
import org.git_tutor.git_logic.GitHelper;
import org.git_tutor.git_logic.model.CommitGraph;
import org.git_tutor.git_logic.graph.GraphCompareResult;
import org.git_tutor.git_logic.graph.dto.ComparisonResultBuilder;
import org.git_tutor.git_logic.graph.dto.GitComparisonResultDto;
import org.git_tutor.git_logic.graph.subgraphmethod.SubgraphMethodExecutor;
import org.git_tutor.git_logic.label.SimpleLabelGenerator;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComparisonService {
    private final FileService fileService;
    private final Map<ComparisonMethod, SubgraphMethodExecutor> methodExecutors;
    private final Map<ReportType, ComparisonResultBuilder<?, ?>> resultBuilders;

    public enum ReportType {
        TWO_GRAPH,
        MERGED_GRAPH
    }

    public enum ComparisonMethod {
        BRANCH,
        BRUTE_FORCE,
        DP,
        UNIQUE_LABEL
    }

    public GitComparisonResultDto compareRepositories(String referenceRepoPath, 
                                                     String studentRepoPath, 
                                                     ReportType reportType,
                                                     ComparisonMethod method) throws IOException {
        
        File referenceDir = fileService.downloadRepository(referenceRepoPath);
        File studentDir = fileService.downloadRepository(studentRepoPath);

        try {
            return compareDirectories(referenceDir, studentDir, reportType, method);
        } finally {
            org.master.diploma.backend.support.FileHelper.deleteRecursive(referenceDir);
            org.master.diploma.backend.support.FileHelper.deleteRecursive(studentDir);
        }
    }

    public GitComparisonResultDto compareDirectories(File referenceDir,
                                                     File studentDir,
                                                     ReportType reportType,
                                                     ComparisonMethod method) throws IOException {
        CommitGraph referenceGraph = GitHelper.createCommitGraph(referenceDir);
        CommitGraph studentGraph = GitHelper.createCommitGraph(studentDir);

        SimpleLabelGenerator.getInstance().makeLabelForGitGraph(referenceGraph);
        SimpleLabelGenerator.getInstance().makeLabelForGitGraph(studentGraph);

        SubgraphMethodExecutor methodExecutor = methodExecutors.get(method);
        if (methodExecutor == null) {
            throw new IllegalArgumentException("Unsupported comparison method: " + method);
        }
        GraphCompareResult compareResult = methodExecutor.execute(studentGraph, referenceGraph);

        ComparisonResultBuilder<?, ?> builder = resultBuilders.get(reportType);
        if (builder == null) {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }

        return builder.build(studentGraph, referenceGraph, compareResult);
    }

}
