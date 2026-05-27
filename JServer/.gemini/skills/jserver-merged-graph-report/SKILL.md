# SKILL: JServer Merged-Graph Report

This skill manages the visualization of Git graph comparisons by merging two graphs into a single, unified network.

## 📌 Architecture
- **DTO**: `org.master.diploma.git.graph.dto.merged_graph.MergedGraphComparisonResultDto`
- **Post-Processor**: `org.master.diploma.git.graph.dto.merged_graph.MergedGraphComparisonPostProcessor`
- **Frontend Template**: `src/main/resources/merged_graph/report_template.html`

## ⚙️ Workflow
1. Apply `MergedGraphComparisonPostProcessor` to the raw `GraphCompareResult` to find `MOVABLE` matches.
2. Use `MergedGraphConverter` to build a single `GitGraphDto` from both graphs.
3. The frontend renders a single `vis.Network` where matched nodes are consolidated.

## 🎨 Visualization Rules
- **Consolidated Node**: Represented as a single node in the graph if a match exists.
- **Internal Diffs**: Node info shows a merged list of labels:
    - `IDENTICAL`: Correct labels.
    - `EXTRA`: Labels present in student work but not in reference.
    - `MISSED`: Labels present in reference but missing in student work.
- **Node Colors**:
    - `IDENTICAL`: Perfect match.
    - `MODIFIED`: Match with label differences.
    - `EXTRA`: Student-only commit.
    - `MISSED`: Reference-only commit.
