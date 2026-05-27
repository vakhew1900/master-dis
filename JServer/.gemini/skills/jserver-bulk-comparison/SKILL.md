# JServer Bulk Comparison Skill

This skill automates the process of running bulk comparisons between student and reference Git repositories.

## Description
The main goal is to execute the `run_bulk_comparison.py` script, which reads repository pairs from a `pairs.json` file and generates HTML comparison reports for each pair.

## Workflow

1.  **Confirm Intent**: Ask the user if they want to run the bulk comparison analysis.
2.  **Locate Script**: The primary script to execute is located at `src/main/resources/scripts/run_bulk_comparison.py`.
3.  **Check Prerequisites**:
    *   The script depends on `src/main/resources/scripts/pairs.json`. If this file is missing, the script will fail.
    *   The script requires the project to be packaged into a fat JAR. Verify that `target/JServer-1.0-SNAPSHOT-jar-with-dependencies.jar` exists. If not, run `mvn clean package -DskipTests` using the full path to `mvn.cmd`.
4.  **Execute**: Run the script using a robust method that supports Unicode paths on Windows. The recommended command is:
    ```bash
    python src/main/resources/scripts/run_bulk_comparison.py
    ```
5.  **Output**: The script will create a main `bulk_reports` directory. Inside, it will generate a separate folder for each comparison pair, named `example-№-X`, containing the `report.html`.
6.  **Troubleshooting**:
    *   **Java Version Error**: The script is configured to use a specific Java 17+ runtime. If an `UnsupportedClassVersionError` occurs, verify the `java_bin` path in the script points to a valid Java 17+ executable.
    *   **JAR Not Found**: If the JAR is not found, it means the project has not been built. Run the Maven packaging command.
    *   **JSON Not Found**: Ensure `pairs.json` is present in the `scripts` directory.
