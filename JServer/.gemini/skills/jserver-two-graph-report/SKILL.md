# SKILL: JServer Two-Graph Side-by-Side Report

This skill manages the visualization of Git graph comparisons using two separate, synchronized networks (Student vs. Reference).

## 📌 Architecture
- **DTO**: `org.master.diploma.git.graph.dto.two_graph.TwoGraphComparisonResultDto`
- **Post-Processor**: `org.master.diploma.git.graph.dto.two_graph.TwoGraphComparisonPostProcessor`
- **Frontend Template**: `src/main/resources/two_compare_graph/report_template.html`

## ⚙️ Workflow
1. Use `TwoGraphComparisonResultDto` to encapsulate the comparison results.
2. The `TwoGraphComparisonPostProcessor` identifies `MOVABLE` nodes by comparing `EXTRA` nodes in both graphs.
3. The frontend renders two separate `vis.Network` instances.
4. Clicking a node in one graph highlights the matched node in the other via `syncSelection`.

## 🎨 Visualization Rules
- **IDENTICAL**: Green (`#365939`) - Node matches perfectly.
- **MODIFIED**: Yellow (`#5e5339`) - Node matches, but diffs are present.
- **EXTRA**: Red (`#593939`) - Node exists in one graph but not the other.
- **MOVABLE**: Blue (`#384c67`) - Node content matches but its position in the graph differs.
