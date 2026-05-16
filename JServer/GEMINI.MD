# GEMINI.MD - Project Overview

This file provides foundational context for AI assistants working on the **JServer** project.

## 📌 Project Summary
**JServer** is a Java-based application that serves as supplementary material for the scientific paper: **"Methods of Finding the Maximum Common Transitive Subgraph: Experimental Comparison"**. It includes implementations and evaluations of various algorithms (including LLM-based) to find the Maximum Common Transitive Subgraph (MCTS), specifically applied in a tutor system for teaching Git.

## 🛠 Tech Stack
- **Language:** Java 17
- **Build Tool:** Maven
- **Core Libraries:**
    - **JGit:** For working with Git repositories.
    - **Gson:** For JSON serialization/deserialization.
    - **OpenAI / Google GenAI SDKs:** For LLM-based MCTS search.
    - **JUnit 5:** For testing.
    - **Lombok:** For reducing boilerplate.
    - **Guava:** For common utility functions.
    - **Log4j2:** For logging.

## 🏗 Engineering Standards
- **Git History Preservation**: All file operations (moving, renaming, deleting) MUST be performed using Git commands (`git mv`, `git rm`) to ensure that version history and file relationships are preserved across refactorings.
- **DTO Construction**: Data Transfer Objects (DTOs) should remain clean POJOs. All complex construction logic, including conversion and post-processing, must be encapsulated in specialized **Builder** classes.

## 🧬 Key Concepts

- **MCTS (Maximum Common Transitive Subgraph):** The central problem being solved.
- **Tutor System:** A system designed to teach students Git, using MCTS to compare student work with expected results.
- **Git Graph Analysis:** Representing Git history as graphs and analyzing their structure.
- **Method Executors:** Various implementations for MCTS search (Brute Force, Branch and Bound, DP, LLM).
- **API Data Models (DTOs):**
    - **`GitGraphDto`**: Data Transfer Object for representing a single Git commit graph. It includes `NodeDto` for individual commits and `LinkDto` for parent-child relationships. Designed for API responses and visualization.
    - **`GitComparisonResultDto`**: Encapsulates the results of a comparison between two Git graphs, containing two `GitGraphDto` instances (for the first and second graphs) and the `GraphCompareResult`.
    - **Node Comparison Statuses**: `NodeDto` now includes a `severity` field to indicate the comparison status of each commit node:
        -   **`IDENTICAL`**: The node is matched in both graphs and has identical labels.
        -   **`MODIFIED`**: The node is matched but has differences in its labels (diffs).
        -   **`EXTRA`**: The node exists in one graph but has no matching counterpart in the other.

## ⚙️ Recent Changes & Refinements

- **`SimpleLabelGenerator.java`**:
    -   Implemented support for English and Russian Javadoc comments within the same blocks for better localization.
    -   A logical bug in the `findNumber` method that caused inconsistent label numbering for identical content was identified and fixed to ensure deterministic label assignment.
- **`GitGraphDto.java`**:
    -   Refactored the `from` method to improve clarity and remove duplication in determining node severity, using a new private helper method `getNodeSeverity`.
- **`HtmlReportGenerator.java` & `report_template.html`**:
    -   Implemented a prototype for visualizing the comparison results.
    -   Used **vis-network.js** for interactive graph rendering.
    -   Supports side-by-side comparison of Student and Reference graphs with automatic node highlighting and detailed commit metadata display.

## 📊 Visualization & Reporting
The system supports generating interactive HTML reports to visualize the MCTS results:
- **Visual Feedback**: Nodes are color-coded based on their `severity` (`IDENTICAL`, `MODIFIED`, `EXTRA`).
- **Interactive Links**: Selecting a node in one graph automatically highlights its counterpart in the other graph (if a match exists).
- **Commit Details**: A dedicated panel shows metadata (hash, author, date, message) and a list of modified files (diffs).

## 🛤 Roadmap
1.  **Phase 1: CLI Prototype (Current)**: Refine the CLI tool to take two repository paths, perform MCTS comparison, and generate/open the HTML report automatically.
2.  **Phase 2: Backend Services**: Migrate core logic into a Spring Boot application, exposing REST endpoints for graph comparison.
3.  **Phase 3: Frontend (React)**: Replace the static HTML report with a full-featured React application for a more dynamic and responsive user experience.

## 📂 Project Structure
- `src/main/java`: Core application logic (MCTS algorithms, Git integration).
    - `org.master.diploma.git.graph`: Base classes for graph theory (`Graph`, `Vertex`, `SimpleGraph`, `SimpleVertex`).
    - `org.master.diploma.git.graph.label`: Classes for graphs with labels (`LabelGraph`, `LabelVertex`).
    - `org.master.diploma.git.git.model`: Git-specific implementations (`CommitGraph`, `Commit`).
- `src/main/resources`: Static assets and report templates.
- `src/test/java`: Test suites for algorithm validation.
- `src/test/resources/graph`: Extensive test datasets in JSON format.
- `docs/`: Detailed documentation on algorithms, test results, and the tutor system.

## ⚙️ Configuration Files
- `src/main/resources/application.properties`: Main Spring Boot configuration. Includes database settings (H2/PostgreSQL), MinIO credentials, and OpenAPI (Swagger) paths.
- `src/main/resources/application.properties.example`: A template for `application.properties`. **Must** be kept in sync with the main config (without real secrets).
- `src/main/resources/log4j2.xml`: Logging configuration for the application.
- `src/main/java/org/master/diploma/backend/config/`:
    - `SecurityConfig.java`: Spring Security rules, password encoder, and route permissions.
    - `MinioConfig.java`: Configuration for the `MinioClient` bean.
    - `GraphMethodConfig.java`: Registry of MCTS comparison algorithms and report builders.
    - `Constants.java`: Centralized storage for API routes and MinIO bucket names.

## 🏗 Graph & Vertex Logic
- **`Vertex.canRelate(Vertex)`**: The primary method for determining if two vertices are similar enough to be matched. In `Commit` (via `LabelVertex`), it checks if they share at least one label (diff hunk).
- **`Graph.removeVertex(int)`**: Implementation in `SimpleGraph` preserves transitive relations by connecting all parents of the removed node directly to all its children.
- **`SimpleGraph.getAdjacencyMatrix()`**: Provides raw access to the graph structure where keys are vertex numbers and values are sets of child vertex numbers.

## 📝 User Notes
*(User to add more specific project details, rules, or preferences here)*
