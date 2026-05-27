
---
name: jserver-report-gen
description: Generate and open HTML reports for Git graph comparison results in the JServer project. Use when you need to visualize the results of a Maximum Common Transitive Subgraph (MCTS) analysis between two Git repositories.
---

# JServer Report Generation

This skill provides workflows and scripts to compare two Git repositories and generate an interactive HTML report using the `HtmlReportGenerator`.

## Core Capabilities

### 1. Repository Comparison & Report Generation
You can compare a student's Git repository against a reference repository. The comparison identifies identical, modified, and extra commits based on their content (diffs) and structure.

**To run a comparison:**
Use the `scripts/compare.ps1` script or run the following Maven command:
```powershell
mvn compile exec:java -Dexec.mainClass="org.master.diploma.git.GitGraphComparisonApp" -Dexec.args="<student_repo_path> <reference_repo_path> [output_html_path]"
```

### 2. Visualization
The generated report (`report.html` by default) uses `vis-network.js` to provide:
- Side-by-side interactive Git graphs.
- Color-coded nodes (Green: Identical, Orange: Modified, Red: Extra).
- Detailed commit information on click, including metadata and diffs.

## Workflow

1. **Identify Repositories**: Ensure you have the absolute paths to the student and reference repositories.
2. **Run Comparison**: Execute the comparison tool using the provided script.
3. **Review Report**: The tool will attempt to open the report in your default browser. If it fails, manually open the generated HTML file.

## Resources

### scripts/compare.ps1
A helper script to run the comparison.
Usage: `./compare.ps1 <student_repo_path> <reference_repo_path> [output_path]`

### references/report_logic.md
Details on how `HtmlReportGenerator` injects data into the template.
