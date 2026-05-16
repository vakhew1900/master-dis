---
name: jserver-frontend
description: Guidelines and standards for JServer frontend development, including HTML, CSS, and vis-network.js graph visualizations. Use when modifying or creating reports, styling components, or configuring graph layouts.
---

# JServer Frontend Skill

This skill provides specialized guidance for developing and maintaining the JServer frontend components, particularly the interactive Git graph reports.

## UI Standards

- **Language:**
    - Always use **Russian** for user-facing status labels (legend, severity names, tooltips).
    - In two-graph comparison view, use **"Текущая версия"** (Current Version) for the first graph and **"Целевая версия"** (Target Version) for the second graph.
    - **Exception:** Always use **English** for technical commit metadata labels in the details panel ("Commit Details", "Message", "Author", "Date").
- **Theme:** Use the dark theme defined in `common_style.css`.
- **Interactivity:**
    - A drag action that starts on a node **must not** pan the view. The view should only pan when dragging the background. Implement this using `dragStart` and `dragEnd` event handlers to temporarily disable `dragView`.
    - Use subtle animations (e.g., legend opening/closing).
    - Show dashed lines for moved nodes only in `merged_graph`.

## Graph Visualization (vis-network.js)

### Layout Configuration
- **Direction:** Use `DU` (Down to Up) for hierarchical layouts.
- **Density:** 
    - `nodeSpacing`: 80.
    - `levelSeparation`: 80.
- **Edge Minimization:** Always enable `edgeMinimization` and `parentCentralization`.

### Node Styling
- **Shape:** Use `dot` for nodes.
- **Labels (`getShortHash`):**
    - For single hashes, shorten to 7 characters.
    - For merged hashes (containing `/`), shorten each part to 4 characters (e.g., `abcd / efgh`).
- **`EXTRA` Node Styling:**
    - **Graph:** Use a `ctxRenderer` to draw a thick, red, diagonal cross over the node.
    - **Legend:** Use CSS pseudo-elements (`::before`, `::after`) on the `.severity-EXTRA` class to render a matching red cross.
- **`MISSED` Node Styling:**
    - The node's background and highlight background must be **opaque** to hide graph edges passing behind them. Use a solid color from the theme palette (e.g., `--panel-bg`).
- **Tooltips:** Use `createTooltip` for rich HTML tooltips that match the details panel.
- **Colors:** Distinguish between student and reference nodes in `MOVABLE` state using `MOVABLE_STUDENT` and `MOVABLE_REFERENCE` constants.

### Edge Styling
- **Visibility:** Use `--edge-color` (`#555`) and width 2 for clear visibility.
- **Smoothness:** Use `curvedCW` for `merged_graph` and `two_compare_graph`.

## Component Patterns

### Collapsible Legend
- Use a `<details>` element with custom CSS for smooth opening/closing.
- Animate `opacity` and `transform` of the content.

### Commit Details Panel
- Use `commit-metadata-toggle` for expandable commit metadata.
- Syntax highlight diffs using `renderDiffs`.

## Prohibitions
- **No TailwindCSS:** Use Vanilla CSS as per project mandates.
- **No long hashes in graph labels:** Adhere to the rules in the `getShortHash` section.
