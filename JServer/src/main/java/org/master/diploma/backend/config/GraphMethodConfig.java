package org.master.diploma.backend.config;

import org.master.diploma.backend.service.ComparisonService;
import org.master.diploma.git.graph.dto.ComparisonResultBuilder;
import org.master.diploma.git.graph.dto.merged_graph.MergedGraphResultBuilder;
import org.master.diploma.git.graph.dto.two_graph.TwoGraphResultBuilder;
import org.master.diploma.git.graph.subgraphmethod.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class GraphMethodConfig {

    @Bean
    public Map<ComparisonService.ComparisonMethod, SubgraphMethodExecutor> methodExecutors() {
        Map<ComparisonService.ComparisonMethod, SubgraphMethodExecutor> map = new EnumMap<>(ComparisonService.ComparisonMethod.class);
        map.put(ComparisonService.ComparisonMethod.BRANCH, new BranchMethodExecutor());
        map.put(ComparisonService.ComparisonMethod.BRUTE_FORCE, new BruteForceMethodExecutor());
        map.put(ComparisonService.ComparisonMethod.DP, new DpMethodHelper());
        map.put(ComparisonService.ComparisonMethod.UNIQUE_LABEL, new UniqueLabelMethodExecutor());
        return map;
    }

    @Bean
    public Map<ComparisonService.ReportType, ComparisonResultBuilder<?, ?>> resultBuilders() {
        Map<ComparisonService.ReportType, ComparisonResultBuilder<?, ?>> map = new EnumMap<>(ComparisonService.ReportType.class);
        map.put(ComparisonService.ReportType.TWO_GRAPH, new TwoGraphResultBuilder());
        map.put(ComparisonService.ReportType.MERGED_GRAPH, new MergedGraphResultBuilder());
        return map;
    }
}
