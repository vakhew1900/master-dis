# Report Generation Logic

The `HtmlReportGenerator` (implements `ReportGenerator`) creates an interactive HTML report by:
1.  **Reading Resources**: It reads the following internal resources:
    -   `/report_template.html` (Main structure)
    -   `/report_style.css` (Visual styling)
    -   `/report_script.js` (Graph logic using vis-network.js)
2.  **Serializing Data**: It serializes the `GitComparisonResultDto` to JSON using `Gson`.
3.  **Injecting Data**: It performs simple string replacements in the template:
    -   `{{CSS}}` → Injects the CSS content.
    -   `{{JS}}` → Injects the JavaScript content.
    -   `{{DATA}}` → Injects the JSON comparison result.

The report includes:
-   **Student Graph**: Left-side visualization.
-   **Reference Graph**: Right-side visualization.
-   **Details Panel**: Bottom panel showing commit metadata and file diffs.
-   **Cross-Highlighting**: Clicking a node in one graph highlights its matched counterpart in the other graph if it exists.
