# TODO: Improvements for Two Graph Comparison Report

## Visualization & Interactivity
- [ ] **Interactive Legend**: Make legend items clickable to highlight or filter nodes with the corresponding severity on both graphs.
- [ ] **Graph Controls**: Add UI buttons for Zoom In, Zoom Out, and "Fit to Screen" (Center view).
- [ ] **Search Functionality**: Implement a search bar to find commits by hash, message, or author.
- [ ] **Path Highlighting**: When a node is selected, highlight its direct ancestors and descendants to visualize branch structure.
- [ ] **Collapsible Diffs**: Add a global toggle to expand/collapse all diff sections in the details panel.

## UI/UX Refinements
- [ ] **Comparison Statistics**: Add a summary card at the top showing match percentage, number of identical vs. modified commits, etc.
- [ ] **Responsive Design**: Ensure the graph containers and details panel resize gracefully on smaller screens (currently fixed height).
- [ ] **Enhanced Diff States**: Further refine the visual distinction between `CORRECT`, `MISSED`, and `EXTRACT` lines (e.g., adding line numbers or file names if available).
- [ ] **Tooltips**: Add informative tooltips for the legend and various UI buttons.

## Technical
- [ ] **Export to PDF/PNG**: Add functionality to export the current view or the entire report as a static file.
- [ ] **Deep Linking**: Allow sharing a link to the report that automatically selects a specific commit.
