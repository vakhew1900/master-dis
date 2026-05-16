# INFO.md - Supplementary Project Information

This file contains additional details and context regarding recent development tasks and decisions that are less critical for daily operations but provide valuable insights into the project's evolution.

## 🐛 Bug Fix Details: `SimpleLabelGenerator.java` - `findNumber` Method

**Description:**
A logical bug was identified and fixed in the `findNumber` method within `SimpleLabelGenerator.java`. Previously, this method had inconsistent behavior when assigning unique numbers to identical `GitLabelInfo` instances.

**Original Behavior:**
When `findNumber(labelInfo)` was called for a `labelInfo` that was not yet present in `labelNumbers`:
1.  The `numberCounter` (e.g., `1`) was used to `put` into the map (`labelNumbers.put(labelInfo, 1)`).
2.  `numberCounter` was then incremented (e.g., `numberCounter` became `2`).
3.  The method returned the *incremented* `numberCounter` (e.g., `2`).
Consequently, for the same `labelInfo`:
-   The first call returned `2` (the incremented counter).
-   Subsequent calls (where `labelInfo` was already in the map) returned `1` (the value stored in the map).
This inconsistency led to non-deterministic label numbering, which could affect comparison results.

**Fixed Behavior:**
The method now correctly assigns and returns the *same* unique number for identical `GitLabelInfo` instances.
1.  When `findNumber(labelInfo)` is called for a new `labelInfo`:
    -   The current `numberCounter` value is stored in a temporary variable (`number`).
    -   `numberCounter` is then incremented.
    -   The `labelInfo` is mapped to the `number` (from the temporary variable).
    -   The method returns this `number`.
This ensures that the value stored in the map and the value returned by the first call are identical, resolving the inconsistency.

## 🚦 Rationale for Node Severity Statuses

The `severity` field in `GitGraphDto.NodeDto` was introduced to provide clear visual cues about the comparison status of individual commit nodes between two Git graphs. The chosen statuses and their meanings are:

-   **`IDENTICAL`**:
    -   **Meaning:** The node exists in both graphs and its content (labels/diffs) is exactly the same.
    -   **Purpose:** Indicates perfect matches, which is crucial for identifying unchanged parts of the graph.

-   **`MODIFIED`**:
    -   **Meaning:** The node exists in both graphs (it's a matched node), but there are differences in its associated labels (extra or missing labels).
    -   **Purpose:** Highlights changes in the content of a commit, even if the commit itself is matched structurally. This is vital for understanding content-level modifications.

-   **`EXTRA`**:
    -   **Meaning:** The node is present in the current graph being displayed (either `firstGraph` or `secondGraph`) but has no corresponding matched node in the other graph.
    -   **Purpose:** Identifies additions or deletions of commits between the two graphs. For instance, if viewing `firstGraph`, an `EXTRA` node means it was "removed" or "not present" in `secondGraph`. If viewing `secondGraph`, an `EXTRA` node means it was "added" or "not present" in `firstGraph`. This provides context about structural differences.

## 🔬 Subgraph Method Executor Implementations

### `BranchMethodExecutor`

**Approach:**
The `BranchMethodExecutor` employs a branch-centric strategy to find the Maximum Common Transitive Subgraph (MCTS).
1.  **Branch Extraction:** It first identifies all possible root-to-leaf paths (branches) in both input graphs.
2.  **Branch Matching:** It then generates all possible pairs of branches (one from each graph) and calculates a `percentageMatch` based on the similarity of their contained labels. These matches are sorted by similarity.
3.  **Filtered Matching:** A filtering step ensures that each individual branch is used in at most one match, prioritizing the highest similarity matches.
4.  **LCS Comparison:** For each selected `BranchMatch`, the Longest Common Subsequence (LCS) algorithm (`BranchLCSHelper.findBranchLCS`) is applied to find the common part between the two branches.
5.  **Result Aggregation:** The results from individual branch LCS comparisons are aggregated to form the overall `GraphCompareResult`, accounting for vertices that were matched and those that were not.

**Key Components:**
-   `getAllBranches()`: A recursive method that performs a Depth-First Search (DFS) to enumerate all branches in a graph.
-   `getBranchMatches()`: A method that pairs branches from two graphs, calculates their label similarity, sorts them, and filters them to produce a unique set of best matches.
-   `BranchMatch` (inner class): Stores a pair of branches and their calculated `percentageMatch`. Its constructor computes the similarity by comparing multisets of labels.

## 🧪 Observations from Test Methods (`src/test/java/org/master/diploma/git/graph/method/`)

### General Structure:
All specific method executor tests (e.g., `BranchMethodExecutorTest`, `BruteForceMethodExecutorTest`) extend an abstract base class, `MethodExecutorTest`. This base class provides:
-   A common setup for loading test graphs from JSON files.
-   A `compareGraphTest` method that executes the chosen subgraph comparison algorithm and asserts its results against a pre-defined expected outcome.
-   Logging and metric collection (`Metrics` class) to evaluate algorithm performance.
-   The `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` annotation ensures a single test instance is used for all tests in a class, which is suitable for `@AfterAll` metrics aggregation.

### Test Organization:
Tests are organized using `@Nested` classes (e.g., `BasicTest`, `MediumTest`, `BigGraphTest`, `DAGTest`, `AdditionalTest`, `BambooTest`). Each nested class corresponds to a category of test graphs and defines specific test cases (`@Test` methods) that call `compareGraphTest` with different JSON input files. This modular structure helps in testing various graph topologies and scenarios systematically.

### Data Loading:
Test graphs and their expected comparison results are loaded from JSON files located in `src/test/resources/graph/{category}/graph/` and `src/test/resources/graph/{category}/result/` respectively. The `readJson` and `readGraph`/`readGraphCompareResult` helper methods handle this deserialization.

### Specific `TODO`s and Observations:
-   **`BruteForceMethodExecutor.java`:** Contains a `//todo поменять` comment next to `res.fillLFinaLabelError(first, second);`. This might indicate a potential area for improvement or recheck in the finalization of label error calculation for the brute-force method.
-   **`UniqueLabelMethodExecutor.java`:** The `getAllChildren` recursive method has a `//todo если в графе могут быть циклы то добавить used` comment. This suggests that the current implementation might not correctly handle graphs with cycles and could lead to infinite loops if not addressed. This method is crucial for building parent/child relationships, and cycles could break its logic.
-   **`DpMethodExecutorTest.java`:** One of the tests (`squashTest`) in the `DAGTest` nested class has a comment `//TODO DpMEthod тоже дает правильный ответ, немного отличный от текущего`. This indicates that the DP method might produce a *valid but different* common subgraph compared to what's currently expected for that specific test case, suggesting there might be multiple valid MCTS results or a slight difference in how the DP method defines "optimal".